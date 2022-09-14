/*
 This file is part of Privacy Friendly Sketching.

 Privacy Friendly Sketching is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.

 Privacy Friendly Sketching is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Privacy Friendly Sketching. If not, see <http://www.gnu.org/licenses/>.
 */
package org.secuso.privacyfriendlysketching.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SupportFactory;

import org.secuso.privacyfriendlysketching.helpers.EncryptionHelper;


/**
 * Handles the creation of the database and makes sure it is existent and consistent.
 */

@Database(entities = {Sketch.class}, version = 1)
public abstract class SketchingRoomDB extends RoomDatabase {

    public static final String DATABASENAME = "sketchingroomdb";

    public abstract SketchDAO sketchDao();

    private static volatile SketchingRoomDB INSTANCE;

    public static SketchingRoomDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SketchingRoomDB.class) {
                if (INSTANCE == null) {
                    SupportFactory factory = new SupportFactory(SQLiteDatabase.getBytes(EncryptionHelper.loadPassPhrase(context)), new SQLiteDatabaseHook() {
                        @Override
                        public void preKey(SQLiteDatabase database) {
                        }

                        @Override
                        public void postKey(SQLiteDatabase database) {
                            database.rawExecSQL("PRAGMA cipher_compatibility = 3;");
                        }
                    });
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), SketchingRoomDB.class, DATABASENAME).openHelperFactory(factory).build();
                }
            }
        }
        return INSTANCE;
    }

}

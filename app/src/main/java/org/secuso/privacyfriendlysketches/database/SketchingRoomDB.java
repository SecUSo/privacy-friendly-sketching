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
package org.secuso.privacyfriendlysketches.database;

import android.content.Context;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.commonsware.cwac.saferoom.SafeHelperFactory;

import org.secuso.privacyfriendlysketches.helpers.EncryptionHelper;


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
                    SafeHelperFactory shf = new SafeHelperFactory(EncryptionHelper.loadPassPhrase(context));
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), SketchingRoomDB.class, DATABASENAME).openHelperFactory(shf).build();
                }
            }
        }
        return INSTANCE;
    }

}

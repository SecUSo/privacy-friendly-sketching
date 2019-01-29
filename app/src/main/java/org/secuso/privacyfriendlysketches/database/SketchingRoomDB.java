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

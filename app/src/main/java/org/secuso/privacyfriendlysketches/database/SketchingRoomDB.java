package org.secuso.privacyfriendlysketches.database;

import android.content.Context;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;


/**
 * Created by enyone on 12/3/18.
 */

@Database(entities = {Sketch.class}, version = 1)
public abstract class SketchingRoomDB extends RoomDatabase {

    public abstract SketchDAO sketchDao();

    private static volatile SketchingRoomDB INSTANCE;

    public static SketchingRoomDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SketchingRoomDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), SketchingRoomDB.class, "sketchingroomdb").build();
                }
            }
        }
        return INSTANCE;
    }

}

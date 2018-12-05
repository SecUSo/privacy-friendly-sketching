package org.secuso.privacyfriendlysketches.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Created by enyone on 12/3/18.
 */

@Database(entities = {Sketch.class}, version = 1)
abstract class SketchingRoomDB extends RoomDatabase {

    public abstract SketchDAO sketchDao();

}

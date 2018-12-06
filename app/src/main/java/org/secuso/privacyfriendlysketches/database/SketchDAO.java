package org.secuso.privacyfriendlysketches.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

/**
 * Created by enyone on 12/5/18.
 */

@Dao
public interface SketchDAO {
    @Insert
    void insertSketch(Sketch... sketches);

    @Query("SELECT * FROM sketch")
    Sketch[] getAllSketches();

    @Query("SELECT * FROM sketch WHERE id = :id")
    Sketch getSketchById(int id);

    @Query("SELECT * FROM sketch WHERE description = :description")
    Sketch[] getSketchByDescription(String description);

    @Query("SELECT * FROM sketch WHERE bitmap = :bitmap")
    Sketch[] getSketchByBitmap(byte[] bitmap);

    @Query("SELECT COUNT(*) FROM sketch")
    int getSketchCount();

    @Update
    void updateSketch(Sketch... sketches);

    @Delete
    void deleteSketch(Sketch... sketches);

    @Query("DELETE FROM sketch")
    void deleteAllSketches();

}

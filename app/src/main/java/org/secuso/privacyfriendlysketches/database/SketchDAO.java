package org.secuso.privacyfriendlysketches.database;

import android.graphics.Bitmap;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

/**
 * Created by enyone on 12/5/18.
 */

@Dao
public interface SketchDAO {
    @Insert
    void insertSketch(Sketch... sketches);

    @Query("SELECT * FROM sketch")
    Sketch getAllSketches();

    @Query("SELECT * FROM sketch WHERE id = :id")
    Sketch getSketchById(int id);

    @Query("SELECT * FROM sketch WHERE description = :description")
    Sketch getSketchByDescription(String description);

    @Query("SELECT * FROM sketch WHERE bitmap = :bitmap")
    Sketch getSketchByBitmap(Bitmap bitmap);

    @Query("SELECT COUNT(*) FROM sketch")
    int getSketchCount();

    @Update
    void updateSketch(Sketch... sketches);

    @Delete
    void deleteSketch(Sketch... sketches);

    @Query("DELETE FROM sketch")
    void deleteAllSketches();

}

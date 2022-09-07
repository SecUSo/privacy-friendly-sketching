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

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

/**
 * Represents the Data Access Objects for the ROOM Database.
 */

@Dao
public interface SketchDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertSketch(Sketch sketch);

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

    @Query("SELECT id FROM sketch ORDER BY id DESC")
    int[] getSketchIds();

    @Update
    void updateSketch(Sketch... sketches);

    @Delete
    void deleteSketch(Sketch... sketches);

    @Query("DELETE FROM sketch WHERE id = :id")
    void deleteSketch(int id);

    @Query("DELETE FROM sketch")
    void deleteAllSketches();

}

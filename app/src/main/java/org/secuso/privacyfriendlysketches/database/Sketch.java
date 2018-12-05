package org.secuso.privacyfriendlysketches.database;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by enyone on 12/5/18.
 */

@Entity(primaryKeys = {"id"}, tableName = "sketch")
public class Sketch {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public Bitmap bitmap;
    public String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getBitmaph() {
        return this.bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}

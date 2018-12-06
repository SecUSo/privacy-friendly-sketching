package org.secuso.privacyfriendlysketches.database;

import android.graphics.Bitmap;

import org.secuso.privacyfriendlysketches.helpers.Utility;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by enyone on 12/5/18.
 */

@Entity(tableName = "sketch")
public class Sketch {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public byte[] bitmap;
    public String description;

    public Sketch(Bitmap bitmap, String description) {
        this.bitmap = Utility.bitmapToBlob(bitmap);
        this.description = description;
    }

    public Sketch(byte[] bitmap, String description) {
        this.bitmap = bitmap;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getBitmapBlob() {
        return this.bitmap;
    }

    public Bitmap getBitmap() {
        return Utility.blobToBitmap(this.bitmap);
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}

package org.secuso.privacyfriendlysketches.database;

import android.graphics.Bitmap;

import org.secuso.privacyfriendlysketches.helpers.Utility;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.divyanshu.draw.widget.MyPath;
import com.divyanshu.draw.widget.PaintOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;

/**
 * Created by enyone on 12/5/18.
 */

@Entity(tableName = "sketch")
public class Sketch implements SketchData {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public byte[] bitmap;
    public byte[] paths;
    public String description;

    public Sketch(Bitmap bitmap, LinkedHashMap<MyPath, PaintOptions> paths, String description) {
        if (bitmap != null)
            this.bitmap = Utility.bitmapToBlob(bitmap);
        else
            this.bitmap = new byte[0];
        this.paths = Utility.serializePaths(paths);
        this.description = description;
    }

    public Sketch(byte[] bitmap, byte[] paths, String description) {
        this.bitmap = bitmap;
        this.paths = paths;
        this.description = description;
    }

    public void setPaths(LinkedHashMap<MyPath, PaintOptions> values) {
        this.paths = Utility.serializePaths(values);
    }

    public LinkedHashMap<MyPath, PaintOptions> getPaths() {
        return Utility.deserializePaths(this.paths);
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

package org.secuso.privacyfriendlysketches.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.divyanshu.draw.widget.MyPath;
import com.divyanshu.draw.widget.PaintOptions;

import org.secuso.privacyfriendlysketches.helpers.Utility;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents the implementation of a single Sketch consisting of an id, a bitmap, a path
 * and a description.
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
        if (this.paths.length == 0)
            return null;
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
        if (this.bitmap.length == 0)
            return null;
        return Utility.blobToBitmap(this.bitmap);
    }

    public Bitmap getFullImage(int width, int height) {
        Bitmap background = this.getBitmap();
        LinkedHashMap<MyPath, PaintOptions> paths = getPaths();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        if (background != null)
            canvas.drawBitmap(background, null, canvas.getClipBounds(), null);

        if (paths != null) {
            Iterator<Map.Entry<MyPath, PaintOptions>> itr = paths.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry<MyPath, PaintOptions> pair = itr.next();

                Paint mPaint = new Paint();
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeJoin(Paint.Join.ROUND);
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                mPaint.setAntiAlias(true);
                mPaint.setColor(pair.getValue().getColor());
                mPaint.setAlpha(pair.getValue().getAlpha());
                mPaint.setStrokeWidth(pair.getValue().getStrokeWidth());

                canvas.drawPath(pair.getKey(), mPaint);
            }
        }

        return bitmap;
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

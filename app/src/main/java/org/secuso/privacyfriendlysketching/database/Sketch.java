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

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.divyanshu.draw.widget.MyPath;
import com.divyanshu.draw.widget.PaintOptions;

import org.secuso.privacyfriendlysketching.helpers.Utility;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents the implementation of a single Sketch consisting of an id, a bitmap, a path
 * and a description.
 */

@Entity(tableName = "sketch")
public class Sketch {
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

        RectF sourceRect = new RectF(0.f, 0.f, width, height );
        RectF targetRect = new RectF( 0.f, 0.f, 0.f, 0.f);
        Matrix transform = new Matrix();

        if (paths != null) {
            for (Map.Entry<MyPath, PaintOptions> pair : paths.entrySet()) {
                float size = pair.getValue().getStrokeWidth();
                RectF bounds = pair.getKey().getBounds();
                bounds.left -= size;
                bounds.right += size;
                bounds.top -= size;
                bounds.bottom += size;
                if (targetRect.left > bounds.left)
                    targetRect.left = bounds.left;
                if (targetRect.right < bounds.right)
                    targetRect.right = bounds.right;
                if (targetRect.top > bounds.top)
                    targetRect.top = bounds.top;
                if (targetRect.bottom < bounds.bottom)
                    targetRect.bottom = bounds.bottom;
            }
        }

        transform.setRectToRect(targetRect, sourceRect, Matrix.ScaleToFit.CENTER);

        if (background != null) {
            RectF backgroundRect = new RectF();
            backgroundRect.left = - background.getWidth() / 2.f;
            backgroundRect.right = background.getWidth() / 2.f;
            backgroundRect.top = - background.getHeight() / 2.f;
            backgroundRect.bottom = + background.getHeight() / 2.f;
            if (background.getHeight() == 1 && background.getHeight() == 1)
                backgroundRect.set(canvas.getClipBounds());
            else
                transform.mapRect(backgroundRect);

            canvas.drawBitmap(background, null, backgroundRect, null);
        }

        canvas.setMatrix(transform);

        if (paths != null) {
            for (Map.Entry<MyPath, PaintOptions> pair : paths.entrySet()) {
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

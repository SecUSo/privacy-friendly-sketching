package org.secuso.privacyfriendlysketches.database;

import android.graphics.Bitmap;

public interface SketchData {
    int getId();
    Bitmap getBitmap();
    String getDescription();
}

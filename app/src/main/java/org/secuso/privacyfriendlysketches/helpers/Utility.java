package org.secuso.privacyfriendlysketches.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by enyone on 12/6/18.
 */

public class Utility {

    public static byte[] bitmapToBlob(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean result = bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        if (!result)
            Log.e("Sketching","Cannot convert bitmap to compressed array.");
        byte[] blob = baos.toByteArray();
        return blob;
    }

    public static Bitmap blobToBitmap(byte[] blob) {
        return BitmapFactory.decodeByteArray(blob, 0, blob.length);
    }
}

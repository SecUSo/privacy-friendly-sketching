package org.secuso.privacyfriendlysketches.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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
 * Created by enyone on 12/6/18.
 */

public class Utility {

    public static byte[] serializePaths(LinkedHashMap<MyPath, PaintOptions> paths) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(paths);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    public static LinkedHashMap<MyPath, PaintOptions> deserializePaths(byte[] lines) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(lines);
             ObjectInput in = new ObjectInputStream(bis)) {
            return (LinkedHashMap<MyPath, PaintOptions>)in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

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

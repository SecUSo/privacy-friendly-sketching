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
package org.secuso.privacyfriendlysketching.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
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
 * A class containing static methods that are used to help anywhere inside the project.
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
        Log.i("Deserialization", String.format("Deserializing %d path bytes.", lines.length));
        try (ByteArrayInputStream bis = new ByteArrayInputStream(lines);
             ObjectInput in = new ObjectInputStream(bis)) {
            return (LinkedHashMap<MyPath, PaintOptions>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] bitmapToBlob(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean result = bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        if (!result)
            Log.e("Sketching", "Cannot convert bitmap to compressed array.");
        byte[] blob = baos.toByteArray();
        return blob;
    }

    public static Bitmap blobToBitmap(byte[] blob) {
        Log.i("Deserialization", String.format("Deserializing %d bitmap bytes.", blob.length));
        return BitmapFactory.decodeByteArray(blob, 0, blob.length);
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
}

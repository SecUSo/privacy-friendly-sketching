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

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import org.secuso.privacyfriendlysketching.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

/**
 * This class helps encrypting and saving and decrypting and
 * loading the passphrase via the AndroidKeyStore
 */

public class EncryptionHelper {

    private final static String ANDROIDKEYSTORE = "AndroidKeyStore";

    public static void savePassPhrase(Context context, byte[] passphrase) {
        try {
            String keyAlias = context.getString(R.string.key_alias);
            String appName = context.getString(R.string.app_name);
            KeyStore store = KeyStore.getInstance(ANDROIDKEYSTORE);
            store.load(null);
            KeyStore.PrivateKeyEntry skEntry = (KeyStore.PrivateKeyEntry) store.getEntry(keyAlias, null);
            PublicKey pk = skEntry.getCertificate().getPublicKey();
            Cipher c = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            c.init(Cipher.ENCRYPT_MODE, pk);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CipherOutputStream cos = new CipherOutputStream(baos, c);
            cos.write(passphrase);
            cos.close();
            byte[] cipher = baos.toByteArray();
            String b64Cipher = Base64.encodeToString(cipher, Base64.DEFAULT);

            SharedPreferences sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE);
            sp.edit().putString(keyAlias, b64Cipher).apply();

            Log.i("ENCRYPTIONHELPER", "saving: " + b64Cipher);


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }

    public static char[] loadPassPhrase(Context context) {

        try {
            String keyAlias = context.getString(R.string.key_alias);
            String appName = context.getString(R.string.app_name);

            SharedPreferences sp = context.getSharedPreferences(appName, Context.MODE_PRIVATE);
            String retrieved = sp.getString(keyAlias, "nope");

            Log.i("ENCRYPTIONHELPER", "retrieved: " + retrieved);

            byte[] encryptedPassphrase = Base64.decode(retrieved, Base64.DEFAULT);

            KeyStore store = KeyStore.getInstance(ANDROIDKEYSTORE);
            store.load(null);
            KeyStore.PrivateKeyEntry skEntry = (KeyStore.PrivateKeyEntry) store.getEntry(keyAlias, null);
            if (skEntry == null)
                return null;
            PrivateKey sk = skEntry.getPrivateKey();
            Cipher c = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            c.init(Cipher.DECRYPT_MODE, sk);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CipherOutputStream cos = new CipherOutputStream(baos, c);
            cos.write(encryptedPassphrase);
            cos.close();
            byte[] plaintext = baos.toByteArray();

            String pass = "";
            for (int i = 0; i < plaintext.length; i++) {
                pass += Byte.toString(plaintext[i]);
            }
            Log.i("ENCRYPTIONHELPER", "Decrypted Passphrase: " + pass);

            char[] plaintextChar = new char[plaintext.length];
            for (int i = 0; i < plaintext.length; i++) {
                plaintextChar[i] = (char) plaintext[i];
            }

            return plaintextChar;

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        return null;

    }

}

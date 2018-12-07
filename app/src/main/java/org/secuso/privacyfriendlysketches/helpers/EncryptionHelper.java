package org.secuso.privacyfriendlysketches.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import org.secuso.privacyfriendlysketches.R;

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
 * Created by enyone on 12/5/18.
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

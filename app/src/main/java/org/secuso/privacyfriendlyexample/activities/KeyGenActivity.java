package org.secuso.privacyfriendlyexample.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.widget.TextView;

import org.secuso.privacyfriendlyexample.activities.helper.BaseActivity;
import org.secuso.privacyfriendlyexample.R;
import org.secuso.privacyfriendlyexample.helpers.EncryptionHelper;
import org.w3c.dom.Text;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

/**
 * Created by enyone on 11/19/18.
 */

public class KeyGenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keygen);

        keyGenClass keyGenClass = new keyGenClass();
        try {
            keyGenClass.doInBackground(new URL("http://bla.de"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Intent i = new Intent(KeyGenActivity.this, MainActivity.class);
        startActivity(i);

    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_keygen;
    }

    /**
     *
     */
    private class keyGenClass extends AsyncTask<URL, Integer, Long> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TextView tv = findViewById(R.id.keyGenInfoText);
            tv.setText("0%");
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);


        }

        @Override
        protected Long doInBackground(URL... urls) {
            Log.i("KEYGEN_ACTIVITY", "generating keys for later use..");

            //Generates a passphrase for the encrypted ROOM DB
            SecureRandom random = new SecureRandom();
            byte[] passphrase = random.generateSeed(20);

            //Generates a key to save the passphrase via Android keystore provider
            //Asymmetric Encryption which is supported on API 21
            KeyPairGenerator kpg = null;
            try {
                kpg = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                Calendar now = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 30);
                KeyPairGeneratorSpec kpgs = new KeyPairGeneratorSpec.Builder(getApplicationContext()).setAlias(getString(R.string.key_alias)).setSubject(new X500Principal("CN=" + getString(R.string.key_alias))).setSerialNumber(BigInteger.ONE).setStartDate(now.getTime()).setEndDate(end.getTime()).build();
                kpg.initialize(kpgs);
                KeyPair pair = kpg.generateKeyPair();

                EncryptionHelper.savePassPhrase(getApplicationContext(), passphrase);


            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
            if (kpg == null) {
                Log.i("KEYGEN_ACTIVITY", "keypairgenerator is null");
                System.exit(0);
            }

            try {
                KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
                ks.load(null);
                Enumeration<String> aliases = ks.aliases();
                while (aliases.hasMoreElements()) {
                    String alias = aliases.nextElement();
                    Log.i("KEYGEN_ACTIVITY", alias);
                }
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return new Long(0);
        }
    }
}


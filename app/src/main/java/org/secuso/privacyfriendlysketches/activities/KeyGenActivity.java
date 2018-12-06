package org.secuso.privacyfriendlysketches.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.util.Log;
import android.widget.TextView;

import org.secuso.privacyfriendlysketches.activities.helper.BaseActivity;
import org.secuso.privacyfriendlysketches.R;
import org.secuso.privacyfriendlysketches.helpers.EncryptionHelper;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.Enumeration;

import javax.security.auth.x500.X500Principal;

/**
 * Created by enyone on 11/19/18.
 */

public class KeyGenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keygen);

    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_keygen;
    }

    @Override
    protected void onResume() {
        super.onResume();
        keyGenClass keyGenClass = new keyGenClass();
        keyGenClass.execute();
    }

    /**
     *
     */
    private class keyGenClass extends AsyncTask<URL, Integer, Long> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TextView tv = findViewById(R.id.keyGenInfoText);

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

                //saves the generated and encrypted passphrase inside the shared preferences
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
            }

            //Create Room Database


            return new Long(0);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            Intent i = new Intent(KeyGenActivity.this, MainActivity.class);
            startActivity(i);
        }
    }
}


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
package org.secuso.privacyfriendlysketching.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.util.Log;
import android.widget.TextView;

import com.commonsware.cwac.saferoom.SQLCipherUtils;
import com.commonsware.cwac.saferoom.SafeHelperFactory;

import org.secuso.privacyfriendlysketching.activities.helper.BaseActivity;
import org.secuso.privacyfriendlysketching.R;
import org.secuso.privacyfriendlysketching.database.SketchingRoomDB;
import org.secuso.privacyfriendlysketching.helpers.EncryptionHelper;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

/**
 * This class contains an activity for the Keygeneration which displays
 * visual progress of the keys that are being generated in the background.
 * Also, this class checks the database and rekeys it, if needed.
 */

public class KeyGenActivity extends BaseActivity {

    TextView progressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keygen);

        this.progressText = findViewById(R.id.keyGenProgressText);
    }

    @Override
    protected int getNavigationDrawerID() {
        return -1;
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

            switch (values[0]) {
                case 0:
                    progressText.setText(getApplicationContext().getString(R.string.keygen_progresstext_1));
                    break;
                case 1:
                    progressText.setText(getApplicationContext().getString(R.string.keygen_progresstext_2));
                    break;
            }


        }

        @Override
        protected Long doInBackground(URL... urls) {
            Log.i("KEYGEN_ACTIVITY", "generating keys for later use..");

            //Generates a passphrase for the encrypted ROOM DB
            SecureRandom random = new SecureRandom();
            byte[] passphrase = random.generateSeed(20);
            publishProgress(0);


            //Generates a key to save the passphrase via Android keystore provider
            //Asymmetric Encryption which is supported on API 21
            KeyPairGenerator kpg = null;
            try {
                kpg = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                Calendar now = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 30);
                KeyPairGeneratorSpec kpgs = new KeyPairGeneratorSpec.Builder(getApplicationContext())
                        .setAlias(EncryptionHelper.PASSPHRASE_KEY_PREF_NAME)
                        .setSubject(new X500Principal("CN=" + EncryptionHelper.PASSPHRASE_KEY_PREF_NAME))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(now.getTime())
                        .setEndDate(end.getTime())
                        .build();
                kpg.initialize(kpgs);
                KeyPair pair = kpg.generateKeyPair();

                //saves the generated and encrypted passphrase inside the shared preferences
                EncryptionHelper.savePassPhrase(getApplicationContext(), passphrase);


            } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
            if (kpg == null) {
                Log.i("KEYGEN_ACTIVITY", "keypairgenerator is null");
            }
            publishProgress(1);

            //reset key in case keyGen is called again
            SQLCipherUtils.State dbstate = SQLCipherUtils.getDatabaseState(getApplicationContext(), SketchingRoomDB.DATABASENAME);
            Log.i("KEYGEN_ACTIVITY", dbstate.toString());

            if (dbstate.equals(SQLCipherUtils.State.UNENCRYPTED)) {
                Log.i("KEYGEN_ACTIVITY", "Database unencrypted, ecnrypting db..");
                try {
                    SQLCipherUtils.encrypt(getApplicationContext(), SketchingRoomDB.DATABASENAME, EncryptionHelper.loadPassPhrase(getApplicationContext()));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (dbstate.equals(SQLCipherUtils.State.ENCRYPTED)) {
                Log.i("KEYGEN_ACTIVITY", "DB Encrypted, rekeying..");

                SketchingRoomDB db = SketchingRoomDB.getDatabase(getApplication());
                SafeHelperFactory.rekey(db.getOpenHelper().getWritableDatabase(), EncryptionHelper.loadPassPhrase(getApplicationContext()));

            }

            return new Long(0);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            Intent i = new Intent(KeyGenActivity.this, GalleryActivity.class);
            startActivity(i);
        }
    }
}


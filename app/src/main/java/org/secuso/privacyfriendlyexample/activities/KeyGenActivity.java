package org.secuso.privacyfriendlyexample.activities;

import android.os.AsyncTask;
import android.os.Bundle;

import org.secuso.privacyfriendlyexample.activities.helper.BaseActivity;
import org.secuso.privacyfriendlyexample.R;

import java.net.URL;

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

    /**
     *
     */
    private class keyGenClass extends AsyncTask<URL, Integer, Long> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Long doInBackground(URL... urls) {
            return new Long(0);
        }
    }
}


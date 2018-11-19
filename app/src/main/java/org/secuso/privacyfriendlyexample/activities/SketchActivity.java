package org.secuso.privacyfriendlyexample.activities;

import android.os.Bundle;

import org.secuso.privacyfriendlyexample.R;
import org.secuso.privacyfriendlyexample.activities.helper.BaseActivity;

public class SketchActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch);
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_sketch;
    }
}

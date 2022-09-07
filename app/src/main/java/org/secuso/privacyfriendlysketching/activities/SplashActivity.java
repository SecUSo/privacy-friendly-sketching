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
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import org.secuso.privacyfriendlysketching.helpers.EncryptionHelper;
import org.secuso.privacyfriendlysketching.helpers.FirstLaunchManager;

/**
 * @author Karola Marky
 * @version 20161022
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent mainIntent = new Intent(SplashActivity.this, TutorialActivity.class);

        FirstLaunchManager firstStartPref = new FirstLaunchManager(this);

        if (firstStartPref.isFirstTimeLaunch() || EncryptionHelper.loadPassPhrase(getApplicationContext()) == null) {
            firstStartPref.initFirstTimeLaunch();
            mainIntent = new Intent(this, TutorialActivity.class);
        } else {
            mainIntent = new Intent(this, GalleryActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        startActivity(mainIntent);
        finish();
    }

}

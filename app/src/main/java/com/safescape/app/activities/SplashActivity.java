package com.safescape.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.safescape.app.MainActivity;
import com.safescape.app.R;
import com.safescape.app.utils.SharedPrefsManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Check if user is already logged in after delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLoginStatus();
            }
        }, SPLASH_DURATION);
    }

    private void checkLoginStatus() {
        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(this);

        Intent intent;
        if (prefsManager.isLoggedIn()) {
            // User is logged in, go to main screen
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // User not logged in, go to login screen
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish(); // Close splash screen
    }
}
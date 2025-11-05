package com.example.absensi_mahasiswa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences loginPrefs;
    private static final String LOGIN_PREFS_NAME = "LoginPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginPrefs = getSharedPreferences(LOGIN_PREFS_NAME, Context.MODE_PRIVATE);

        // This Activity now acts as a router or splash screen.
        // It checks the login status and navigates accordingly.
        Intent intent;
        if (loginPrefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            // If logged in, go directly to Dashboard
            intent = new Intent(MainActivity.this, DashboardActivity.class);
        } else {
            // If not logged in, go to the Login screen
            intent = new Intent(MainActivity.this, MainActivityLogin.class);
        }
        startActivity(intent);
        finish(); // Close this router activity
    }
}

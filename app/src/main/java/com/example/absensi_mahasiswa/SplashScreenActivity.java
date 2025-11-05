package com.example.absensi_mahasiswa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.absensi_mahasiswa.databinding.ActivitySplashScreenBinding;

public class SplashScreenActivity extends AppCompatActivity {

    private int waktu_loading = 2000; // Durasi splash screen
    private SharedPreferences loginPrefs;
    private static final String LOGIN_PREFS_NAME = "LoginPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashScreenBinding binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginPrefs = getSharedPreferences(LOGIN_PREFS_NAME, Context.MODE_PRIVATE);

        new Handler().postDelayed(() -> {
            Intent intent;
            // Cek status login dari SharedPreferences
            if (loginPrefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
                // Jika sudah login, langsung ke dasbor
                intent = new Intent(SplashScreenActivity.this, DashboardActivity.class);
            } else {
                // Jika belum, ke halaman login
                intent = new Intent(SplashScreenActivity.this, MainActivityLogin.class);
            }
            startActivity(intent);

            // Apply the custom transition animation
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            finish(); // Tutup splash screen agar tidak bisa kembali
        }, waktu_loading);
    }
}

package com.example.absensi_mahasiswa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivityLogin extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;

    private SharedPreferences loginPrefs;
    private static final String LOGIN_PREFS_NAME = "LoginPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginPrefs = getSharedPreferences(LOGIN_PREFS_NAME, Context.MODE_PRIVATE);

        emailEditText = findViewById(R.id.input_email);
        passwordEditText = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.login_button);

        // Load animations for input fields
        Animation slideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        Animation slideInRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        Animation slideInBottom = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);

        // Start animations
        emailEditText.startAnimation(slideInLeft);
        passwordEditText.startAnimation(slideInRight);
        loginButton.startAnimation(slideInBottom);

        loginButton.setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Email and password cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("ryan@gmail.com".equals(email) && "password".equals(password)) {
            SharedPreferences.Editor editor = loginPrefs.edit();
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.apply();

            Intent intent = new Intent(MainActivityLogin.this, DashboardActivity.class);
            startActivity(intent);
            
            // Apply the custom transition animation
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            finish(); // Close login activity
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}

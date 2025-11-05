package com.example.absensi_mahasiswa;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private Button btnAbsenMasuk, btnAbsenKeluar, btnLogout;
    private TableLayout tableLayout;

    private SharedPreferences absenPrefs, loginPrefs;
    private static final String ABSEN_PREFS_NAME = "AbsenPrefs";
    private static final String LOGIN_PREFS_NAME = "LoginPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_LAST_ABSEN_DATE = "lastAbsenDate";
    private static final String KEY_ABSEN_MASUK_TIME = "absenMasukTime";
    private static final String KEY_ABSEN_KELUAR_TIME = "absenKeluarTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_screen);

        absenPrefs = getSharedPreferences(ABSEN_PREFS_NAME, Context.MODE_PRIVATE);
        loginPrefs = getSharedPreferences(LOGIN_PREFS_NAME, Context.MODE_PRIVATE);

        btnAbsenMasuk = findViewById(R.id.btn_absen_masuk);
        btnAbsenKeluar = findViewById(R.id.btn_absen_keluar);
        btnLogout = findViewById(R.id.btn_logout);
        tableLayout = findViewById(R.id.tableLayout);

        updateUIBasedOnTodayAbsen();

        btnAbsenMasuk.setOnClickListener(v -> handleAbsenMasuk());
        btnAbsenKeluar.setOnClickListener(v -> handleAbsenKeluar());
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_logout, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        // Make the dialog background transparent to show the custom shape
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        Button btnYes = dialogView.findViewById(R.id.btn_yes);
        Button btnNo = dialogView.findViewById(R.id.btn_no);

        btnYes.setOnClickListener(v -> {
            performLogout();
            dialog.dismiss();
        });

        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void performLogout() {
        SharedPreferences.Editor editor = loginPrefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();

        Intent intent = new Intent(DashboardActivity.this, MainActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); 
    }

    // ... (rest of the attendance logic remains the same) ...

    private void handleAbsenMasuk() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String lastAbsenDate = absenPrefs.getString(KEY_LAST_ABSEN_DATE, null);

        if (todayDate.equals(lastAbsenDate)) {
            Toast.makeText(this, "Anda sudah absen masuk hari ini.", Toast.LENGTH_SHORT).show();
        } else {
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

            SharedPreferences.Editor editor = absenPrefs.edit();
            editor.putString(KEY_LAST_ABSEN_DATE, todayDate);
            editor.putString(KEY_ABSEN_MASUK_TIME, currentTime);
            editor.remove(KEY_ABSEN_KELUAR_TIME); 
            editor.apply();

            updateTable(todayDate, currentTime, "-");
            Toast.makeText(this, "Absen masuk berhasil direkam.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAbsenKeluar() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String lastAbsenDate = absenPrefs.getString(KEY_LAST_ABSEN_DATE, null);
        String absenMasukTime = absenPrefs.getString(KEY_ABSEN_MASUK_TIME, null);
        String absenKeluarTime = absenPrefs.getString(KEY_ABSEN_KELUAR_TIME, null);

        if (absenMasukTime == null || !todayDate.equals(lastAbsenDate)) {
            Toast.makeText(this, "Anda harus absen masuk terlebih dahulu hari ini.", Toast.LENGTH_SHORT).show();
        } else if (absenKeluarTime != null) {
            Toast.makeText(this, "Anda sudah absen keluar hari ini.", Toast.LENGTH_SHORT).show();
        } else {
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

            SharedPreferences.Editor editor = absenPrefs.edit();
            editor.putString(KEY_ABSEN_KELUAR_TIME, currentTime);
            editor.apply();

            updateTable(todayDate, absenMasukTime, currentTime);
            Toast.makeText(this, "Absen keluar berhasil direkam.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUIBasedOnTodayAbsen() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String lastAbsenDate = absenPrefs.getString(KEY_LAST_ABSEN_DATE, null);

        if (todayDate.equals(lastAbsenDate)) {
            String absenMasukTime = absenPrefs.getString(KEY_ABSEN_MASUK_TIME, "-");
            String absenKeluarTime = absenPrefs.getString(KEY_ABSEN_KELUAR_TIME, null);

            updateTable(todayDate, absenMasukTime, absenKeluarTime != null ? absenKeluarTime : "-");
        } else {
            clearTable();
        }
    }

    private void clearTable() {
        if (tableLayout.getChildCount() > 1) {
            tableLayout.removeViews(1, tableLayout.getChildCount() - 1);
        }
    }

    private void updateTable(String date, String masukTime, String keluarTime) {
        clearTable(); 

        TableRow newRow = new TableRow(this);

        TextView dateCell = new TextView(this);
        dateCell.setText(date);
        dateCell.setPadding(8, 8, 8, 8);
        dateCell.setGravity(Gravity.CENTER);
        dateCell.setTextColor(ContextCompat.getColor(this, R.color.black));

        TextView absenMasukCell = new TextView(this);
        absenMasukCell.setText(masukTime);
        absenMasukCell.setPadding(8, 8, 8, 8);
        absenMasukCell.setGravity(Gravity.CENTER);
        absenMasukCell.setTextColor(ContextCompat.getColor(this, R.color.black));

        TextView absenKeluarCell = new TextView(this);
        absenKeluarCell.setText(keluarTime);
        absenKeluarCell.setPadding(8, 8, 8, 8);
        absenKeluarCell.setGravity(Gravity.CENTER);
        absenKeluarCell.setTextColor(ContextCompat.getColor(this, R.color.black));

        newRow.addView(dateCell);
        newRow.addView(absenMasukCell);
        newRow.addView(absenKeluarCell);

        tableLayout.addView(newRow);
    }
}

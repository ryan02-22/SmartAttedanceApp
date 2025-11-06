package com.example.absensi_mahasiswa;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private Button btnAbsenMasuk, btnAbsenKeluar, btnLogout;
    private TableLayout tableLayout;

    private SharedPreferences loginPrefs;
    private static final String LOGIN_PREFS_NAME = "LoginPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private AbsenDao absenDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_screen);

        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        absenDao = db.absenDao();

        loginPrefs = getSharedPreferences(LOGIN_PREFS_NAME, Context.MODE_PRIVATE);

        btnAbsenMasuk = findViewById(R.id.btn_absen_masuk);
        btnAbsenKeluar = findViewById(R.id.btn_absen_keluar);
        btnLogout = findViewById(R.id.btn_logout);
        tableLayout = findViewById(R.id.tableLayout);

        loadAttendanceHistory();

        btnAbsenMasuk.setOnClickListener(v -> handleAbsenMasuk());
        btnAbsenKeluar.setOnClickListener(v -> handleAbsenKeluar());
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void handleAbsenMasuk() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Absen todayAbsen = absenDao.getByDate(todayDate);

        if (todayAbsen != null) {
            Toast.makeText(this, "Anda sudah absen masuk hari ini.", Toast.LENGTH_SHORT).show();
        } else {
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            Absen newAbsen = new Absen();
            newAbsen.tanggal = todayDate;
            newAbsen.jamMasuk = currentTime;
            absenDao.insert(newAbsen);

            loadAttendanceHistory();
            Toast.makeText(this, "Absen masuk berhasil direkam.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAbsenKeluar() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Absen todayAbsen = absenDao.getByDate(todayDate);

        if (todayAbsen == null) {
            Toast.makeText(this, "Anda harus absen masuk terlebih dahulu hari ini.", Toast.LENGTH_SHORT).show();
        } else if (todayAbsen.jamKeluar != null && !todayAbsen.jamKeluar.isEmpty()) {
            Toast.makeText(this, "Anda sudah absen keluar hari ini.", Toast.LENGTH_SHORT).show();
        } else {
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            todayAbsen.jamKeluar = currentTime;
            absenDao.update(todayAbsen);

            loadAttendanceHistory();
            Toast.makeText(this, "Absen keluar berhasil direkam.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAttendanceHistory() {
        while (tableLayout.getChildCount() > 1) {
            tableLayout.removeViewAt(1);
        }

        List<Absen> history = absenDao.getAll();
        for (int i = 0; i < history.size(); i++) {
            Absen absen = history.get(i);
            addTableRow(absen, i);
        }
    }

    private void addTableRow(Absen absen, int index) {
        TableRow newRow = new TableRow(this);
        // Apply zebra stripe
        if (index % 2 == 0) {
            newRow.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
        } else {
            newRow.setBackgroundColor(Color.WHITE);
        }

        TextView dateCell = createTableCell(absen.tanggal);
        TextView absenMasukCell = createTableCell(absen.jamMasuk);
        TextView absenKeluarCell = createTableCell(absen.jamKeluar != null ? absen.jamKeluar : "-");

        newRow.addView(dateCell);
        newRow.addView(absenMasukCell);
        newRow.addView(absenKeluarCell);

        tableLayout.addView(newRow);
    }

    private TextView createTableCell(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(ContextCompat.getColor(this, R.color.black));
        return textView;
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_logout, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialogView.findViewById(R.id.btn_yes).setOnClickListener(v -> {
            performLogout();
            dialog.dismiss();
        });
        dialogView.findViewById(R.id.btn_no).setOnClickListener(v -> dialog.dismiss());
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
}

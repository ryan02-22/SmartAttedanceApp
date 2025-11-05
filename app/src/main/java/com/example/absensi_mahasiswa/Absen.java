package com.example.absensi_mahasiswa;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "riwayat_absen")
public class Absen {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "tanggal")
    public String tanggal;

    @ColumnInfo(name = "jam_masuk")
    public String jamMasuk;

    @ColumnInfo(name = "jam_keluar")
    public String jamKeluar;

}

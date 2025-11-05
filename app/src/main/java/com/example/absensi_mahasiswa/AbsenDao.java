package com.example.absensi_mahasiswa;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AbsenDao {

    @Insert
    void insert(Absen absen);

    @Update
    void update(Absen absen);

    @Query("SELECT * FROM riwayat_absen ORDER BY tanggal DESC")
    List<Absen> getAll();

    @Query("SELECT * FROM riwayat_absen WHERE tanggal = :tanggal LIMIT 1")
    Absen getByDate(String tanggal);

}

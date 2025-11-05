package com.example.absensi_mahasiswa;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Absen.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AbsenDao absenDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "absen_database")
                            .allowMainThreadQueries() // For simplicity, not recommended for production
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

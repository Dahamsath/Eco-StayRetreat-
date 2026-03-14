package com.example.ecostayretreat.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {UserEntity.class, RoomEntity.class, ActivityEntity.class, BookingEntity.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract RoomDao roomDao();
    public abstract ActivityDao activityDao();
    public abstract BookingDao bookingDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "ecostay_database")
                            .fallbackToDestructiveMigration() // Use this only during development if schema changes frequently
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
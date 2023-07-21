package com.example.databaseroom;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 5) // Укажите здесь все ваши классы сущностей
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao getUserDao(); // Абстрактные методы для каждой сущности

    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "my_database")
                    .fallbackToDestructiveMigration() // Используйте fallbackToDestructiveMigration() для простой миграции
                    .build();
        }
        return INSTANCE;

    }
}


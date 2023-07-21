package com.example.databaseroom;

import android.app.Application;
import androidx.room.Room;

public class MyApp extends Application {
    private AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        appDatabase = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "my_database")
                .build();
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}


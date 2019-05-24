package com.protechgene.android.bpconnect.di.module;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.protechgene.android.bpconnect.data.local.db.AppDatabase;
import com.protechgene.android.bpconnect.data.local.db.DatabaseHelper;

public class RoomModule {

    private AppDatabase mAppDatabase;
    public RoomModule(Application mApplication) {

        mAppDatabase  = Room.databaseBuilder(mApplication,
                AppDatabase.class, "app_database")
                // Wipes and rebuilds instead of migrating if no Migration object.
                // Migration is not part of this codelab.
                .fallbackToDestructiveMigration()
                .build();
    }

    public AppDatabase provideDatabase()
      {
          return mAppDatabase;
      }

    public DatabaseHelper provideDatabaseHelper(AppDatabase appDatabase)
    {
        return new DatabaseHelper(appDatabase);
    }
}

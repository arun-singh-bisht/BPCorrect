package com.protechgene.android.bpconnect.data.local.db;


import android.arch.lifecycle.LiveData;

import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;

import java.util.List;

public class DatabaseHelper implements DatabaseHelperInterface {

    private AppDatabase mAppDatabase;
    public DatabaseHelper(AppDatabase appDatabase) {
        this.mAppDatabase = appDatabase;
    }

    @Override
    public void addNewHealthRecord(HealthReading healthReading) {
        mAppDatabase.getHealthReadingDAO().addNewRecord(healthReading);
    }

    @Override
    public List<HealthReading> getAllRecords() {
        return mAppDatabase.getHealthReadingDAO().getAllRecords();
    }

    @Override
    public void deleteAllHealthRecords() {
        mAppDatabase.getHealthReadingDAO().deleteAllRecords();
    }
}

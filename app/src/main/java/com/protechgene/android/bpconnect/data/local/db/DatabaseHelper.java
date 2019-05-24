package com.protechgene.android.bpconnect.data.local.db;



import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;

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
    public List<HealthReading> getLastAlarmRecords(long lastAlarmTimeInMilli, long offsetTime) {
        return mAppDatabase.getHealthReadingDAO().getLastAlarmRecords(lastAlarmTimeInMilli, offsetTime);
    }

    @Override
    public void deleteAllHealthRecords() {
        mAppDatabase.getHealthReadingDAO().deleteAllRecords();
    }

    @Override
    public void addNewProtocol(ProtocolModel protocolModel) {
        mAppDatabase.getProtocolDAO().addNewProtocol(protocolModel);
    }

    @Override
    public List<ProtocolModel> getAllProtocol() {
        return mAppDatabase.getProtocolDAO().getAllProtocol();
    }

    @Override
    public void deleteAllProtocol() {
        mAppDatabase.getProtocolDAO().deleteAllProtocol();
    }
}

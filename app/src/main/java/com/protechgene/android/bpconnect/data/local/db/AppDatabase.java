package com.protechgene.android.bpconnect.data.local.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.protechgene.android.bpconnect.data.local.db.dao.HealthReadingDAO;
import com.protechgene.android.bpconnect.data.local.db.dao.ProtocolDAO;
import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;


/**
 * Created by Arun.Singh on 9/11/2018.
 */

@Database(entities = {HealthReading.class, ProtocolModel.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract HealthReadingDAO getHealthReadingDAO();

    public abstract ProtocolDAO getProtocolDAO();

}

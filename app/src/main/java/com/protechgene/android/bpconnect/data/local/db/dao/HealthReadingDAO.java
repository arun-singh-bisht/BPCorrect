package com.protechgene.android.bpconnect.data.local.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;

import java.util.List;

/**
 * Created by Arun.Singh on 9/11/2018.
 */

@Dao
public interface HealthReadingDAO {

    @Insert
    void addNewRecord(HealthReading healthReading);

    @Query("SELECT * FROM health_table")
    List<HealthReading> getAllRecords();

    @Query("SELECT * FROM health_table WHERE reading_time >= :lastAlarmTimeInMilli AND reading_time<= :offsetTime")
    List<HealthReading> getLastAlarmRecords(long lastAlarmTimeInMilli,long offsetTime);


    @Query("DELETE FROM health_table")
    void deleteAllRecords();
}

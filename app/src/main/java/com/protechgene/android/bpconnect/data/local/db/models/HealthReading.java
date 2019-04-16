package com.protechgene.android.bpconnect.data.local.db.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


/**
 * Created by Arun.Singh on 9/11/2018.
 */

@Entity(tableName = "health_table")
public class HealthReading {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "productId")
    private int readingID;

    @ColumnInfo(name = "systolic")
    private String systolic;

    @ColumnInfo(name = "diastolic")
    private String diastolic;

    @ColumnInfo(name = "pulse")
    private String pulse;

    @ColumnInfo(name = "logTime")
    private String logTime;

    @ColumnInfo(name = "isSync")
    private boolean isSync;

    public HealthReading(int readingID, String systolic, String diastolic, String pulse, String logTime, boolean isSync) {
        this.readingID = readingID;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.pulse = pulse;
        this.logTime = logTime;
        this.isSync = isSync;
    }

    public String getSystolic() {
        return systolic;
    }

    public void setSystolic(String systolic) {
        this.systolic = systolic;
    }

    public String getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(String diastolic) {
        this.diastolic = diastolic;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    public int getReadingID() {
        return readingID;
    }

    public void setReadingID(int readingID) {
        this.readingID = readingID;
    }
}

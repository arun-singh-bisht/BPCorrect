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
    @ColumnInfo(name = "readingID")
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

    @ColumnInfo(name = "is_abberant")
    private String is_abberant;

    @ColumnInfo(name = "protocol_id")
    private String protocol_id;

    @ColumnInfo(name = "reading_time")
    private String reading_time;

   /* public HealthReading(int readingID, String systolic, String diastolic, String pulse, String logTime, boolean isSync,boolean is_abberant,String protocol_id) {
        this.readingID = readingID;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.pulse = pulse;
        this.logTime = logTime;
        this.isSync = isSync;
        this.is_abberant = is_abberant;
        this.protocol_id = protocol_id;
    }*/

    public HealthReading() {
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



    public String getProtocol_id() {
        return protocol_id;
    }

    public void setProtocol_id(String protocol_id) {
        this.protocol_id = protocol_id;
    }

    public String getReading_time() {
        return reading_time;
    }

    public void setReading_time(String reading_time) {
        this.reading_time = reading_time;
    }

    public String getIs_abberant() {
        return is_abberant;
    }

    public void setIs_abberant(String is_abberant) {
        this.is_abberant = is_abberant;
    }
}

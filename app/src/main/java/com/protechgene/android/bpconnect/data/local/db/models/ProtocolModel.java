package com.protechgene.android.bpconnect.data.local.db.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


/**
 * Created by Arun.Singh on 9/11/2018.
 */

@Entity(tableName = "protocol_table")
public class ProtocolModel {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "protocolId")
    private int protocolId;

    @ColumnInfo(name = "startDay")
    private String startDay;

    @ColumnInfo(name = "endDay")
    private String endDay;

    @ColumnInfo(name = "morningReadingTime")
    private String morningReadingTime;

    @ColumnInfo(name = "eveningReadingTime")
    private String eveningReadingTime;

    @ColumnInfo(name = "isActive")
    private boolean isActive;

    @ColumnInfo(name = "protocolCode")
    private String protocolCode;

    public ProtocolModel(int protocolId, String startDay, String endDay, String morningReadingTime, String eveningReadingTime, boolean isActive) {
        this.protocolId = protocolId;
        this.startDay = startDay;
        this.endDay = endDay;
        this.morningReadingTime = morningReadingTime;
        this.eveningReadingTime = eveningReadingTime;
        this.isActive = isActive;
    }

    public int getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(int protocolId) {
        this.protocolId = protocolId;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public String getMorningReadingTime() {
        return morningReadingTime;
    }

    public void setMorningReadingTime(String morningReadingTime) {
        this.morningReadingTime = morningReadingTime;
    }

    public String getEveningReadingTime() {
        return eveningReadingTime;
    }

    public void setEveningReadingTime(String eveningReadingTime) {
        this.eveningReadingTime = eveningReadingTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getProtocolCode() {
        return protocolCode;
    }

    public void setProtocolCode(String protocolCode) {
        this.protocolCode = protocolCode;
    }
}

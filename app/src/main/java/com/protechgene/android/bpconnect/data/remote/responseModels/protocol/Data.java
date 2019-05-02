package com.protechgene.android.bpconnect.data.remote.responseModels.protocol;

import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("end_date")
    private String endDate;
    @SerializedName("patient_id")
    private Integer patientId;
    @SerializedName("morning_alarm")
    private String morningAlarm;
    @SerializedName("total_readings")
    private Integer totalReadings;
    @SerializedName("id")
    private Integer id;
    @SerializedName("protocol_id")
    private String protocolId;
    @SerializedName("evening_alarm")
    private String eveningAlarm;
    @SerializedName("start_date")
    private String startDate;
    @SerializedName("status")
    private String status;

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getMorningAlarm() {
        return morningAlarm;
    }

    public void setMorningAlarm(String morningAlarm) {
        this.morningAlarm = morningAlarm;
    }

    public Integer getTotalReadings() {
        return totalReadings;
    }

    public void setTotalReadings(Integer totalReadings) {
        this.totalReadings = totalReadings;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(String protocolId) {
        this.protocolId = protocolId;
    }

    public String getEveningAlarm() {
        return eveningAlarm;
    }

    public void setEveningAlarm(String eveningAlarm) {
        this.eveningAlarm = eveningAlarm;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

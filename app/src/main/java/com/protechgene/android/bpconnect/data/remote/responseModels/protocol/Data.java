package com.protechgene.android.bpconnect.data.remote.responseModels.protocol;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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
    @SerializedName("device_name")
    private String device_name;
    @SerializedName("_key_value")
    private Datasource datasource;
    @SerializedName("sys")
    private float systolic;
    @SerializedName("dias")
    private float diastolic;
    @SerializedName("pulse")
    private float pulse;

    public float getPulse() {
        return pulse;
    }

    public void setPulse(float pulse) {
        this.pulse = pulse;
    }


    public float getSystolic() {
        return systolic;
    }

    public void setSystolic(float systolic) {
        this.systolic = systolic;
    }

    public float getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(float diastolic) {
        this.diastolic = diastolic;
    }

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

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public Datasource getDatasource() {
        return datasource;
    }

    public void setDatasource(Datasource datasource) {
        this.datasource = datasource;
    }
}

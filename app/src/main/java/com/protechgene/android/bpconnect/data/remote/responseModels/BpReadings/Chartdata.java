package com.protechgene.android.bpconnect.data.remote.responseModels.BpReadings;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Chartdata {

    @SerializedName("date")
    private String date;
    @SerializedName("avg_values")
    private AvgValues avgValues;
    @SerializedName("actual_values")
    private List<ActualValue> actualValues = null;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public AvgValues getAvgValues() {
        return avgValues;
    }

    public void setAvgValues(AvgValues avgValues) {
        this.avgValues = avgValues;
    }

    public List<ActualValue> getActualValues() {
        return actualValues;
    }

    public void setActualValues(List<ActualValue> actualValues) {
        this.actualValues = actualValues;
    }
}

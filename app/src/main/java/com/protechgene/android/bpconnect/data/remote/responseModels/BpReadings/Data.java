package com.protechgene.android.bpconnect.data.remote.responseModels.BpReadings;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    @SerializedName("chartdata")
    private List<Chartdata> chartdata = null;

    public List<Chartdata> getChartdata() {
        return chartdata;
    }

    public void setChartdata(List<Chartdata> chartdata) {
        this.chartdata = chartdata;
    }
}

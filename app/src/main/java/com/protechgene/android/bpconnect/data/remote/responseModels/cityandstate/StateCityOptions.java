package com.protechgene.android.bpconnect.data.remote.responseModels.cityandstate;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StateCityOptions {

    @SerializedName("data")
    private List<DataOptions> data;

    public List<DataOptions> getData() {
        return data;
    }

    public void setData(List<DataOptions> data) {
        this.data = data;
    }
}

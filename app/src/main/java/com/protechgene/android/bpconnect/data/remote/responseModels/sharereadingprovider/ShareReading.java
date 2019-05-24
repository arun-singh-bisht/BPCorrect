package com.protechgene.android.bpconnect.data.remote.responseModels.sharereadingprovider;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ShareReading {

    @SerializedName("data")
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData (List<Data> data) {
        this.data = data;
    }
}

package com.protechgene.android.bpconnect.data.remote.responseModels.cityandstate;

import com.google.gson.annotations.SerializedName;

public class DataOptions {

    @SerializedName("name")
    String name;
    @SerializedName("code")
    String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

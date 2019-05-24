package com.protechgene.android.bpconnect.data.remote.responseModels.protocol;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetProtocolResponse {

    private String message;
    @SerializedName("data")
    private List<Data> data = null;
    @SerializedName("valid")
    private Boolean valid;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}

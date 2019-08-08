package com.protechgene.android.bpconnect.data.remote.responseModels.protocol;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetProtocolResponse {

    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public List<Data> data = null;
    @SerializedName("valid")
    public Boolean valid;

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

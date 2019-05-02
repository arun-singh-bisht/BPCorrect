package com.protechgene.android.bpconnect.data.remote.responseModels.AddBPReading;

import com.google.gson.annotations.SerializedName;

public class AddBpReadingResponse {

    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private String data;
    @SerializedName("valid")
    private Boolean valid;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}

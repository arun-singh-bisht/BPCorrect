package com.protechgene.android.bpconnect.data.remote.responseModels.ResetPassword;

import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

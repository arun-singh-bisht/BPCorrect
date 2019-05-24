package com.protechgene.android.bpconnect.data.remote.responseModels;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordModel {

    @SerializedName("message")
    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

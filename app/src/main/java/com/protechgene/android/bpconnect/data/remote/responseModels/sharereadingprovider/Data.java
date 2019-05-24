package com.protechgene.android.bpconnect.data.remote.responseModels.sharereadingprovider;

import com.google.gson.annotations.SerializedName;

public class Data {

   @SerializedName("status")
   private Integer status;
   @SerializedName("message")
   private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

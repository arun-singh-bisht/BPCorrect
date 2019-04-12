package com.protechgene.android.bpconnect.data.remote.responseModels.BpReadings;

import com.google.gson.annotations.SerializedName;

public class ActualValue {

    @SerializedName("timestamp")
    private String timestamp;
    @SerializedName("DBP")
    private String dBP;
    @SerializedName("SBP")
    private String sBP;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDBP() {
        return dBP;
    }

    public void setDBP(String dBP) {
        this.dBP = dBP;
    }

    public String getSBP() {
        return sBP;
    }

    public void setSBP(String sBP) {
        this.sBP = sBP;
    }
}

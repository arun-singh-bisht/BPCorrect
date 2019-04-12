package com.protechgene.android.bpconnect.data.remote.responseModels.BpReadings;

import com.google.gson.annotations.SerializedName;

public class AvgValues {

    @SerializedName("DBP")
    private String dBP;
    @SerializedName("SBP")
    private String sBP;

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

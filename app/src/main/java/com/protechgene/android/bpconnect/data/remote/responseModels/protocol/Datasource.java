package com.protechgene.android.bpconnect.data.remote.responseModels.protocol;

import com.google.gson.annotations.SerializedName;

public class Datasource {

    @SerializedName("SBP")
    private String SBP;
    @SerializedName("DBP")
    private Integer DBP;
    @SerializedName("PULSE")
    private String PULSE;

    public String getSBP() {
        return SBP;
    }

    public void setSBP(String SBP) {
        this.SBP = SBP;
    }

    public Integer getDBP() {
        return DBP;
    }

    public void setDBP(Integer DBP) {
        this.DBP = DBP;
    }

    public String getPULSE() {
        return PULSE;
    }

    public void setPULSE(String PULSE) {
        this.PULSE = PULSE;
    }
}

package com.protechgene.android.bpconnect.data.remote.responseModels.BpReadings;

import com.google.gson.annotations.SerializedName;

public class ActualValue {

    @SerializedName("timestamp")
    private String timestamp;
    @SerializedName("DBP")
    private String dBP;
    @SerializedName("SBP")
    private String sBP;
    @SerializedName("is_abberant")
    private String is_abberant;
    @SerializedName("protocol_id")
    private String protocol_id;
    @SerializedName("PULSE")
    private String PULSE;

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

    public String getdBP() {
        return dBP;
    }

    public void setdBP(String dBP) {
        this.dBP = dBP;
    }

    public String getsBP() {
        return sBP;
    }

    public void setsBP(String sBP) {
        this.sBP = sBP;
    }

    public String getIs_abberant() {
        return is_abberant;
    }

    public void setIs_abberant(String is_abberant) {
        this.is_abberant = is_abberant;
    }

    public String getProtocol_id() {
        return protocol_id;
    }

    public void setProtocol_id(String protocol_id) {
        this.protocol_id = protocol_id;
    }

    public String getPULSE() {
        return PULSE;
    }

    public void setPULSE(String PULSE) {
        this.PULSE = PULSE;
    }
}

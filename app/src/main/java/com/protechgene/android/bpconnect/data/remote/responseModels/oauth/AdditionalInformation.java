package com.protechgene.android.bpconnect.data.remote.responseModels.oauth;

import com.google.gson.annotations.SerializedName;

public class AdditionalInformation {

    @SerializedName("role")
    private String role;
    @SerializedName("enable")
    private Integer enable;
    @SerializedName("id")
    private Integer id;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}

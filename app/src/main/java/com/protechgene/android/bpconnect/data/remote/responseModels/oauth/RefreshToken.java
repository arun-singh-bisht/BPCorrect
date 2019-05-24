package com.protechgene.android.bpconnect.data.remote.responseModels.oauth;

import com.google.gson.annotations.SerializedName;

public class RefreshToken {
    @SerializedName("value")
    private String value;
    @SerializedName("expiration")
    private Long expiration;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

}

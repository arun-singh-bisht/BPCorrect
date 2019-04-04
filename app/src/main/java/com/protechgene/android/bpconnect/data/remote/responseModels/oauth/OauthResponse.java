package com.protechgene.android.bpconnect.data.remote.responseModels.oauth;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OauthResponse {

    @SerializedName("value")
    private String value;
    @SerializedName("expiration")
    private Long expiration;
    @SerializedName("tokenType")
    private String tokenType;
    @SerializedName("refreshToken")
    private RefreshToken refreshToken;
    @SerializedName("scope")
    private List<String> scope = null;
    @SerializedName("additionalInformation")
    private AdditionalInformation additionalInformation;
    @SerializedName("expiresIn")
    private Integer expiresIn;
    @SerializedName("expired")
    private Boolean expired;

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

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public List<String> getScope() {
        return scope;
    }

    public void setScope(List<String> scope) {
        this.scope = scope;
    }

    public AdditionalInformation getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(AdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }
}

package com.protechgene.android.bpconnect.data.remote.responseModels;

import com.google.gson.annotations.SerializedName;

public class User implements ResponseDataInterface {

    @SerializedName("userId")
    private String userId;

    @SerializedName("userName")
    private String userName;

    @SerializedName("email")
    private String email;

    @SerializedName("profilePic")
    private String profilePic;

    @SerializedName("accessToken")
    private String accessToken;

    public User(String userId, String userName, String email, String profilePic, String accessToken) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.profilePic = profilePic;
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

package com.protechgene.android.bpconnect.data.local.sp;

import android.content.SharedPreferences;


public class PreferencesHelper implements PreferencesHelperInterface {


    private SharedPreferences mSharedPreferences;

    public PreferencesHelper(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    @Override
    public void clearSharedPref() {
        mSharedPreferences.edit().clear().commit();
    }

    @Override
    public String getAccessToken() {
        return mSharedPreferences.getString(PREF_KEY_ACCESS_TOKEN, null);
    }

    @Override
    public void setAccessToken(String accessToken) {
        mSharedPreferences.edit().putString(PREF_KEY_ACCESS_TOKEN, accessToken).apply();
    }

    @Override
    public String getCurrentUserEmail() {
        return mSharedPreferences.getString(PREF_KEY_CURRENT_USER_EMAIL, null);
    }

    @Override
    public void setCurrentUserEmail(String email) {
        mSharedPreferences.edit().putString(PREF_KEY_CURRENT_USER_EMAIL, email).apply();
    }

    @Override
    public String getCurrentUserId() {
        return mSharedPreferences.getString(PREF_KEY_CURRENT_USER_ID, null);
    }

    @Override
    public void setCurrentUserId(String userId) {
        mSharedPreferences.edit().putString(PREF_KEY_CURRENT_USER_ID, userId).apply();
    }


    @Override
    public String getCurrentUserName() {
        return mSharedPreferences.getString(PREF_KEY_CURRENT_USER_NAME, null);
    }

    @Override
    public void setCurrentUserName(String userName) {
        mSharedPreferences.edit().putString(PREF_KEY_CURRENT_USER_NAME, userName).apply();
    }

    @Override
    public String getCurrentUserProfilePicUrl() {
        return mSharedPreferences.getString(PREF_KEY_CURRENT_USER_PROFILE_PIC_URL, null);
    }

    @Override
    public void setCurrentUserProfilePicUrl(String profilePicUrl) {
        mSharedPreferences.edit().putString(PREF_KEY_CURRENT_USER_PROFILE_PIC_URL, profilePicUrl).apply();
    }

    @Override
    public boolean isLoggedIn() {
        return mSharedPreferences.getBoolean(PREF_KEY_IS_LOGIN,false);
    }

    @Override
    public void setIsLoggedIn(boolean isLoggedIn) {
        mSharedPreferences.edit().putBoolean(PREF_KEY_IS_LOGIN,isLoggedIn).apply();
    }
}

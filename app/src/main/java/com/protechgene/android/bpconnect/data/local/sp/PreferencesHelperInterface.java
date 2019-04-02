package com.protechgene.android.bpconnect.data.local.sp;

public interface PreferencesHelperInterface {


     String PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN";
     String PREF_KEY_CURRENT_USER_EMAIL = "PREF_KEY_CURRENT_USER_EMAIL";
     String PREF_KEY_CURRENT_USER_ID = "PREF_KEY_CURRENT_USER_ID";
     String PREF_KEY_CURRENT_USER_NAME = "PREF_KEY_CURRENT_USER_NAME";
     String PREF_KEY_CURRENT_USER_PROFILE_PIC_URL = "PREF_KEY_CURRENT_USER_PROFILE_PIC_URL";
     String PREF_KEY_IS_LOGIN = "PREF_KEY_IS_LOGIN";

     void clearSharedPref();

     String getAccessToken();

     void setAccessToken(String accessToken);

     String getCurrentUserEmail();

     void setCurrentUserEmail(String email);

     String getCurrentUserId();

     void setCurrentUserId(String userId);

     String getCurrentUserName();

     void setCurrentUserName(String userName);

     String getCurrentUserProfilePicUrl();

     void setCurrentUserProfilePicUrl(String profilePicUrl);

     boolean isLoggedIn();

     void setIsLoggedIn(boolean isLoggedIn);
}

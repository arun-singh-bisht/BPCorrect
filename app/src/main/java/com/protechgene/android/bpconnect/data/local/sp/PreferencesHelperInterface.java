package com.protechgene.android.bpconnect.data.local.sp;

public interface PreferencesHelperInterface {


     String PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN";
     String PREF_KEY_IS_LOGIN = "PREF_KEY_IS_LOGIN";

     //Profile details
     String PREF_KEY_CURRENT_USER_EMAIL = "PREF_KEY_CURRENT_USER_EMAIL";
     String PREF_KEY_CURRENT_USER_ID = "PREF_KEY_CURRENT_USER_ID";
     String PREF_KEY_CURRENT_USER_NAME = "PREF_KEY_CURRENT_USER_NAME";
     String PREF_KEY_CURRENT_USER_PROFILE_PIC_URL = "PREF_KEY_CURRENT_USER_PROFILE_PIC_URL";
     String PREF_KEY_PATIENT_ID = "PREF_KEY_PATIENT_ID";
     String PREF_KEY_PATIENT_GENDER = "PREF_KEY_PATIENT_GENDER";
     String PREF_KEY_PATIENT_IS_GRADUATED = "PREF_KEY_PATIENT_IS_GRADUATED";
     String PREF_KEY_PATIENT_ADDRESS = "PREF_KEY_PATIENT_ADDRESS";
     String PREF_KEY_PATIENT_MOBILE = "PREF_KEY_PATIENT_MOBILE";
     String PREF_KEY_PATIENT_DOB = "PREF_KEY_PATIENT_DOB";

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

     String getPatientId();

     void setPatientId(String patientId);

     String getPatientGender();

     void setPatientGender(String gender);

     String getPatientAddress();

     void setPatientAddress(String address);

     String getPatientMobile();

     void setPatientMobile(String mobile);

     String getPatientDOB();

     void setPatientDOB(String dob);

     boolean isPatientGraduated();

     void setIsPatientGraduated(boolean isPatientGraduated);


}
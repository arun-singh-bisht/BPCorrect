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
     String PREF_KEY_PATIENT_WEIGHT = "PREF_KEY_PATIENT_WEIGHT";
     String PREF_KEY_PATIENT_HEIGHT = "PREF_KEY_PATIENT_HEIGHT";
     String PREF_KEY_PATIENT_ABOUT = "PREF_KEY_PATIENT_ABOUT";

     String PREF_KEY_BP_DEVICE_NAME = "PREF_KEY_BP_DEVICE_NAME";
     String PREF_KEY_BP_DEVICE_ADDRESS = "PREF_KEY_BP_DEVICE_ADDRESS";
     String PREF_KEY_BP_DEVICE_UUID = "PREF_KEY_BP_DEVICE_UUID";

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

     String getPatientWeight();

     void setPatientWeight(String weight);

     String getPatientHeight();

     void setPatientHeight(String height);

     boolean isPatientGraduated();

     void setIsPatientGraduated(boolean isPatientGraduated);

     String getBPDeviceName();

     void setBPDeviceName(String name);

     String getBPDeviceAddress();

     void setBPDeviceAddress(String address);

     String getBPDeviceUUID();

     void setBPDeviceUUID(String uuid);

     String getPatientAbout();

     void setPatientAbout(String about);


}

package com.protechgene.android.bpconnect.data.local.sp;

public interface PreferencesHelperInterface {


     String PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN";
     String PREF_KEY_IS_LOGIN = "PREF_KEY_IS_LOGIN";
     String PREF_KEY_IS_DATA_SYNC = "PREF_KEY_IS_DATA_SYNc";

     //Profile details
     String PREF_KEY_CURRENT_USER_EMAIL = "PREF_KEY_CURRENT_USER_EMAIL";
     String PREF_KEY_CURRENT_USER_ID = "PREF_KEY_CURRENT_USER_ID";
     String PREF_KEY_CURRENT_USER_NAME = "PREF_KEY_CURRENT_USER_NAME";
     String PREF_KEY_CURRENT_USER_FIRST_NAME = "PREF_KEY_CURRENT_USER_FIRST_NAME";
     String PREF_KEY_CURRENT_USER_LAST_NAME = "PREF_KEY_CURRENT_USER_LAST_NAME";
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
     String PREF_KEY_PROFILE_IMG = "PREF_KEY_PROFILE_IMG";


     // edit by sohit
     String PREF_KEY_PATIENT_STATE = "PREF_KEY_PATIENT_STATE";
     String PREF_KEY_PATIENT_CITY = "PREF_KEY_PATIENT_CITY";
     String PREF_KEY_FIRST_TIME_USER = "PREF_KEY_FIRST_TIME_USER";
     String PREF_KEY_PATIENT_ZIPCODE = "PREF_KEY_PATIENT_ZIPCODE";
     String PREF_KEY_ORG_NAME = "PREF_KEY_ORG_NAME";
     String PREF_KEY_PROVIDER_NAME = "PREF_KEY_PROVIDER_NAME";

     String PREF_KEY_BP_DEVICE_NAME = "PREF_KEY_BP_DEVICE_NAME";
     String PREF_KEY_BP_DEVICE_ADDRESS = "PREF_KEY_BP_DEVICE_ADDRESS";
     String PREF_KEY_BP_DEVICE_UUID = "PREF_KEY_BP_DEVICE_UUID";

     String PREF_KEY_BP_DEVICE_NAME_iHealthbp3l = "PREF_KEY_BP_DEVICE_NAME_iHealthbp3l";
     String PREF_KEY_BP_DEVICE_ADDRESS_iHealthbp3l = "PREF_KEY_BP_DEVICE_ADDRESS_iHealthbp3l";



     // edit by sohit
     void setPrefKeyPatientState(String state);
     String getPrefKeyPatientState();

     void setPrefKeyPatientCity(String city);
     String getPrefKeyPatientCity();

     void setFirstTimeuser(String status);
     String getFirstTimeuser();

     void setPrefKeyPatientZipcode(String zipcode);
     String getPrefKeyPatientZipcode();

     void setPrefKeyOrgName(String orgName);
     String getPrefKeyOrgName();

     void setPrefKeyProviderName(String providerName);
     String getPrefKeyProviderName();


     void clearSharedPref();

     String getAccessToken();

     void setAccessToken(String accessToken);

     String getCurrentUserEmail();

     void setCurrentUserEmail(String email);

     String getCurrentUserId();

     void setCurrentUserId(String userId);

     String getCurrentUserName();

     void setCurrentUserName(String userName);




     String getUserFirstName();

     void setUserFirstName(String firstName);

     String getUserLastName();

     void setUserLastName(String lastName);


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

     boolean isHistoryDataSync();

     void setHistoryDataSyncStatus(boolean status);

     void setPrefKeyProfileImg(String url);

     String getPrefKeyProfileImg();

     String getDeviceName_iHealthbp3l();
     void setDeviceName_iHealthbp3l(String name) ;
     String getDeviceAddress_iHealthbp3l() ;
     void setDeviceAddress_iHealthbp3l(String address) ;

}

package com.protechgene.android.bpconnect.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.google.gson.JsonArray;
import com.protechgene.android.bpconnect.data.local.db.DatabaseHelper;
import com.protechgene.android.bpconnect.data.local.db.DatabaseHelperInterface;
import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;
import com.protechgene.android.bpconnect.data.local.db.models.Word;
import com.protechgene.android.bpconnect.data.local.sp.PreferencesHelper;
import com.protechgene.android.bpconnect.data.local.sp.PreferencesHelperInterface;
import com.protechgene.android.bpconnect.data.remote.ApiInterface;
import com.protechgene.android.bpconnect.data.remote.responseModels.AddBPReading.AddBpReadingResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.BpReadings.BpReadingsResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.ChangePasswordModel;
import com.protechgene.android.bpconnect.data.remote.responseModels.ResetPassword.ResetPasswordResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.appRedirect.ActivateAccount;
import com.protechgene.android.bpconnect.data.remote.responseModels.cityandstate.StateCityOptions;
import com.protechgene.android.bpconnect.data.remote.responseModels.oauth.OauthResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.profile.ProfileResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.protocol.GetProtocolResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.sharereadingprovider.ShareReading;
import com.protechgene.android.bpconnect.di.module.AppModule;
import com.protechgene.android.bpconnect.di.module.RestModule;
import com.protechgene.android.bpconnect.di.module.RoomModule;

import org.json.JSONArray;

import java.util.List;

import io.reactivex.Observable;

public class Repository implements ApiInterface,
        PreferencesHelperInterface,
        DatabaseHelperInterface {

    private static Repository repositoryInstance;

    private ApiInterface mApiInterface;
    private PreferencesHelper mSharedPrefsHelper;
    private DatabaseHelper mDatabaseHelper;

    private Repository(ApiInterface apiInterface, PreferencesHelper mSharedPrefsHelper, DatabaseHelper databaseHelper) {
        this.mApiInterface = apiInterface;
        this.mSharedPrefsHelper = mSharedPrefsHelper;
        this.mDatabaseHelper = databaseHelper;
    }

    public static Repository getInstance(Application mApplication)
    {
        if(repositoryInstance==null)
        {

            RestModule restModule = new RestModule();
            ApiInterface apiInterface = restModule.provideApiCallInterface(restModule.provideRetrofit(restModule.provideGson(),restModule.provideOkHttpClient(mApplication.getApplicationContext())));

            AppModule appModule = new AppModule(mApplication);
            PreferencesHelper preferencesHelper = appModule.provideSharedPrefsHelper(appModule.provideSharedPrefs());

            RoomModule roomModule = new RoomModule(mApplication);
            DatabaseHelper databaseHelper = roomModule.provideDatabaseHelper(roomModule.provideDatabase());

            repositoryInstance = new Repository(apiInterface,preferencesHelper,databaseHelper);
        }
        return repositoryInstance;
    }
    //----------------------------------------------------------------------------------------------
    // Shared Preference access methods

    @Override
    public String getAccessToken() {
        return mSharedPrefsHelper.getAccessToken();
    }

    @Override
    public void setAccessToken(String accessToken) {
        mSharedPrefsHelper.setAccessToken(accessToken);
    }

    @Override
    public String getCurrentUserEmail() {
        return mSharedPrefsHelper.getCurrentUserEmail();
    }

    @Override
    public void setCurrentUserEmail(String email) {
        mSharedPrefsHelper.setCurrentUserEmail(email);
    }

    @Override
    public String getCurrentUserId() {
        return mSharedPrefsHelper.getCurrentUserId();
    }

    @Override
    public void setCurrentUserId(String userId) {
        mSharedPrefsHelper.setCurrentUserId(userId);
    }

    @Override
    public String getCurrentUserName() {
        return mSharedPrefsHelper.getCurrentUserName();
    }

    @Override
    public void setCurrentUserName(String userName) { mSharedPrefsHelper.setCurrentUserName(userName); }



    @Override
    public String getUserFirstName() {
        return mSharedPrefsHelper.getUserFirstName();
    }

    @Override
    public void setUserFirstName(String firstName) {
        mSharedPrefsHelper.setUserFirstName(firstName);
    }

    @Override
    public String getUserLastName() {
        return mSharedPrefsHelper.getUserLastName();
    }

    @Override
    public void setUserLastName(String lastName) {
        mSharedPrefsHelper.setUserLastName(lastName);
    }

    @Override
    public String getCurrentUserProfilePicUrl() { return mSharedPrefsHelper.getCurrentUserProfilePicUrl(); }

    @Override
    public void setCurrentUserProfilePicUrl(String profilePicUrl) { mSharedPrefsHelper.setCurrentUserProfilePicUrl(profilePicUrl); }

    @Override
    public boolean isLoggedIn() { return mSharedPrefsHelper.isLoggedIn(); }

    @Override
    public void setIsLoggedIn(boolean isLoggedIn) { mSharedPrefsHelper.setIsLoggedIn(isLoggedIn); }

    @Override
    public String getPatientId() {
        return mSharedPrefsHelper.getPatientId();
    }

    @Override
    public void setPatientId(String patientId) {
        mSharedPrefsHelper.setPatientId(patientId);
    }

    @Override
    public String getPatientGender() {
        return mSharedPrefsHelper.getPatientGender();
    }

    @Override
    public void setPatientGender(String gender) {
        mSharedPrefsHelper.setPatientGender(gender);
    }

    @Override
    public String getPatientAddress() {
        return mSharedPrefsHelper.getPatientAddress();
    }

    @Override
    public void setPatientAddress(String address) {
        mSharedPrefsHelper.setPatientAddress(address);
    }

    @Override
    public String getPatientMobile() {
        return mSharedPrefsHelper.getPatientMobile();
    }

    @Override
    public void setPatientMobile(String mobile) {
        mSharedPrefsHelper.setPatientMobile(mobile);
    }

    @Override
    public String getPatientDOB() {
        return mSharedPrefsHelper.getPatientDOB();
    }

    @Override
    public void setPatientDOB(String dob) {
        mSharedPrefsHelper.setPatientDOB(dob);
    }

    @Override
    public String getPatientWeight() {
        return mSharedPrefsHelper.getPatientWeight();
    }

    @Override
    public void setPatientWeight(String weight) {
        mSharedPrefsHelper.setPatientWeight(weight);
    }

    @Override
    public String getPatientHeight() {
        return mSharedPrefsHelper.getPatientHeight();
    }

    @Override
    public void setPatientHeight(String height) {
        mSharedPrefsHelper.setPatientHeight(height);
    }

    @Override
    public boolean isPatientGraduated() {
        return mSharedPrefsHelper.isPatientGraduated();
    }

    @Override
    public void setIsPatientGraduated(boolean isPatientGraduated) {
        mSharedPrefsHelper.setIsPatientGraduated(isPatientGraduated);
    }

    @Override
    public String getBPDeviceName() {
        return mSharedPrefsHelper.getBPDeviceName();
    }

    @Override
    public void setBPDeviceName(String name) {
        mSharedPrefsHelper.setBPDeviceName(name);
    }

    @Override
    public String getBPDeviceAddress() {
        return mSharedPrefsHelper.getBPDeviceAddress();
    }

    @Override
    public void setBPDeviceAddress(String address) {
        mSharedPrefsHelper.setBPDeviceAddress(address);
    }

    @Override
    public String getBPDeviceUUID() {
        return mSharedPrefsHelper.getBPDeviceUUID();
    }

    @Override
    public void setBPDeviceUUID(String uuid) {
        mSharedPrefsHelper.setBPDeviceUUID(uuid);
    }

    @Override
    public String getPatientAbout() {
        return mSharedPrefsHelper.getPatientAbout();
    }

    @Override
    public void setPatientAbout(String about) {
        mSharedPrefsHelper.setPatientAbout(about);
    }

    @Override
    public boolean isHistoryDataSync() {
        return mSharedPrefsHelper.isHistoryDataSync();
    }

    @Override
    public void setHistoryDataSyncStatus(boolean status) {
        mSharedPrefsHelper.setHistoryDataSyncStatus(status);
    }

    @Override
    public void setPrefKeyProfileImg(String url) {
        mSharedPrefsHelper.setPrefKeyProfileImg(url);
    }

    @Override
    public String getPrefKeyProfileImg() {
        return mSharedPrefsHelper.getPrefKeyProfileImg();
    }

    @Override
    public String getDeviceName_iHealthbp3l() {
        return mSharedPrefsHelper.getDeviceName_iHealthbp3l();
    }

    @Override
    public void setDeviceName_iHealthbp3l(String name) {
        mSharedPrefsHelper.setDeviceName_iHealthbp3l(name);
    }

    @Override
    public String getDeviceAddress_iHealthbp3l() {
        return mSharedPrefsHelper.getDeviceAddress_iHealthbp3l();
    }

    @Override
    public void setDeviceAddress_iHealthbp3l(String address) {
        mSharedPrefsHelper.setDeviceAddress_iHealthbp3l(address);
    }

    @Override
    public void setPrefKeyPatientState(String state) {
        mSharedPrefsHelper.setPrefKeyPatientState(state);
    }

    @Override
    public String getPrefKeyPatientState() {
        return mSharedPrefsHelper.getPrefKeyPatientState();
    }

    @Override
    public void setPrefKeyPatientCity(String city) {
        mSharedPrefsHelper.setPrefKeyPatientCity(city);
    }

    @Override
    public String getPrefKeyPatientCity() {
        return mSharedPrefsHelper.getPrefKeyPatientCity();
    }

    @Override
    public void setPrefKeyPatientZipcode(String zipcode) {
        mSharedPrefsHelper.setPrefKeyPatientZipcode(zipcode);
    }

    @Override
    public String getPrefKeyPatientZipcode() {
        return mSharedPrefsHelper.getPrefKeyPatientZipcode();
    }

    @Override
    public void setPrefKeyOrgName(String orgName) {
         mSharedPrefsHelper.setPrefKeyOrgName(orgName);
    }

    @Override
    public String getPrefKeyOrgName() {
        return mSharedPrefsHelper.getPrefKeyOrgName();
    }

    @Override
    public void setPrefKeyProviderName(String providerName) {
        mSharedPrefsHelper.setPrefKeyProviderName(providerName);
    }

    @Override
    public String getPrefKeyProviderName() {
        return mSharedPrefsHelper.getPrefKeyProviderName();
    }

    @Override
    public void clearSharedPref() {
        mSharedPrefsHelper.clearSharedPref();
    }


    // ----------------------------------------------------------------------------------------------
    // API Calls


    @Override
    public Observable<OauthResponse> oauth(String grant_type, String client_id, String username, String password) {
        Observable<OauthResponse> oauth = mApiInterface.oauth(grant_type, client_id, username, password);
        return oauth;
    }

    @Override
    public Observable<ActivateAccount> activate(String code) {
        Observable<ActivateAccount> activate = mApiInterface.activate(code);
        return activate;
    }

    @Override
    public Observable<ResetPasswordResponse> signUp(String email, String password) {
        Observable<ResetPasswordResponse> responseObservable = mApiInterface.signUp(email, password);
        return responseObservable;
    }

    @Override
    public Observable<ProfileResponse> profileDetails(String access_token, String patientUserId) {
        Observable<ProfileResponse> responseObservable = mApiInterface.profileDetails(access_token, patientUserId);
        return responseObservable;
    }

    @Override
    public Observable<BpReadingsResponse> getBpReadings(String access_token, String patientUserId, String fromdate, String todate, String dayno) {
        Observable<BpReadingsResponse> responseObservable = mApiInterface.getBpReadings(access_token, patientUserId,fromdate,todate,dayno);
        return responseObservable;
    }

    @Override
    public Observable<AddBpReadingResponse> addBpReadings(String access_token, JsonArray json) {
        Observable<AddBpReadingResponse> responseObservable = mApiInterface.addBpReadings(access_token,json);
        return responseObservable;
    }

    @Override
    public Observable<ProfileResponse> createProtocol(String access_token, String userId,String startdate,String enddate,String protocol_id,String morning_alarm,String evening_alarm) {
        Observable<ProfileResponse> responseObservable = mApiInterface.createProtocol(access_token,userId,startdate,enddate,protocol_id,morning_alarm,evening_alarm);
        return responseObservable;
    }

    @Override
    public Observable<GetProtocolResponse> getProtocolDetail(String access_token, String userId) {
        Observable<GetProtocolResponse> responseObservable = mApiInterface.getProtocolDetail(access_token,userId);
        return responseObservable;
}

    @Override
    public Observable<StateCityOptions> getOptionCity() {
        Observable<StateCityOptions> responseObservable = mApiInterface.getOptionCity();
        return responseObservable;
    }

    @Override
    public Observable<ResetPasswordResponse> changePassword(String password, String access_token, String userid) {
        Observable<ResetPasswordResponse> responseObservable = mApiInterface.changePassword(password,access_token,userid);
        return responseObservable;
    }

    @Override
    public Observable<ResetPasswordResponse> resetPassword(String password, String code) {
        Observable<ResetPasswordResponse> responseObservable = mApiInterface.resetPassword(password,code);
        return responseObservable;
    }

    @Override
    public Observable<ShareReading> getShareReading(String userid, String accessToken, String request_type, Boolean status) {
        Observable<ShareReading> responseObservable = mApiInterface.getShareReading(userid,accessToken,request_type,status);
        return responseObservable;
    }


    @Override
    public Observable<ProfileResponse> updateProfile(String access_token, String userId, String firstname,String lastname, String gender, String dob, String mobile1, String address1,String weight,String height,String photo_url,String state,String city,String zipcode) {
        Observable<ProfileResponse> responseObservable = mApiInterface.updateProfile(access_token, userId, firstname,lastname, gender, dob, mobile1, address1,weight,height,photo_url,state,city,zipcode);
        return responseObservable;
    }

    @Override
    public Observable<ResetPasswordResponse> resetPassword(String email) {
        Observable<ResetPasswordResponse> responseObservable = mApiInterface.resetPassword( email);
        return responseObservable;
    }


    // ----------------------------------------------------------------------------------------------
    // Local Database access methods

    @Override
    public void addNewHealthRecord(HealthReading healthReading) {
        mDatabaseHelper.addNewHealthRecord(healthReading);
    }

    @Override
    public List<HealthReading> getAllRecords() {
        return mDatabaseHelper.getAllRecords();
    }

    @Override
    public List<HealthReading> getLastAlarmRecords(long lastAlarmTimeInMilli, long offsetTime) {
        return mDatabaseHelper.getLastAlarmRecords(lastAlarmTimeInMilli, offsetTime);
    }

    @Override
    public void deleteAllHealthRecords() {
        mDatabaseHelper.deleteAllHealthRecords();
    }

    @Override
    public void addNewProtocol(ProtocolModel protocolModel) {
        mDatabaseHelper.addNewProtocol(protocolModel);
    }

    @Override
    public List<ProtocolModel> getAllProtocol() {
        return mDatabaseHelper.getAllProtocol();
    }

    @Override
    public void deleteAllProtocol() {
        mDatabaseHelper.deleteAllProtocol();
    }
}

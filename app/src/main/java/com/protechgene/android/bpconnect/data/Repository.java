package com.protechgene.android.bpconnect.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.protechgene.android.bpconnect.data.local.db.DatabaseHelper;
import com.protechgene.android.bpconnect.data.local.db.DatabaseHelperInterface;
import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;
import com.protechgene.android.bpconnect.data.local.db.models.Word;
import com.protechgene.android.bpconnect.data.local.sp.PreferencesHelper;
import com.protechgene.android.bpconnect.data.local.sp.PreferencesHelperInterface;
import com.protechgene.android.bpconnect.data.remote.ApiInterface;
import com.protechgene.android.bpconnect.data.remote.responseModels.BpReadings.BpReadingsResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.ResetPassword.ResetPasswordResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.oauth.OauthResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.profile.ProfileResponse;
import com.protechgene.android.bpconnect.di.module.AppModule;
import com.protechgene.android.bpconnect.di.module.RestModule;
import com.protechgene.android.bpconnect.di.module.RoomModule;

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
    public Observable<ProfileResponse> updateProfile(String access_token, String userId, String firstname, String gender, String dob, String mobile1, String address1,String weight,String height,String about) {
        Observable<ProfileResponse> responseObservable = mApiInterface.updateProfile(access_token, userId, firstname, gender, dob, mobile1, address1,weight,height,about);
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

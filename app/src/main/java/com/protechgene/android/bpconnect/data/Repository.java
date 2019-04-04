package com.protechgene.android.bpconnect.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.protechgene.android.bpconnect.data.local.db.DatabaseHelper;
import com.protechgene.android.bpconnect.data.local.db.DatabaseHelperInterface;
import com.protechgene.android.bpconnect.data.local.db.models.Word;
import com.protechgene.android.bpconnect.data.local.sp.PreferencesHelper;
import com.protechgene.android.bpconnect.data.local.sp.PreferencesHelperInterface;
import com.protechgene.android.bpconnect.data.remote.ApiInterface;
import com.protechgene.android.bpconnect.data.remote.responseModels.User;
import com.protechgene.android.bpconnect.data.remote.responseModels.oauth.OauthResponse;
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
    public Observable<User> login(String apiKey, String email, String password) {
        Observable<User> responseObservable = mApiInterface.login(apiKey, email, password);
        return responseObservable;
    }

    @Override
    public Observable<User> signUp(String apiKey, String email, String password) {
        Observable<User> responseObservable = mApiInterface.signUp(apiKey, email, password);
        return responseObservable;
    }

    @Override
    public Observable<User> resetPassword(String apiKey, String email) {
        Observable<User> responseObservable = mApiInterface.resetPassword(apiKey, email);
        return responseObservable;
    }


    // ----------------------------------------------------------------------------------------------
    // Local Database access methods

    @Override
    public void addNewWord(Word word) {
        //Execute this new backgroundthread
        mDatabaseHelper.addNewWord(word);
    }

    @Override
    public LiveData<List<Word>> getAllWords() {
        return mDatabaseHelper.getAllWords();
    }

    @Override
    public void deleteAllWords() {
        mDatabaseHelper.deleteAllWords();
    }
}

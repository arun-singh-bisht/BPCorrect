package com.protechgene.android.bpconnect.data;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.protechgene.android.bpconnect.data.local.db.DatabaseHelper;
import com.protechgene.android.bpconnect.data.local.db.DatabaseHelperInterface;
import com.protechgene.android.bpconnect.data.local.db.models.Word;
import com.protechgene.android.bpconnect.data.local.sp.PreferencesHelper;
import com.protechgene.android.bpconnect.data.local.sp.PreferencesHelperInterface;
import com.protechgene.android.bpconnect.data.remote.ApiInterface;
import com.protechgene.android.bpconnect.data.remote.models.User;

import java.util.List;

import io.reactivex.Observable;

public class Repository implements ApiInterface,
        PreferencesHelperInterface,
        DatabaseHelperInterface {

    private static Repository repositoryInstance;

    private ApiInterface mApiInterface;
    private PreferencesHelper mSharedPrefsHelper;
    private DatabaseHelper mDatabaseHelper;

    public Repository(ApiInterface apiInterface, PreferencesHelper mSharedPrefsHelper, DatabaseHelper databaseHelper) {
        this.mApiInterface = apiInterface;
        this.mSharedPrefsHelper = mSharedPrefsHelper;
        this.mDatabaseHelper = databaseHelper;
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

   /* @Override
    public Observable<MovieResponse> getTopRatedMovie(String apiKey) {
        Observable<MovieResponse> responseObservable = mApiInterface.getTopRatedMovie(apiKey);
        return responseObservable;
    }*/

    @Override
    public Observable<User> login(String apiKey, String email, String password) {
        Observable<User> responseObservable = mApiInterface.login(apiKey, email, password);
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

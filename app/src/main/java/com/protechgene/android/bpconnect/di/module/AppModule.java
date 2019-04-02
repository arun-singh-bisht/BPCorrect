package com.protechgene.android.bpconnect.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.local.db.DatabaseHelper;
import com.protechgene.android.bpconnect.data.local.sp.PreferencesHelper;
import com.protechgene.android.bpconnect.data.remote.ApiInterface;

public class AppModule {

    private final Application mApplication;

    public AppModule(Application app) {
        mApplication = app;
    }

    Context provideContext() {
        return mApplication.getApplicationContext();
    }

    Application provideApplication() {
        return mApplication;
    }

    SharedPreferences provideSharedPrefs() {
        return mApplication.getSharedPreferences("demo-prefs", Context.MODE_PRIVATE);
    }

    PreferencesHelper provideSharedPrefsHelper(SharedPreferences sharedPreferences) {
        return new PreferencesHelper(sharedPreferences);
    }

    Repository provideRepository(ApiInterface apiCallInterface, PreferencesHelper mSharedPrefsHelper, DatabaseHelper databaseHelper) {
        return new Repository(apiCallInterface,mSharedPrefsHelper,databaseHelper);
    }

    /*ViewModelProvider.Factory provideViewModelFactory(Repository myRepository) {
        return new ViewModelFactory(myRepository);
    }*/

    /*MovieFragment provideMoviesFragment()
    {
        return new MovieFragment();
    }*/
}

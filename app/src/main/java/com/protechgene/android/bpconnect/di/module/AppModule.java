package com.protechgene.android.bpconnect.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.protechgene.android.bpconnect.data.local.sp.PreferencesHelper;

public class AppModule {

    private final Application mApplication;

    public AppModule(Application app) {
        mApplication = app;
    }

    public Context provideContext() {
        return mApplication.getApplicationContext();
    }

    public Application provideApplication() {
        return mApplication;
    }

    public SharedPreferences provideSharedPrefs() {
        return mApplication.getSharedPreferences("bp_connect_db", Context.MODE_PRIVATE);
    }

    public PreferencesHelper provideSharedPrefsHelper(SharedPreferences sharedPreferences) {
        return new PreferencesHelper(sharedPreferences);
    }

    /*public Repository provideRepository(ApiInterface apiCallInterface, PreferencesHelper mSharedPrefsHelper, DatabaseHelper databaseHelper) {
        return new Repository(apiCallInterface,mSharedPrefsHelper,databaseHelper);
    }*/

    /*ViewModelProvider.Factory provideViewModelFactory(Repository myRepository) {
        return new ViewModelFactory(myRepository);
    }*/

    /*MovieFragment provideMoviesFragment()
    {
        return new MovieFragment();
    }*/
}

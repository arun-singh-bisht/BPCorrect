package com.protechgene.android.bpconnect.ui.splash;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.ui.base.BaseActivity;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.home.MainActivity;
import com.protechgene.android.bpconnect.ui.login.LoginActivity;


public class SplashActivity extends BaseActivity implements SplashNavigator{


    private SplashViewModel splashViewModel;

    @Override
    protected int layoutRes() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initialize() {

        ViewModelFactory mViewModelFactory = ViewModelFactory.getInstance(getApplication());
        splashViewModel = ViewModelProviders.of(this,mViewModelFactory).get(SplashViewModel.class);
        splashViewModel.setNavigator(this);
        splashViewModel.nextScreen();
    }

    @Override
    public void goToLoginScreen() {
       startActivity(new Intent(this, LoginActivity.class));
       finish();
    }

    @Override
    public void goToHomeScreen() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

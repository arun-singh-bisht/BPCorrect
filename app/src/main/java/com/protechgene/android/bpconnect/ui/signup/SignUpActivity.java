package com.protechgene.android.bpconnect.ui.signup;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Handler;
import android.widget.EditText;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.ui.base.BaseActivity;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.login.LoginActivity;

import butterknife.BindView;
import butterknife.OnClick;


public class SignUpActivity extends BaseActivity implements SignUpNavigator  {

    @BindView(R.id.edit_email)
    EditText edit_email;
    @BindView(R.id.edit_password)
    EditText edit_password;
    @BindView(R.id.edit_password_confirm)
    EditText edit_password_confirm;

    private SignUpViewModel mSignUpViewModel;

    @Override
    protected int layoutRes() {
        return R.layout.activity_sign_up_new;
    }

    @Override
    protected void initialize() {
        mSignUpViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getApplication())).get(SignUpViewModel.class);
        mSignUpViewModel.setNavigator(this);
    }

    @OnClick(R.id.image_back)
    void onBackImageClick() {
        finish();
    }


    @OnClick(R.id.btn_signup)
    public void signUp() {
        String email = edit_email.getText().toString();
        String password = edit_password.getText().toString();
        String passwordConfirm = edit_password_confirm.getText().toString();

        showProgress("Please wait...");
        mSignUpViewModel.registerUser(email, password,passwordConfirm);
    }

    @Override
    public void openLoginScreen() {
        hideProgress();
        showSnakeBar("Patient Added SuccessFully");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        },3*1000);
    }

    @Override
    public void handleError(Throwable throwable) {
        hideProgress();
        showSnakeBar(throwable.getMessage());
    }
}

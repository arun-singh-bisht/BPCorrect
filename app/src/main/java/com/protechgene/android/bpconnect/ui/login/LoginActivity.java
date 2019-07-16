package com.protechgene.android.bpconnect.ui.login;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.PermissionUtils;
import com.protechgene.android.bpconnect.ui.ResetPassword;
import com.protechgene.android.bpconnect.ui.WebViewScreen;
import com.protechgene.android.bpconnect.ui.base.BaseActivity;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.forgotPassword.ForgotPasswordActivity;
import com.protechgene.android.bpconnect.ui.home.MainActivity;
import com.protechgene.android.bpconnect.ui.signup.SignUpActivity;
import com.protechgene.android.bpconnect.ui.splash.SplashActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;


public class LoginActivity extends BaseActivity implements LoginNavigator {

    @BindView(R.id.edit_email)
    EditText edit_email;
    @BindView(R.id.edit_password)
    EditText edit_password;

    private LoginViewModel mLoginViewModel;

    @Override
    protected int layoutRes() {
        return R.layout.activity_sign_in;

    }

    @Override
    protected void initialize() {
        //Do something here

        mLoginViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getApplication())).get(LoginViewModel.class);
        mLoginViewModel.setNavigator(this);
        new PermissionUtils().requestForPermission(this);
        Intent intent = getIntent();
        Uri data = intent.getData();
        if(data!=null) {
            String code = data.toString();
            String activationCode = code.substring(code.lastIndexOf('/') + 1);
            if (code.contains("activate_account")) {
                showProgress("Please wait...");
                mLoginViewModel.activate_account(activationCode);
            } else if (code.contains("setpassword")){
                openMainActivity_resetPassword(activationCode);
                finish();
            } else {
                startActivity(new Intent(this, SplashActivity.class));
                finish();
            }
        }
    }

    @Override
    @OnClick(R.id.btn_login)
    public void login() {
        String email = edit_email.getText().toString().trim();
        String password = edit_password.getText().toString().trim();

        showProgress("Please wait...");
        mLoginViewModel.login(email, password);
    }

    @OnClick(R.id.terms_and_privacy_link)
    void openlink() {
        startActivity( new Intent(this, WebViewScreen.class));
    }

    @OnClick(R.id.txt_sign_up)
    void signUp() {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }

    @OnClick(R.id.txt_forgot_password)
    void forgotPassword() {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }

    @Override
    public void openMainActivity() {
        hideProgress();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void openMainActivity_resetPassword(String code) {
        hideProgress();
        startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("ispasswordreset", true).putExtra("code", code));
        finish();
    }

    @Override
    public void handleError(Throwable throwable) {
        hideProgress();
        System.out.println("err is "+throwable.toString());
        showSnakeBar(throwable.getMessage());
    }

    @Override
    public void accountActivated(String message) {
        hideProgress();
        showSnakeBar(message);
    }


}

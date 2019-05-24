package com.protechgene.android.bpconnect.ui.login;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.widget.EditText;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.PermissionUtils;
import com.protechgene.android.bpconnect.ui.base.BaseActivity;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.forgotPassword.ForgotPasswordActivity;
import com.protechgene.android.bpconnect.ui.home.MainActivity;
import com.protechgene.android.bpconnect.ui.signup.SignUpActivity;

import butterknife.BindView;
import butterknife.OnClick;


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
    }

    @Override
    @OnClick(R.id.btn_login)
    public void login() {
        String email = edit_email.getText().toString().trim();
        String password = edit_password.getText().toString().trim();

        showProgress("Please wait...");
        mLoginViewModel.login(email, password);
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
    public void handleError(Throwable throwable) {
        hideProgress();
        showSnakeBar(throwable.getMessage());
    }


}

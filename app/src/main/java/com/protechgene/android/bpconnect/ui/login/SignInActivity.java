package com.protechgene.android.bpconnect.ui.login;

import android.content.Intent;
import android.widget.EditText;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.ui.activites.ForgotPasswordActivity;
import com.protechgene.android.bpconnect.ui.activites.MainActivity;
import com.protechgene.android.bpconnect.ui.activites.SignUpActivity;
import com.protechgene.android.bpconnect.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;


public class SignInActivity extends BaseActivity implements LoginNavigator {

    @BindView(R.id.edit_email)
    EditText edit_email;
    @BindView(R.id.edit_password)
    EditText edit_password;

    @Override
    protected int layoutRes() {
        return R.layout.activity_sign_in;
    }

    @Override
    protected void initialize() {
        //Do something here
    }

    @Override
    @OnClick(R.id.btn_login)
    public void login() {
        String email = edit_email.getText().toString();
        String password = edit_password.getText().toString();

        if(mLoginViewModel.isEmailAndPasswordValid(email,password)) {
            showProgress("Please wait...");
            mLoginViewModel.login(email, password);
        }
        else
            showSnakeBar("Please enter a valid email or password.");
    }

    @OnClick(R.id.txt_sign_up)
    void signUp() {
        startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
    }

    @OnClick(R.id.txt_forgot_password)
    void forgotPassword() {
        startActivity(new Intent(SignInActivity.this,ForgotPasswordActivity.class));
    }

    @Override
    public void openMainActivity() {
        startActivity(new Intent(SignInActivity.this,MainActivity.class));
        finish();
    }

    @Override
    public void handleError(Throwable throwable) {
        showSnakeBar(throwable.getMessage());
    }


}

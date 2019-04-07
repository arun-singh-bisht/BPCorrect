package com.protechgene.android.bpconnect.ui.forgotPassword;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Handler;
import android.widget.EditText;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.ui.base.BaseActivity;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;


import butterknife.BindView;
import butterknife.OnClick;


public class ForgotPasswordActivity extends BaseActivity implements ForgotPasswordNavigator  {


    @BindView(R.id.edit_email)
    EditText edit_email;

    private ForgotPasswordViewModel forgotPasswordViewModel;

    @Override
    protected int layoutRes() {
        return R.layout.activity_forgot_password;
    }

    @Override
    protected void initialize() {
        forgotPasswordViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getApplication())).get(ForgotPasswordViewModel.class);
        forgotPasswordViewModel.setNavigator(this);
    }

    @OnClick(R.id.image_back)
    void onBackImageClick() {
        finish();
    }

    @OnClick(R.id.btn_reset_password)
    public void resetPassword() {
        String email = edit_email.getText().toString();

        showProgress("Please wait...");
        forgotPasswordViewModel.resetPassword(email);
    }

    @Override
    public void redirectToLoginPage() {
        hideProgress();
        showSnakeBar("Reset password link sent to registered email address");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
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

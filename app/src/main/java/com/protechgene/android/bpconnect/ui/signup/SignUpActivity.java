package com.protechgene.android.bpconnect.ui.signup;


import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.ui.WebViewScreen;
import com.protechgene.android.bpconnect.ui.base.BaseActivity;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.custom.CustomAlertDialog;
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

    @BindView(R.id.terms_and_condition)
    CheckBox terms_and_condition;
    @BindView(R.id.password_instruction)
    TextView password_instruction;

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


    @OnClick(R.id.terms_and_privacy_link)
    void opentermslink() {
        startActivity( new Intent(this, WebViewScreen.class).putExtra("link","http://protechgenie.in/bpcorrect/#/terms_&_condition" ));
    }

    @OnClick(R.id.privacy_policy_link)
    void openprivacylink() {
        startActivity( new Intent(this, WebViewScreen.class).putExtra("link","http://protechgenie.in/bpcorrect/#/privacy_policy"));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick(R.id.btn_signup)
    public void signUp() {
        String email = edit_email.getText().toString();
        String password = edit_password.getText().toString();
        String passwordConfirm = edit_password_confirm.getText().toString();

        showProgress("Please wait...");
        if (!terms_and_condition.isChecked()) {
            terms_and_condition.setTextColor(getColor(R.color.bp_red));
            hideProgress();
        return;
        } else {
            terms_and_condition.setTextColor(getColor(R.color.black_overlay));
        }

        mSignUpViewModel.registerUser(email, password,passwordConfirm);
    }



    @Override
    public void openLoginScreen() {
        hideProgress();

        CustomAlertDialog.showDialogSingleButton(this, "A verification mail has been sent to your registered email. Please verify that link before login.", new CustomAlertDialog.I_CustomAlertDialog() {
            @Override
            public void onPositiveClick(Dialog dialog, int request_code) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onNegativeClick(Dialog dialog, int request_code) {

            }
        });
    }

    @Override
    public void set_password_instruction() {
        password_instruction.setVisibility(View.VISIBLE);
    }

    @Override
    public void handleError(Throwable throwable) {
        hideProgress();
        showSnakeBar(throwable.getMessage());
    }
}

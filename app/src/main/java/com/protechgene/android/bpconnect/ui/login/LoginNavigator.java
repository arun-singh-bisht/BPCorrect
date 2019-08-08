package com.protechgene.android.bpconnect.ui.login;

public interface LoginNavigator {


    void handleError(Throwable throwable);

    void login();

    void openMainActivity();
    void accountActivated(String message);
    void openMainActivity_resetPassword(String code);

}

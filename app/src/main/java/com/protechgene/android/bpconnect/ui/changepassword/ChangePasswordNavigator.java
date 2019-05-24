package com.protechgene.android.bpconnect.ui.changepassword;

public interface ChangePasswordNavigator {

    void handleError(Throwable throwable);

    void navigateToLogin(String msg);

}

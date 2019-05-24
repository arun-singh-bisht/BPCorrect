package com.protechgene.android.bpconnect.ui.settings;

public interface SettingFragmentNavigator {


    void handleError(Throwable throwable);
    void onSignOut();
    void fetchSwitcher(Boolean status);
    void snackBar(String msg);

}

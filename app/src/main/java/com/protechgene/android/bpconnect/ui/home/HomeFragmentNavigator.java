package com.protechgene.android.bpconnect.ui.home;

public interface HomeFragmentNavigator {


    void handleError(Throwable throwable);

    void showProfileDetails();

    void isProtocolExists(boolean status);

    void historyDataSyncStatus(boolean status);
}

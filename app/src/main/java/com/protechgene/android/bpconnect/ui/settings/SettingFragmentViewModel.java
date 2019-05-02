package com.protechgene.android.bpconnect.ui.settings;


import android.os.AsyncTask;

import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;
import com.protechgene.android.bpconnect.ui.profile.ProfileFragmentNavigator;


public class SettingFragmentViewModel extends BaseViewModel<SettingFragmentNavigator> {


    public SettingFragmentViewModel(Repository repository) {
        super(repository);
    }

    public void signOut()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                getRespository().clearSharedPref();
                getRespository().deleteAllHealthRecords();
                getRespository().deleteAllProtocol();
                getNavigator().onSignOut();
            }
        });

    }
}

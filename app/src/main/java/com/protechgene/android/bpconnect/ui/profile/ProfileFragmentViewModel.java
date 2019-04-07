package com.protechgene.android.bpconnect.ui.profile;


import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;


public class ProfileFragmentViewModel extends BaseViewModel<ProfileFragmentNavigator> {


    public ProfileFragmentViewModel(Repository repository) {
        super(repository);
    }

    public String getUserName()
    {
        return getRespository().getCurrentUserName();
    }

    public String getUserEmail()
    {
        return getRespository().getCurrentUserEmail();
    }

    public String getUserDoB()
    {
        return getRespository().getPatientDOB();
    }

    public String getUserAddress()
    {
        return getRespository().getPatientAddress();
    }

    public String getUserMobile()
    {
        return getRespository().getPatientMobile();
    }
}

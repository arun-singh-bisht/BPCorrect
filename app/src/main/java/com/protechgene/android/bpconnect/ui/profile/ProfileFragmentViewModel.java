package com.protechgene.android.bpconnect.ui.profile;


import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;


public class ProfileFragmentViewModel extends BaseViewModel<ProfileFragmentNavigator> {


    public ProfileFragmentViewModel(Repository repository) {
        super(repository);
    }

   /* public String getUserName()
    {
        return getRespository().getCurrentUserName();
    }*/

    public String getUserFirstName()
    {
        return getRespository().getUserFirstName();
    }

    public String getUserLastName()
    {
        return getRespository().getUserLastName();
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

    public String getUserWeight()
    {
        return getRespository().getPatientWeight();
    }

    public String getUserHeight()
    {
        return getRespository().getPatientHeight();
    }

    public String getUserGender()
    {
        return getRespository().getPatientGender();
    }

    public String getUserAbout()
    {
        return getRespository().getPatientAbout();
    }

    public String getProfilePic()
    {
        return getRespository().getPrefKeyProfileImg();
    }
}

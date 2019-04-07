package com.protechgene.android.bpconnect.ui.profile;


import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.remote.responseModels.profile.ProfileResponse;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class ProfileEditFragmentViewModel extends BaseViewModel<ProfileEditFragmentNavigator> {


    public ProfileEditFragmentViewModel(Repository repository) {
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


    public void updateProfile(String firstname, String gender, String dob, String mobile1, String address1)
    {

        Throwable throwable =null;
        if(firstname==null || firstname.isEmpty())
        {
            throwable = new IllegalArgumentException("Enter your profile name");
            getNavigator().handleError(throwable);
            return;
        }
        if(mobile1==null || mobile1.isEmpty() || mobile1.length()!=10)
        {
            throwable = new IllegalArgumentException("Enter valid mobile number");
            getNavigator().handleError(throwable);
            return;
        }
        if(address1==null || address1.isEmpty())
        {
            throwable = new IllegalArgumentException("Enter your address");
            getNavigator().handleError(throwable);
            return;
        }

        String accessToken = getRespository().getAccessToken();
        String currentUserId = getRespository().getCurrentUserId();

        disposables.add(getRespository().updateProfile(accessToken, currentUserId, firstname, gender, dob, mobile1, address1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .subscribe(new Consumer<ProfileResponse>() {
                    @Override
                    public void accept(ProfileResponse profileResponse) throws Exception {

                        //Save user Details
                        Repository respository = getRespository();
                        respository.setCurrentUserName(profileResponse.getData().get(0).getFirstname());
                        respository.setPatientGender(profileResponse.getData().get(0).getGender());
                        respository.setPatientAddress(profileResponse.getData().get(0).getAddress1());
                        respository.setPatientDOB(profileResponse.getData().get(0).getDob());
                        respository.setPatientMobile(profileResponse.getData().get(0).getMobile1());
                        //respository.setPatientId(profileResponse.getData().get(0).getPatientId().toString());

                        getNavigator().onProfileUpdate();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        getNavigator().handleError(throwable);
                    }
                }));

    }
}

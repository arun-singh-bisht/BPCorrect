package com.protechgene.android.bpconnect.ui.profile;


import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.local.models.ProfileDetailModel;
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

    public void updateProfile(final ProfileDetailModel profileDetailModel)
    {

        Throwable throwable =null;
        if(profileDetailModel.getFirstname()==null || profileDetailModel.getFirstname().isEmpty())
        {
            throwable = new IllegalArgumentException("Enter your profile name");
            getNavigator().handleError(throwable);
            return;
        }
        if(profileDetailModel.getAddress1()==null || profileDetailModel.getAddress1().isEmpty())
        {
            throwable = new IllegalArgumentException("Enter your address");
            getNavigator().handleError(throwable);
            return;
        }
        if(profileDetailModel.getMobile1()==null || profileDetailModel.getMobile1().isEmpty() || profileDetailModel.getMobile1().length()!=10)
        {
            throwable = new IllegalArgumentException("Enter valid mobile number");
            getNavigator().handleError(throwable);
            return;
        }
        if(profileDetailModel.getWeight()==null || profileDetailModel.getWeight().isEmpty())
        {
            throwable = new IllegalArgumentException("Enter your weight");
            getNavigator().handleError(throwable);
            return;
        }
        if(profileDetailModel.getHeight()==null || profileDetailModel.getHeight().isEmpty())
        {
            throwable = new IllegalArgumentException("Enter your height");
            getNavigator().handleError(throwable);
            return;
        }
        if(profileDetailModel.getGender()==null || profileDetailModel.getGender().isEmpty())
        {
            throwable = new IllegalArgumentException("Enter your gender");
            getNavigator().handleError(throwable);
            return;
        }

        String accessToken = getRespository().getAccessToken();
        String currentUserId = getRespository().getCurrentUserId();

        disposables.add(getRespository().updateProfile(accessToken, currentUserId, profileDetailModel.getFirstname(), profileDetailModel.getGender(), profileDetailModel.getDob(), profileDetailModel.getMobile1(), profileDetailModel.getAddress1(),profileDetailModel.getWeight(),profileDetailModel.getHeight(),profileDetailModel.getAbout())
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
                        respository.setPatientWeight(profileResponse.getData().get(0).getWeight());
                        respository.setPatientHeight(profileResponse.getData().get(0).getHeight());
                        respository.setPatientAbout(profileResponse.getData().get(0).getAddress2());

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

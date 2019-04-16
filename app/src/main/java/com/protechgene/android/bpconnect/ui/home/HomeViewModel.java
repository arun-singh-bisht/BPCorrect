package com.protechgene.android.bpconnect.ui.home;


import android.os.AsyncTask;

import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.remote.responseModels.profile.ProfileResponse;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HomeViewModel extends BaseViewModel<HomeFragmentNavigator> {


    public HomeViewModel(Repository repository) {
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

    public void getProfileDetails()
    {


        String patientId = getRespository().getPatientId();
        if(patientId!=null)
        {
            getNavigator().showProfileDetails();
            return;
        }

        String accessToken = getRespository().getAccessToken();
        String currentUserId = getRespository().getCurrentUserId();

        disposables.add(getRespository().profileDetails(accessToken,currentUserId)
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
                        respository.setPatientId(profileResponse.getData().get(0).getPatientId().toString());

                        getNavigator().showProfileDetails();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        getNavigator().handleError(throwable);
                    }
                }));

    }

    public void insertDummyValue()
    {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                HealthReading healthReading = new HealthReading(0,115+"",83+"",76+"",System.currentTimeMillis()+"",false);
                /*healthReading.setSystolic(115+"");
                healthReading.setDiastolic(83+"");
                healthReading.setPulse(76+"");
                healthReading.setLogTime(System.currentTimeMillis()+"");
                healthReading.setSync(false);*/
                getRespository().addNewHealthRecord(healthReading);
            }
        });


    }
}

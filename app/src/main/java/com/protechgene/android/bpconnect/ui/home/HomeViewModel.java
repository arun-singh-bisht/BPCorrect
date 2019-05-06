package com.protechgene.android.bpconnect.ui.home;


import android.os.AsyncTask;

import com.protechgene.android.bpconnect.Utils.DateUtils;
import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;
import com.protechgene.android.bpconnect.data.remote.responseModels.profile.ProfileResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.protocol.Data;
import com.protechgene.android.bpconnect.data.remote.responseModels.protocol.GetProtocolResponse;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

import java.util.List;

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
                        respository.setPatientWeight(profileResponse.getData().get(0).getWeight());
                        respository.setPatientHeight(profileResponse.getData().get(0).getHeight());
                        respository.setPatientAbout(profileResponse.getData().get(0).getAddress2());

                        getNavigator().showProfileDetails();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        getNavigator().handleError(throwable);
                    }
                }));

    }


    public void checkActiveProtocol()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<ProtocolModel> allProtocol = getRespository().getAllProtocol();

                if(allProtocol==null || allProtocol.size()==0)
                    getProtocolFromServer();
                else
                    getNavigator().isProtocolExists(true);
            }
        });
    }

    private void getProtocolFromServer()
    {
        String accessToken = getRespository().getAccessToken();
        String userId = getRespository().getCurrentUserId();

        disposables.add(getRespository().getProtocolDetail(accessToken,userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .subscribe(new Consumer<GetProtocolResponse>() {
                    @Override
                    public void accept(GetProtocolResponse protocolResponse) throws Exception {

                        //Throwable throwable = new Throwable("Data Syn to server");
                        //getNavigator().handleError(throwable);
                        if(protocolResponse.getData().get(0).getProtocolId() == null) {
                            getNavigator().isProtocolExists(false);
                        }
                        else
                        {
                            Data data = protocolResponse.getData().get(0);

                            String startDate = data.getStartDate();
                            startDate = DateUtils.convertMillisecToDateTime(Long.parseLong(startDate) * 1000,"MMM dd,yyyy");

                            String endDate = data.getEndDate();
                            endDate = DateUtils.convertMillisecToDateTime(Long.parseLong(endDate) * 1000,"MMM dd,yyyy");

                            final ProtocolModel protocolModel = new ProtocolModel(0,startDate,endDate,data.getMorningAlarm(),data.getEveningAlarm(),true);
                            protocolModel.setProtocolCode(data.getProtocolId());

                            //Save new protocol in DB
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    getRespository().addNewProtocol(protocolModel);
                                    getNavigator().isProtocolExists(true);
                                }
                            });
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        getNavigator().isProtocolExists(false);
                    }
                }));
    }
}

package com.protechgene.android.bpconnect.ui.home;


import android.os.AsyncTask;

import com.protechgene.android.bpconnect.Utils.DateUtils;
import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;
import com.protechgene.android.bpconnect.data.remote.responseModels.BpReadings.ActualValue;
import com.protechgene.android.bpconnect.data.remote.responseModels.BpReadings.BpReadingsResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.BpReadings.Chartdata;
import com.protechgene.android.bpconnect.data.remote.responseModels.profile.ProfileResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.protocol.Data;
import com.protechgene.android.bpconnect.data.remote.responseModels.protocol.GetProtocolResponse;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HomeViewModel extends BaseViewModel<HomeFragmentNavigator> {


    public HomeViewModel(Repository repository) {
        super(repository);
    }

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

    public String getProfilePic()
    {
        return getRespository().getPrefKeyProfileImg();
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
                        respository.setUserFirstName(profileResponse.getData().get(0).getFirstname());
                        respository.setUserLastName(profileResponse.getData().get(0).getLastname());
                        respository.setPatientGender(profileResponse.getData().get(0).getGender());
                        respository.setPatientAddress(profileResponse.getData().get(0).getAddress1());
                        respository.setPatientDOB(profileResponse.getData().get(0).getDob());
                        respository.setPatientMobile(profileResponse.getData().get(0).getMobile1());
                        respository.setPatientId(profileResponse.getData().get(0).getPatientId().toString());
                        respository.setPatientWeight(profileResponse.getData().get(0).getWeight());
                        respository.setPatientHeight(profileResponse.getData().get(0).getHeight());
                        respository.setPatientAbout(profileResponse.getData().get(0).getAddress2());
                        respository.setPrefKeyProfileImg(profileResponse.getData().get(0).getPhoto_url());

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

    public void synHistoryData()
    {

        if(!getRespository().isHistoryDataSync())
        {
            String accessToken = getRespository().getAccessToken();
            String currentUserId = getRespository().getCurrentUserId();
            String fromDay = "1546300800"; // 1 January 2019 00:00:00
            String toDay = (System.currentTimeMillis() + (5*60*60*1000))/1000 +"";
            String noDay = "30";

            disposables.add(getRespository().getBpReadings(accessToken, currentUserId, fromDay, toDay, noDay)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {

                        }
                    })
                    .subscribe(new Consumer<BpReadingsResponse>() {
                        @Override
                        public void accept(BpReadingsResponse bpReadingsResponse) throws Exception {

                            //Save user Details
                            List<Chartdata> chartdata = bpReadingsResponse.getData().get(0).getChartdata();

                            List<ActualValue> actualValues = new ArrayList<>();
                            for(int i=0;i<chartdata.size();i++)
                            {
                                actualValues.addAll(chartdata.get(i).getActualValues());
                            }

                            //Save InDB
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {

                                    for(int i=0;i<actualValues.size();i++)
                                    {
                                        ActualValue actualValue = actualValues.get(i);

                                        HealthReading healthReading = new HealthReading();
                                        healthReading.setSystolic(actualValue.getSBP());
                                        healthReading.setDiastolic(actualValue.getDBP());
                                        healthReading.setLogTime((Long.parseLong(actualValue.getTimestamp())*1000)+"");
                                        healthReading.setReading_time((Long.parseLong(actualValue.getTimestamp())*1000));
                                        healthReading.setPulse(actualValue.getPULSE());
                                        healthReading.setSync(true);
                                        healthReading.setIs_abberant(actualValue.getIs_abberant());
                                        healthReading.setProtocol_id(actualValue.getProtocol_id());

                                        getRespository().addNewHealthRecord(healthReading);
                                    }

                                    getNavigator().historyDataSyncStatus(true);
                                    getRespository().setHistoryDataSyncStatus(true);
                                }
                            });


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                            getNavigator().historyDataSyncStatus(false);
                        }
                    }));
        }
    }
}

package com.protechgene.android.bpconnect.ui.home;


import android.app.AlarmManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
import com.protechgene.android.bpconnect.ui.reminder.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HomeViewModel extends BaseViewModel<HomeFragmentNavigator> {

    public String morning_alarm = null;
    public String evening_alarm = null;
    public HomeViewModel(Repository repository) {
        super(repository);
    }


    public String getUserFirstName()
    {
        return getRespository().getUserFirstName();
    }

    // update by rajat
    public String getAddress()
    {
        return getRespository().getPatientAddress()+" "+getRespository().getPrefKeyPatientCity()+" "+getRespository().getPrefKeyPatientState();
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

    public boolean getprotocol() {
        return getRespository().getAllProtocol().size() > 0? true: false;
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
                        // edit by sohit covertdob
                        respository.setPatientDOB(convertdob(profileResponse.getData().get(0).getDob()));
                        respository.setPatientMobile(profileResponse.getData().get(0).getMobile1());
                        respository.setPatientId(profileResponse.getData().get(0).getPatientId()+"");
                        respository.setPatientWeight(profileResponse.getData().get(0).getWeight());
                        respository.setPatientHeight(profileResponse.getData().get(0).getHeight());
                        respository.setPatientAbout(profileResponse.getData().get(0).getAddress2());
                        respository.setPrefKeyProfileImg(profileResponse.getData().get(0).getPhoto_url());
                        respository.setPrefKeyOrgName(profileResponse.getData().get(0).getOrg_name());
                        respository.setPrefKeyProviderName(profileResponse.getData().get(0).getProvider());
                        // edit by rajat
                        respository.setPrefKeyPatientState(profileResponse.getData().get(0).getState());
                        respository.setPrefKeyPatientCity(profileResponse.getData().get(0).getCity());
                        respository.setPrefKeyPatientZipcode(profileResponse.getData().get(0).getZipcode());
                        respository.setPatientAddress(profileResponse.getData().get(0).getAddress1());
                       // respository.setHealthOrgName(profileResponse.getData().get(0).getOrg_name());

                        getNavigator().showProfileDetails();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        getNavigator().handleError(throwable);
                    }
                }));

    }

    // edit by sohit
    public String convertdob(String dob) {
        if(dob==null)
            return null;
        String ar[] = dob.split("-");
        return ar[1]+"-"+ar[2]+"-"+ar[0];
    }


    public void synHistoryData(final Context context)
    {

        if(!getRespository().isHistoryDataSync())
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

                            if(protocolResponse.getData() == null || protocolResponse.getData().size()==0 || protocolResponse.getData().get(0).getProtocolId() == null) {
                                //getNavigator().isProtocolExists(false,null);
                            }
                            else {
                                Data data = protocolResponse.getData().get(0);

                                String startDate = data.getStartDate();
                                startDate = DateUtils.convertMillisecToDateTime(Long.parseLong(startDate) * 1000, "MMM dd,yyyy");

                                String endDate = data.getEndDate();
                                endDate = DateUtils.convertMillisecToDateTime(Long.parseLong(endDate) * 1000, "MMM dd,yyyy");

                                final ProtocolModel protocolModel = new ProtocolModel(0, startDate, endDate, data.getMorningAlarm(), data.getEveningAlarm(), true,true,true);
                                protocolModel.setProtocolCode(data.getProtocolId());
                                morning_alarm = data.getMorningAlarm();
                                evening_alarm = data.getEveningAlarm();

                                set_first_login_alarm(context);

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        //getRespository().addNewProtocol(protocolModel);
                                        //Remove all Previous Data
                                        getRespository().deleteAllProtocol();
                                        getRespository().deleteAllHealthRecords();
                                        //Save new protocol in DB
                                        getRespository().addNewProtocol(protocolModel);
                                    }
                                });

                            }

                            synBPReadingData();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                            //getNavigator().handleError(throwable);
                            getNavigator().historyDataSyncStatus(false);
                        }
                    }));
        }else
        {
            getNavigator().historyDataSyncStatus(true);
        }

    }

    void set_first_login_alarm(Context context) {
        Log.d("set_first_login_alarm",morning_alarm+" "+evening_alarm);
        if (morning_alarm != null && evening_alarm != null && !morning_alarm.isEmpty() && !evening_alarm.isEmpty() ) {
            String currrentTime = DateUtils.getDateString(0, "HH:mm");
            long k = DateUtils.compareTimeString(morning_alarm, currrentTime, "HH:mm");
            long m = DateUtils.compareTimeString(evening_alarm, currrentTime, "HH:mm");
            int datOffset = 0;

            if (m <= 0) {
                //Today Evening Time has passed.Alarm should be set from next day(morning)
                datOffset = 1;
            } else if (m > 0 && k <= 0) {
                //Today morning alarm time has passed but evening alarm is still to go. Alarm should be set from today(evening)
                datOffset = 0;
            } else if (k > 0) {
                //Today morning alarm is still to go. Alarm should be set from today(morning)
                datOffset = 0;
            }
            //Set Alarm
            String[] split = null;
            if (datOffset == 0) {

                if (k > 0) {
                    //Set Today Morning Alarm
                    split = morning_alarm.split(":");
                } else {
                    //Set Today Evening Alarm
                    split = evening_alarm.split(":");
                }

            } else if (datOffset == 1) {
                //Set Next Day Morning Alarm
                split = morning_alarm.split(":");

            }
            AlarmReceiver.setAlarm(context, Integer.parseInt(split[0]), Integer.parseInt(split[1]), datOffset);
        }
    }


    void setFirstTimeUser() {
        getRespository().setFirstTimeuser("false");
    }

    boolean getFirstTimeUser() {
        return getRespository().getFirstTimeuser().equals("true")? true:false;
    }

    public void synBPReadingData()
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


                                getRespository().setHistoryDataSyncStatus(true);
                                getNavigator().historyDataSyncStatus(true);
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

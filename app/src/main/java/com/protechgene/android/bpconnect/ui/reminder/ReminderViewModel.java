package com.protechgene.android.bpconnect.ui.reminder;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.protechgene.android.bpconnect.Utils.DateUtils;
import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;
import com.protechgene.android.bpconnect.data.remote.responseModels.AddBPReading.AddBpReadingResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.profile.ProfileResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.protocol.Data;
import com.protechgene.android.bpconnect.data.remote.responseModels.protocol.GetProtocolResponse;
import com.protechgene.android.bpconnect.ui.ApplicationBPConnect;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;
import com.protechgene.android.bpconnect.ui.custom.TimePickerFragment;

import java.util.Calendar;
import java.util.List;

import butterknife.internal.Utils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class ReminderViewModel extends BaseViewModel<ReminderFragmentNavigator> implements TimePickerFragment.TimePickedListener {

    private Context context;
    private String selectedMorningTime;
    private String selectedEveningTime;
    private ProtocolModel activeProtocol;

    public ReminderViewModel(Repository repository) {
        super(repository);
    }

    public void checkActiveProtocol()
    {
        getNavigator().showSearchingProgress();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<ProtocolModel> allProtocol = getRespository().getAllProtocol();

                if(allProtocol==null || allProtocol.size()==0)
                    getProtocolFromServer();
                else
                    getNavigator().isProtocolExists(true,allProtocol.get(0));
            }
        });

    }

    public void createProtocol(Context context)
    {
        this.context = context;
        TimePickerFragment picker = new TimePickerFragment(this,1001,"Morning Reminder Time",6,0);
        picker.show(((Activity)context).getFragmentManager(), "timePicker");
    }

    public void updateMorningAlarmTime(Context context,ProtocolModel activeProtocol)
    {
        this.context = context;
        this.activeProtocol = activeProtocol;
        String[] time = activeProtocol.getMorningReadingTime().split(":");
        int hour = Integer.parseInt(time[0]);
        int min = Integer.parseInt(time[1]);
        TimePickerFragment picker = new TimePickerFragment(this,1003,"Morning Reminder Time",hour,min);
        picker.show(((Activity)context).getFragmentManager(), "timePicker");
    }

    public void updateEveningAlarmTime(Context context,ProtocolModel activeProtocol)
    {
        this.context = context;
        this.activeProtocol = activeProtocol;
        String[] time = activeProtocol.getEveningReadingTime().split(":");
        int hour = Integer.parseInt(time[0]);
        int min = Integer.parseInt(time[1]);
        TimePickerFragment picker = new TimePickerFragment(this,1004,"Evening Reminder Time",hour,min);
        picker.show(((Activity)context).getFragmentManager(), "timePicker");
    }

    public void deleteProtocol(final Context context, ProtocolModel protocolModel)
    {
        //Delete protocol from Local DB
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AlarmReceiver.deleteAllAlarm(context);
                getRespository().deleteAllProtocol();
            }
        });
        //Delete Protocol from server
        sendProtocolToServer(protocolModel);
    }

    @Override
    public void onTimePicked(Calendar time, int id) {
        int HOUR_OF_DAY = time.get(Calendar.HOUR_OF_DAY);
        int MINUTE = time.get(Calendar.MINUTE);

        if(id == 1001)
        {
            //Selected Morning Time
            if(HOUR_OF_DAY>= ApplicationBPConnect.PROTOCOL_MORNING_MINIMUM_TIME && HOUR_OF_DAY<ApplicationBPConnect.PROTOCOL_MORNING_MAXIMUM_TIME)
            {
                selectedMorningTime = HOUR_OF_DAY+":"+MINUTE;

                TimePickerFragment picker = new TimePickerFragment(this,1002,"Evening Reminder Time",18,0);
                picker.show(((Activity)context).getFragmentManager(), "timePicker");
            }else
            {
                //Show invalid Time Message
                getNavigator().invalidTimeSelection("Morning Alarm should be between 4:00 AM to 12:00 PM");
            }
        }else if(id == 1002)
        {
            //Selected Evening Time
            if(HOUR_OF_DAY>=ApplicationBPConnect.PROTOCOL_EVENING_MINIMUM_TIME && HOUR_OF_DAY<ApplicationBPConnect.PROTOCOL_EVENING_MAXIMUM_TIME)
            {
                selectedEveningTime = HOUR_OF_DAY+":"+MINUTE;

                String currrentTime = DateUtils.getDateString(0, "HH:mm");
                long k = DateUtils.compareTimeString(selectedMorningTime, currrentTime,"HH:mm");
                Log.d("compareTimeString","compareTimeString :"+k);

                int i =0;
                if(k<=0)
                    i = 1;
                else
                    i = 0;

                //Remove this
                if(ApplicationBPConnect.isTodayIncluded)
                    i =0;

                String startDate = DateUtils.getDateString(i, "MMM dd,yyyy");
                String endDate = DateUtils.getDateString(6+i, "MMM dd,yyyy");

                final ProtocolModel protocolModel = new ProtocolModel(0,startDate,endDate,selectedMorningTime,selectedEveningTime,true);
                String protocolCode = startDate+"_"+endDate+"_"+getRespository().getPatientId()+"_"+System.currentTimeMillis();
                protocolModel.setProtocolCode(protocolCode);

                getNavigator().onProtocolCreated(protocolModel);

                //Set Alarm
                String[] split = selectedMorningTime.split(":");
                AlarmReceiver.setAlarm(context,Integer.parseInt(split[0]),Integer.parseInt(split[1]),i);

                //Save new protocol in DB
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        getRespository().addNewProtocol(protocolModel);
                    }
                });

                //Send created Protocol to server
                sendProtocolToServer(protocolModel);

            }else
            {
                //Show invalid Time Message
                getNavigator().invalidTimeSelection("Evening Alarm should be between 4:00 PM to 12:00 AM");
            }
        }else if(id == 1003)
        {
            //Selected Morning Time
            if(HOUR_OF_DAY>=4 && HOUR_OF_DAY<12)
            {
                final String selectedMorningTime = HOUR_OF_DAY+":"+MINUTE;
                activeProtocol.setMorningReadingTime(selectedMorningTime);
                getNavigator().onProtocolCreated(activeProtocol);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        getRespository().deleteAllProtocol();
                        getRespository().addNewProtocol(activeProtocol);
                    }
                });

            }else
            {
                //Show invalid Time Message
                getNavigator().invalidTimeSelection("Morning Alarm should be between 4:00 AM to 12:00 PM");
            }
        }else if(id == 1004)
        {
            //Selected Evening Time
            if(HOUR_OF_DAY>=16 && HOUR_OF_DAY<24)
            {
                String selectedEveningTime = HOUR_OF_DAY+":"+MINUTE;
                activeProtocol.setEveningReadingTime(selectedEveningTime);
                getNavigator().onProtocolCreated(activeProtocol);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        getRespository().deleteAllProtocol();
                        getRespository().addNewProtocol(activeProtocol);
                    }
                });

            }else
            {
                //Show invalid Time Message
                getNavigator().invalidTimeSelection("Evening Alarm should be between 4:00 PM to 12:00 AM");
            }
        }
    }


    private void sendProtocolToServer(final ProtocolModel protocolModel)
    {
        String accessToken = getRespository().getAccessToken();
        String userId = getRespository().getCurrentUserId();

        String startdate = DateUtils.convertDateStringToMillisec(protocolModel.getStartDay(), "MMM dd,yyyy");
        String endDate = DateUtils.convertDateStringToMillisec(protocolModel.getEndDay(), "MMM dd,yyyy");

        disposables.add(getRespository().createProtocol(accessToken,userId,startdate,endDate,protocolModel.getProtocolCode(),protocolModel.getMorningReadingTime(),protocolModel.getEveningReadingTime())
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

                        //Throwable throwable = new Throwable("Data Syn to server");
                        //getNavigator().handleError(throwable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        //getNavigator().handleError(throwable);
                    }
                }));
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
                        if(protocolResponse.getData().get(0).getProtocolId() == null)
                            getNavigator().isProtocolExists(false,null);
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
                                    getNavigator().isProtocolExists(true,protocolModel);
                                }
                            });
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        //getNavigator().handleError(throwable);
                        getNavigator().isProtocolExists(false,null);
                    }
                }));
    }


    /**
     * This is the real time /wall clock time
     * @param context
     */
    public static void scheduleRepeatingRTCNotification(Context context, String hour, String min) {
        //get calendar instance to be able to select what time notification should be scheduled
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //Setting time of the day (8am here) when notification will be sent every day (default)
        calendar.set(Calendar.HOUR_OF_DAY,
                Integer.getInteger(hour, 8),
                Integer.getInteger(min, 0));

        //Setting intent to class where Alarm broadcast message will be handled
        Intent intent = new Intent(context, AlarmReceiver.class);
        //Setting alarm pending intent
        PendingIntent alarmIntentRTC = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //getting instance of AlarmManager service
        AlarmManager alarmManagerRTC = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        //Setting alarm to wake up device every day for clock time.
        //AlarmManager.RTC_WAKEUP is responsible to wake up device for sure, which may not be good practice all the time.
        // Use this when you know what you're doing.
        //Use RTC when you don't need to wake up device, but want to deliver the notification whenever device is woke-up
        //We'll be using RTC.WAKEUP for demo purpose only
        alarmManagerRTC.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntentRTC);
    }

    public void onDestroy()
    {
        context = null;
    }
}

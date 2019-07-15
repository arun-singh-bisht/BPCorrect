package com.protechgene.android.bpconnect.ui.reminder;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.protechgene.android.bpconnect.Utils.DateUtils;
import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;
import com.protechgene.android.bpconnect.data.remote.responseModels.profile.ProfileResponse;
import com.protechgene.android.bpconnect.ui.ApplicationBPConnect;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;
import com.protechgene.android.bpconnect.ui.custom.SupportedTimePickerFragment;

import java.util.Calendar;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.protechgene.android.bpconnect.ui.ApplicationBPConnect.PROTOCOL_DAYS;


public class ActiveProtocolViewModel extends BaseViewModel<ActiveProtocolFragmentNavigator> implements SupportedTimePickerFragment.TimePickedListener {

    private int CREATE_PROTOCOL_MORNING_TIME = 1001;
    private int CREATE_PROTOCOL_EVENING_TIME = 1002;
    private int UPDATE_PROTOCOL_MORNING_TIME = 1003;
    private int UPDATE_PROTOCOL_EVENING_TIME = 1004;


    private Context context;
    private String selectedMorningTime;
    private String selectedEveningTime;
    private ProtocolModel activeProtocol;

    public ActiveProtocolViewModel(Repository repository) {
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
                    getNavigator().isProtocolExists(false,null);
                else
                    getNavigator().isProtocolExists(true,allProtocol.get(0));
            }
        });

    }

    public void createProtocol(Context context)
    {
        this.context = context;
        /*TimePickerFragment picker = new TimePickerFragment(this,1001,"Morning Reminder Time",6,0);
        picker.show(((Activity)context).getFragmentManager(), "timePicker");*/

        SupportedTimePickerFragment picker = new SupportedTimePickerFragment(this,CREATE_PROTOCOL_MORNING_TIME,"Morning Reminder Time",6,0);
        picker.show(((Activity)context).getFragmentManager(), "timePicker");
    }

    public void updateMorningAlarmTime(Context context,ProtocolModel activeProtocol)
    {
        this.context = context;
        this.activeProtocol = activeProtocol;
        String[] time = activeProtocol.getMorningReadingTime().split(":");
        int hour = Integer.parseInt(time[0]);
        int min = Integer.parseInt(time[1]);
        SupportedTimePickerFragment picker = new SupportedTimePickerFragment(this,UPDATE_PROTOCOL_MORNING_TIME,"Morning Reminder Time",hour,min);
        picker.show(((Activity)context).getFragmentManager(), "timePicker");
    }

    public void updateEveningAlarmTime(Context context,ProtocolModel activeProtocol)
    {
        this.context = context;
        this.activeProtocol = activeProtocol;
        String[] time = activeProtocol.getEveningReadingTime().split(":");
        int hour = Integer.parseInt(time[0]);
        int min = Integer.parseInt(time[1]);
        SupportedTimePickerFragment picker = new SupportedTimePickerFragment(this,UPDATE_PROTOCOL_EVENING_TIME,"Evening Reminder Time",hour,min);
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

        if(id == CREATE_PROTOCOL_MORNING_TIME)
        {
            //Selected Morning Time
            if(HOUR_OF_DAY>= ApplicationBPConnect.PROTOCOL_MORNING_MINIMUM_TIME && HOUR_OF_DAY<ApplicationBPConnect.PROTOCOL_MORNING_MAXIMUM_TIME)
            {
                selectedMorningTime = HOUR_OF_DAY+":"+MINUTE;

                SupportedTimePickerFragment picker = new SupportedTimePickerFragment(this,CREATE_PROTOCOL_EVENING_TIME,"Evening Reminder Time",18,0);
                picker.show(((Activity)context).getFragmentManager(), "timePicker");
            }else
            {
                //Show invalid Time Message
                getNavigator().invalidTimeSelection("Morning Alarm should be between 4:00 AM to 12:00 PM");
            }
        }else if(id == CREATE_PROTOCOL_EVENING_TIME)
        {
            //Selected Evening Time
            if(HOUR_OF_DAY>=ApplicationBPConnect.PROTOCOL_EVENING_MINIMUM_TIME && HOUR_OF_DAY<ApplicationBPConnect.PROTOCOL_EVENING_MAXIMUM_TIME)
            {
                selectedEveningTime = HOUR_OF_DAY+":"+MINUTE;

                String currrentTime = DateUtils.getDateString(0, "HH:mm");
                long k = DateUtils.compareTimeString(selectedMorningTime, currrentTime,"HH:mm");
                Log.d("compareTimeString","compareTimeString :"+k);

                int datOffset =0;
                if(k<=0)
                    datOffset = 1;
                else
                    datOffset = 0;

                //Remove this
                //if(ApplicationBPConnect.isTodayIncluded)
                  //  datOffset =0;

                //String startDate = DateUtils.getDateString(datOffset, "MMM dd,yyyy");
                //String endDate = DateUtils.getDateString(6+datOffset, "MMM dd,yyyy");

                String startDate = DateUtils.getDateString(0, "MMM dd,yyyy");
                String endDate = DateUtils.getDateString((PROTOCOL_DAYS-1)+0, "MMM dd,yyyy");

                final ProtocolModel protocolModel = new ProtocolModel(0,startDate,endDate,selectedMorningTime,selectedEveningTime,true,true,true);
                String protocolCode = startDate+"_"+endDate+"_"+getRespository().getPatientId()+"_"+System.currentTimeMillis();
                protocolModel.setProtocolCode(protocolCode);

                getNavigator().onProtocolCreated(protocolModel);

                //Set Alarm
                if(datOffset==0)
                {
                    String[] split = selectedMorningTime.split(":");
                    AlarmReceiver.setAlarm(context,Integer.parseInt(split[0]),Integer.parseInt(split[1]),0);
                }else if(datOffset==1)
                {
                    String[] split = selectedEveningTime.split(":");
                    AlarmReceiver.setAlarm(context,Integer.parseInt(split[0]),Integer.parseInt(split[1]),0);
                }

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
        }else if(id == UPDATE_PROTOCOL_MORNING_TIME)
        {
            //Selected Morning Time
            if(HOUR_OF_DAY>=ApplicationBPConnect.PROTOCOL_MORNING_MINIMUM_TIME  && HOUR_OF_DAY<ApplicationBPConnect.PROTOCOL_MORNING_MAXIMUM_TIME)
            {
                final String previousMorningAlarmTime = activeProtocol.getMorningReadingTime();

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

                //Send created Protocol to server
                sendProtocolToServer(activeProtocol);

                //update Alarm
                String todayDate = DateUtils.getDateString(0, "MMM dd,yyyy");
                String protocolStartDate = activeProtocol.getStartDay();
                long k = DateUtils.compareTimeString(todayDate, protocolStartDate,"MMM dd,yyyy");
                if(k<0)
                {
                    //protocol will start from next day
                    //Remove All Old Alarms and set New
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            AlarmReceiver.deleteAllAlarm(context);
                            String[] split = selectedMorningTime.split(":");
                            AlarmReceiver.setAlarm(context,Integer.parseInt(split[0]),Integer.parseInt(split[1]),1);
                        }
                    });


                }else
                {
                    //protocol has already started
                    String currrentTime = DateUtils.getDateString(0, "HH:mm");
                    k = DateUtils.compareTimeString(currrentTime, previousMorningAlarmTime,"HH:mm");
                    if(k<0)
                    {
                        //Morning alarm will go off in few hours, you can still change the alarm time
                        //Remove All Old Alarms and set New
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                AlarmReceiver.deleteAllAlarm(context);
                                String[] split = selectedMorningTime.split(":");
                                AlarmReceiver.setAlarm(context,Integer.parseInt(split[0]),Integer.parseInt(split[1]),0);
                            }
                        });

                    }
                }
            }else
            {
                //Show invalid Time Message
                getNavigator().invalidTimeSelection("Morning Alarm should be between 4:00 AM to 12:00 PM");
            }
        }else if(id == UPDATE_PROTOCOL_EVENING_TIME)
        {
            //Selected Evening Time
            if(HOUR_OF_DAY>=ApplicationBPConnect.PROTOCOL_EVENING_MINIMUM_TIME && HOUR_OF_DAY<ApplicationBPConnect.PROTOCOL_EVENING_MAXIMUM_TIME)
            {
                final String previousEveningAlarmTime = activeProtocol.getEveningReadingTime();

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

                //Send created Protocol to server
                sendProtocolToServer(activeProtocol);

                //update Alarm
                String todayDate = DateUtils.getDateString(0, "MMM dd,yyyy");
                String protocolStartDate = activeProtocol.getStartDay();
                long k = DateUtils.compareTimeString(todayDate, protocolStartDate,"MMM dd,yyyy");
                if(k<0)
                {
                    //protocol will start from next day
                    //Do Nothing...
                }else
                {
                    //protocol has already started
                    String currrentTime = DateUtils.getDateString(0, "HH:mm");
                    String morningReadingTime = activeProtocol.getMorningReadingTime();

                    long t1 = DateUtils.compareTimeString(currrentTime, morningReadingTime,"HH:mm");
                    long t2 = DateUtils.compareTimeString(currrentTime, previousEveningAlarmTime,"HH:mm");
                    if(t1>0 && t2<0)
                    {
                        //Evening alarm will go off in few hours, you can still change the alarm time
                        //Remove All Old Alarms and set New
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                AlarmReceiver.deleteAllAlarm(context);
                                String[] split = selectedEveningTime.split(":");
                                AlarmReceiver.setAlarm(context,Integer.parseInt(split[0]),Integer.parseInt(split[1]),0);
                            }
                        });
                    }
                }

            }else
            {
                //Show invalid Time Message
                getNavigator().invalidTimeSelection("Evening Alarm should be between 4:00 PM to 12:00 AM");
            }
        }
    }


    public void saveProtocol(final ProtocolModel activeProtocol)
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                getRespository().deleteAllProtocol();
                getRespository().addNewProtocol(activeProtocol);
            }
        });
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

                        Throwable throwable = new Throwable("Data Syn to server");
                        getNavigator().handleError(throwable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        getNavigator().handleError(throwable);
                    }
                }));
    }

    public void onDestroy()
    {
        context = null;
    }
}

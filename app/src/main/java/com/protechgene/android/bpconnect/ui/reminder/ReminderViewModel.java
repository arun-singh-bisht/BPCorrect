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
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;
import com.protechgene.android.bpconnect.ui.custom.TimePickerFragment;

import java.util.Calendar;
import java.util.List;


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

    public void deleteProtocol()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                getRespository().deleteAllProtocol();
            }
        });
    }

    @Override
    public void onTimePicked(Calendar time, int id) {
        int HOUR_OF_DAY = time.get(Calendar.HOUR_OF_DAY);
        int MINUTE = time.get(Calendar.MINUTE);

        if(id == 1001)
        {
            //Selected Morning Time
            if(HOUR_OF_DAY>=4 && HOUR_OF_DAY<12)
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
            if(HOUR_OF_DAY>=16 && HOUR_OF_DAY<24)
            {
                selectedEveningTime = HOUR_OF_DAY+":"+MINUTE;
                String startDate = DateUtils.getDateString(1, "MMM dd,yyyy");
                String endDate = DateUtils.getDateString(7, "MMM dd,yyyy");

                final ProtocolModel protocolModel = new ProtocolModel(0,startDate,endDate,selectedMorningTime,selectedEveningTime,true);
                getNavigator().onProtocolCreated(protocolModel);
                //Save new protocol in DB
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        getRespository().addNewProtocol(protocolModel);
                    }
                });
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

    public void setAlarm(Context context,int hour_of_day,int min)
    {
        //Create alarm manager
        AlarmManager alarmMgr0 = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        //Create pending intent & register it to your alarm notifier class
        Intent intent0 = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent0 = PendingIntent.getBroadcast(context, 1101, intent0, PendingIntent.FLAG_UPDATE_CURRENT);

        //set timer you want alarm to work (here I have set it to 7.20pm)
        //Intent intent0 = new Intent(this, OldEntryRemover.class);
        Calendar timeOff9 = Calendar.getInstance();
        timeOff9.set(Calendar.HOUR_OF_DAY, hour_of_day);
        timeOff9.set(Calendar.MINUTE, min);
        timeOff9.set(Calendar.SECOND, 0);

        //set that timer as a RTC Wakeup to alarm manager object
        alarmMgr0.set(AlarmManager.RTC_WAKEUP, timeOff9.getTimeInMillis(), pendingIntent0);
        Log.d("setAlarm","setAlarm at "+hour_of_day+":"+min);
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

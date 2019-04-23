package com.protechgene.android.bpconnect.ui.reminder;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.protechgene.android.bpconnect.Utils.DateUtils;
import com.protechgene.android.bpconnect.Utils.NotificationUtil;
import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;
import com.protechgene.android.bpconnect.ui.home.MainActivity;

import java.util.Calendar;

public class AlarmReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String alarmTime = intent.getStringExtra("AlarmTime");
        Log.d("AlarmReceiver","onReceive "+alarmTime);

        //Intent to invoke app when click on notification.
        //In this sample, we want to start/launch this sample app when user clicks on notification
        Intent intentToRepeat = new Intent(context, MainActivity.class);
        intentToRepeat.putExtra("isAlarmFired",true);
        intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Show Notification status bar
        Log.d("AlarmReceiver","Show Notification");
        new NotificationUtil().buildLocalNotification(context,intentToRepeat,1001,"Time to check BP");

        //Play Sound in loop
        //AlarmSound.getInstance(context).playSound();

        //Set Next Alarm
        final Repository repository = Repository.getInstance((Application) context.getApplicationContext());
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ProtocolModel protocolModel = repository.getAllProtocol().get(0);
                String morningReadingTime = protocolModel.getMorningReadingTime();
                String eveningReadingTime = protocolModel.getEveningReadingTime();
                String endDay = protocolModel.getEndDay();

                if(alarmTime.equalsIgnoreCase(morningReadingTime))
                {
                    //set next alarm for evening.
                    String[] split = eveningReadingTime.split(":");
                    AlarmReceiver.setAlarm(context,Integer.parseInt(split[0]),Integer.parseInt(split[1]),0);


                }else if(alarmTime.equalsIgnoreCase(eveningReadingTime))
                {
                    //set next alarm for next day morning.
                    String todayDateString = DateUtils.getDateString(0, "MMM dd,yyyy");
                    int compareResult = DateUtils.compareTimeString(todayDateString, endDay, "MMM dd,yyyy");
                    if(compareResult<0)
                    {
                        //Set Alarm for next day morning.
                        String[] split = morningReadingTime.split(":");
                        AlarmReceiver.setAlarm(context,Integer.parseInt(split[0]),Integer.parseInt(split[1]),1);
                    }else
                    {
                        //Do not set alarm for next day morning as today is end day
                    }
                }
            }
        });
    }

    public static void setAlarm(Context context,int hour_of_day,int min,int dayOffsetFromToday)
    {
        //Create alarm manager
        AlarmManager alarmMgr0 = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        //Create pending intent & register it to your alarm notifier class
        Intent intent0 = new Intent(context, AlarmReceiver.class);
        intent0.putExtra("AlarmTime",hour_of_day+":"+min);
        PendingIntent pendingIntent0 = PendingIntent.getBroadcast(context, 1101, intent0, PendingIntent.FLAG_UPDATE_CURRENT);

        //set timer you want alarm to work (here I have set it to 7.20pm)
        //Intent intent0 = new Intent(this, OldEntryRemover.class);
        Calendar timeOff9 = Calendar.getInstance();
        timeOff9.set(Calendar.HOUR_OF_DAY, hour_of_day);
        timeOff9.set(Calendar.MINUTE, min);
        timeOff9.set(Calendar.SECOND, 0);
        timeOff9.add(Calendar.DATE,dayOffsetFromToday);

        //set that timer as a RTC Wakeup to alarm manager object
        alarmMgr0.set(AlarmManager.RTC_WAKEUP, timeOff9.getTimeInMillis(), pendingIntent0);
        Log.d("setAlarm","setAlarm at "+hour_of_day+":"+min);
    }

    public static void deleteAllAlarm(final Context context)
    {
                final Repository repository = Repository.getInstance((Application) context.getApplicationContext());
                ProtocolModel protocolModel = repository.getAllProtocol().get(0);
                String morningReadingTime = protocolModel.getMorningReadingTime();
                String eveningReadingTime = protocolModel.getEveningReadingTime();

                String currrentTime = DateUtils.getDateString(0, "HH:mm");

                int i = DateUtils.compareTimeString(morningReadingTime,currrentTime,"HH:mm");
                if(i>0)
                {
                    //Cancel Morning alarm
                    AlarmManager alarmMgr0 = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent0 = new Intent(context, AlarmReceiver.class);
                    intent0.putExtra("AlarmTime",morningReadingTime);
                    PendingIntent pendingIntent0 = PendingIntent.getBroadcast(context, 1101, intent0, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmMgr0.cancel(pendingIntent0);
                    return;
                }

                i = DateUtils.compareTimeString(eveningReadingTime,currrentTime,"HH:mm");
                if(i>0)
                {
                    //cancel evening alarm
                    AlarmManager alarmMgr0 = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent0 = new Intent(context, AlarmReceiver.class);
                    intent0.putExtra("AlarmTime",eveningReadingTime);
                    PendingIntent pendingIntent0 = PendingIntent.getBroadcast(context, 1101, intent0, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmMgr0.cancel(pendingIntent0);
                    return;
                }

                if(i<0)
                {
                    //cancel next morning alarm
                    AlarmManager alarmMgr0 = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent0 = new Intent(context, AlarmReceiver.class);
                    intent0.putExtra("AlarmTime",morningReadingTime);
                    PendingIntent pendingIntent0 = PendingIntent.getBroadcast(context, 1101, intent0, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmMgr0.cancel(pendingIntent0);
                    return;
                }
    }
}

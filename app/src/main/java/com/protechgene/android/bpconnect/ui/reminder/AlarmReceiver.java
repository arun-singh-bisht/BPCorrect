package com.protechgene.android.bpconnect.ui.reminder;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.protechgene.android.bpconnect.Utils.AlarmSound;
import com.protechgene.android.bpconnect.Utils.DateUtils;
import com.protechgene.android.bpconnect.Utils.NotificationUtil;
import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;
import com.protechgene.android.bpconnect.ui.ApplicationBPConnect;
import com.protechgene.android.bpconnect.ui.home.MainActivity;

import java.util.Calendar;

public class AlarmReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {

        final Repository repository = Repository.getInstance((Application) context.getApplicationContext());
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {


                ProtocolModel protocolModel = repository.getAllProtocol().get(0);
                String morningReadingTime = protocolModel.getMorningReadingTime();
                boolean morningActive = protocolModel.isMorningActive();
                String eveningReadingTime = protocolModel.getEveningReadingTime();
                boolean iseveningActive = protocolModel.isIseveningActive();
                String endDay = protocolModel.getEndDay();



                String alarmTime = intent.getStringExtra("AlarmTime");
                Log.e("AlarmReceiver","onReceive alarmTime "+alarmTime);


                String ReceivedExtraType = intent.getStringExtra("ReceivedExtraType");
                if(ReceivedExtraType!=null && ReceivedExtraType.equalsIgnoreCase("STOP_ALARM_SOUND"))
                {
                    AlarmSound.getInstance(context).stopSound();
                    Log.d("AlarmReceiver","STOP_ALARM_SOUND");
                    return;
                }

                if((alarmTime.equalsIgnoreCase(morningReadingTime) && morningActive) || (alarmTime.equalsIgnoreCase(eveningReadingTime) && iseveningActive))
                {
                    //In this sample, we want to start/launch this sample app when user clicks on notification
                    //Intent to invoke app when click on notification.
                    Intent intentToHomeScreen = new Intent(context, MainActivity.class);
                    intentToHomeScreen.putExtra("isAlarmFired",true);
                    intentToHomeScreen.putExtra("alarmFireTime",alarmTime);
                    intentToHomeScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    Intent intentToStopAlarmSound = new Intent(context, AlarmReceiver.class);
                    intentToStopAlarmSound.putExtra("ReceivedExtraType","STOP_ALARM_SOUND");

                    //Show Notification status bar
                    Log.e("AlarmReceiver","Show Notification FireTime "+alarmTime);

                    new NotificationUtil().buildLocalNotification(context,intentToHomeScreen,intentToStopAlarmSound,1001,"Time to check BP");

                    //Play Sound in loop
                    if(ApplicationBPConnect.isAlarmSoundEnabled)
                        AlarmSound.getInstance(context).playSound();
                }else
                {
                    Log.d("AlarmReceiver","alarmTime :"+alarmTime +" is inactive");
                }

                //Set Next Alarm
                if(alarmTime.equalsIgnoreCase(morningReadingTime))
                {
                    //set next alarm for evening.
                    String[] split = eveningReadingTime.split(":");
                    AlarmReceiver.setAlarm(context,Integer.parseInt(split[0]),Integer.parseInt(split[1]),0);
                    Log.d("AlarmReceiver","onReceive set next alarm for evening."+eveningReadingTime);
                }else if(alarmTime.equalsIgnoreCase(eveningReadingTime))
                {
                    //set next alarm for next day morning.
                    String todayDateString = DateUtils.getDateString(0, "MMM dd,yyyy");
                    long compareResult = DateUtils.compareTimeString(todayDateString, endDay, "MMM dd,yyyy");
                    if(compareResult<0)
                    {
                        //Set Alarm for next day morning.
                        String[] split = morningReadingTime.split(":");
                        AlarmReceiver.setAlarm(context,Integer.parseInt(split[0]),Integer.parseInt(split[1]),1);
                        Log.d("AlarmReceiver","onReceive Set Alarm for next day morning.."+morningReadingTime);
                    }else
                    {
                        //Do not set alarm for next day morning as today is end day
                        Log.d("AlarmReceiver","onReceive Do not set alarm for next day morning as today is end day.");
                    }
                }else
                {
                    Log.d("AlarmReceiver","onReceive Snooz Time Captured");
                }
            }
        });
    }

    public static void setAlarm(Context context,int hour_of_day,int min,int dayOffsetFromToday)
    {
        Log.e("contextfor",context.getPackageName());
        //Create pending intent & register it to your alarm notifier class
        Intent intent0 = new Intent(context, AlarmReceiver.class);
        intent0.putExtra("AlarmTime",hour_of_day+":"+min);
        PendingIntent pendingIntent0 = PendingIntent.getBroadcast(context, 1101, intent0, PendingIntent.FLAG_UPDATE_CURRENT);

        //set timer you want alarm to work (here I have set it to 7.20pm)
        //Intent intent0 = new Intent(this, OldEntryRemover.class);
        Calendar timeOff = Calendar.getInstance();
        timeOff.set(Calendar.HOUR_OF_DAY, hour_of_day);
        timeOff.set(Calendar.MINUTE, min);
        timeOff.set(Calendar.SECOND, 0);
        timeOff.add(Calendar.DATE,dayOffsetFromToday);

        //Create alarm manager
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, timeOff.getTimeInMillis(), pendingIntent0);

        Log.d("setAlarm","setAlarm at "+hour_of_day+":"+min);
    }

    public static void setSnoozAlarm(Context context,String alarmFireTime)
    {
        String newTime = DateUtils.addTime(alarmFireTime, "HH:mm", 0, 10);
        String[] split = newTime.split(":");
        int hour_of_day = Integer.parseInt(split[0]);
        int min = Integer.parseInt(split[1]);
        int dayOffsetFromToday = 0;
        //Create pending intent & register it to your alarm notifier class
        Intent intent0 = new Intent(context, AlarmReceiver.class);
        intent0.putExtra("AlarmTime",alarmFireTime);
        PendingIntent pendingIntent0 = PendingIntent.getBroadcast(context, 1101, intent0, PendingIntent.FLAG_UPDATE_CURRENT);

        //set timer you want alarm to work (here I have set it to 7.20pm)
        //Intent intent0 = new Intent(this, OldEntryRemover.class);
        Calendar timeOff = Calendar.getInstance();
        timeOff.set(Calendar.HOUR_OF_DAY, hour_of_day);
        timeOff.set(Calendar.MINUTE, min);
        timeOff.set(Calendar.SECOND, 0);
        timeOff.add(Calendar.DATE,dayOffsetFromToday);

        //Create alarm manager
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, timeOff.getTimeInMillis(), pendingIntent0);

        Log.d("setAlarm","setAlarm at "+hour_of_day+":"+min);
    }

    public static void deleteAllAlarm(final Context context)
    {
                final Repository repository = Repository.getInstance((Application) context.getApplicationContext());
                ProtocolModel protocolModel = repository.getAllProtocol().get(0);
                String morningReadingTime = protocolModel.getMorningReadingTime();
                String eveningReadingTime = protocolModel.getEveningReadingTime();

                String currrentTime = DateUtils.getDateString(0, "HH:mm");

                long i = DateUtils.compareTimeString(morningReadingTime,currrentTime,"HH:mm");
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

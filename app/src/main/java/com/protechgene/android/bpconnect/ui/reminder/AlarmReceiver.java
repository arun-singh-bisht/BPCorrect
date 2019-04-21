package com.protechgene.android.bpconnect.ui.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.protechgene.android.bpconnect.Utils.NotificationUtil;
import com.protechgene.android.bpconnect.ui.home.MainActivity;

public class AlarmReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver",""+System.currentTimeMillis());

        //Intent to invoke app when click on notification.
        //In this sample, we want to start/launch this sample app when user clicks on notification
        Intent intentToRepeat = new Intent(context, MainActivity.class);
        //set flag to restart/relaunch the app
        intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

       new NotificationUtil().buildLocalNotification(context,intentToRepeat,1001,"Time to check BP");
    }



}

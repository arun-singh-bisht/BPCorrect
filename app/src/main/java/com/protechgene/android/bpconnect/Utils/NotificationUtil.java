package com.protechgene.android.bpconnect.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.protechgene.android.bpconnect.R;

public class NotificationUtil {


    public void buildLocalNotification(Context context, Intent intentToHomeScreen,Intent intentToStopAlarmSound,int requestCode,String title)
    {
        //Pending intent to handle launch of Activity in intent above
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, requestCode, intentToHomeScreen, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent deleteIntent = PendingIntent.getBroadcast(context, requestCode, intentToStopAlarmSound, 0);

        //Build notification
        Notification repeatedNotification = createBuilder(context,title,pendingIntent,deleteIntent).build();

        //Send local notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(requestCode, repeatedNotification);
    }

    private NotificationCompat.Builder createBuilder(Context context, String title,PendingIntent pendingIntent,PendingIntent deleteIntent) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "1001")
                .setSmallIcon(R.drawable.ic_launcher_round_new)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText("Connect to BP device and record.")
                .setColor(Color.parseColor("#009add"))
                .setDeleteIntent(deleteIntent)
                .setAutoCancel(true);

        return mBuilder;
    }


    //Called From Application Class
    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "";
            String description = "";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1001", "BPNotification", importance);
            channel.setDescription("BPNotification");
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

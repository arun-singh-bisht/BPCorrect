package com.protechgene.android.bpconnect.ui;

import android.app.Application;

import com.protechgene.android.bpconnect.Utils.NotificationUtil;
import com.protechgene.android.bpconnect.data.Repository;

public class ApplicationBPConnect extends Application {

    public static int PROTOCOL_MORNING_MINIMUM_TIME = 4; // 4:00 AM
    public static int PROTOCOL_MORNING_MAXIMUM_TIME = 10; // 12:00 pM
    public static int PROTOCOL_EVENING_MINIMUM_TIME = 14; // 4:00 PM
    public static int PROTOCOL_EVENING_MAXIMUM_TIME = 18; // 12:00 AM


    public static long PROTOCOL_READING_ACCEPTED_TIME_WINDOW = 1000*60*5; //5 min
    public static String PROTOCOL_READING__MORNING = "PROTOCOL_READING__MORNING";
    public static String PROTOCOL_READING__EVENING = "PROTOCOL_READING__EVENING";

    public static boolean isAlarmSoundEnabled = true;
    public static boolean isTodayIncluded = true;
    public static boolean isBPDeviceRequiredForTesting = true;
    public static boolean isReadingTakenFromActualDevice = true;

    @Override
    public void onCreate() {
        super.onCreate();

        //Repository.getInstance(this);
        NotificationUtil.createNotificationChannel(this);
    }
}

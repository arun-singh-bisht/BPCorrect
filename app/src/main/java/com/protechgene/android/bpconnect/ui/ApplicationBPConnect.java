package com.protechgene.android.bpconnect.ui;

import android.app.Application;
import android.util.Log;

import com.ihealth.communication.manager.iHealthDevicesManager;
import com.protechgene.android.bpconnect.Utils.NotificationUtil;

public class ApplicationBPConnect extends Application {

    public static int PROTOCOL_MORNING_MINIMUM_TIME = 4; // 4:00 AM
    public static int PROTOCOL_MORNING_MAXIMUM_TIME = 24; // 12:00 pM
    public static int PROTOCOL_EVENING_MINIMUM_TIME = 13; // 4:00 PM
    public static int PROTOCOL_EVENING_MAXIMUM_TIME = 24; // 12:00 AM


    public static long PROTOCOL_READING_ACCEPTED_TIME_WINDOW = 1000*60*15 ; //15 min
    public static String PROTOCOL_READING__MORNING = "PROTOCOL_READING__MORNING";
    public static String PROTOCOL_READING__EVENING = "PROTOCOL_READING__EVENING";

    public static boolean isAlarmSoundEnabled = true;
    public static boolean isTodayIncluded = true;
    public static boolean isBPDeviceRequiredForTesting = true;
    public static boolean readingUploadToServer = true;

    @Override
    public void onCreate() {
        super.onCreate();

        //Repository.getInstance(this);
        NotificationUtil.createNotificationChannel(this);
        /*
         * Initializes the iHealth devices manager. Can discovery available iHealth devices nearby
         * and connect these devices through iHealthDevicesManager.
         */
        iHealthDevicesManager.getInstance().init(this,  Log.VERBOSE, Log.VERBOSE);
    }
}

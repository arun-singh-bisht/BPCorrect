package com.protechgene.android.bpconnect.ui;

import android.app.Application;

import com.protechgene.android.bpconnect.Utils.NotificationUtil;
import com.protechgene.android.bpconnect.data.Repository;

public class ApplicationBPConnect extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Repository.getInstance(this);
        NotificationUtil.createNotificationChannel(this);
    }
}

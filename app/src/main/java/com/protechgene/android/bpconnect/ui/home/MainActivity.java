package com.protechgene.android.bpconnect.ui.home;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.AlarmSound;
import com.protechgene.android.bpconnect.Utils.DateUtils;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.base.BaseActivity;
import com.protechgene.android.bpconnect.ui.custom.CustomAlertDialog;
import com.protechgene.android.bpconnect.ui.measureBP.MeasureBPFragmentNew;
import com.protechgene.android.bpconnect.ui.reminder.AlarmReceiver;


public class MainActivity extends BaseActivity {

    @Override
    protected int layoutRes() {
        return R.layout.app_bar_main;
    }

    @Override
    protected void initialize() {

        HomeFragment homeFragment = new HomeFragment();

        boolean isAlarmFired = getIntent().getBooleanExtra("isAlarmFired", false);
        String alarmFireTime = getIntent().getStringExtra("FireTime");
        Bundle args = new Bundle();
        args.putBoolean("isAlarmFired",isAlarmFired);
        args.putString("alarmFireTime",alarmFireTime);
        homeFragment.setArguments(args);

        FragmentUtil.loadFragment(this,R.id.container_fragment,homeFragment,HomeFragment.FRAGMENT_TAG,null);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment frg = getSupportFragmentManager().findFragmentById(R.id.container_fragment);
        if (frg != null) {
            frg.onActivityResult(requestCode, resultCode, data);
        }
    }
}

package com.protechgene.android.bpconnect.ui.home;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.AlarmSound;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.base.BaseActivity;
import com.protechgene.android.bpconnect.ui.custom.CustomAlertDialog;
import com.protechgene.android.bpconnect.ui.measureBP.MeasureBPFragment;


public class MainActivity extends BaseActivity implements CustomAlertDialog.I_CustomAlertDialogThreeButton {

    @Override
    protected int layoutRes() {
        return R.layout.app_bar_main;
    }

    @Override
    protected void initialize() {
        FragmentUtil.loadFragment(this,R.id.container_fragment,new HomeFragment(),HomeFragment.FRAGMENT_TAG,null);

        //Protocol Alarm
        boolean isAlarmFired = getIntent().getBooleanExtra("isAlarmFired", false);
        if(isAlarmFired)
        {
            String msg = "It's time to check your blood pressure. You can also snooz it for some time.";
            CustomAlertDialog.showThreeButtonDialog(this,1001,msg,"Check Now","Snooz","Cancel",this);
        }else
        {
            CustomAlertDialog.showVideoDialog(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment frg = getSupportFragmentManager().findFragmentById(R.id.container_fragment);
        if (frg != null) {
            frg.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPositiveClick(int request_code) {
        //Take Now
        AlarmSound.getInstance(this).stopSound();

        MeasureBPFragment measureBPFragment = new MeasureBPFragment();
        Bundle args = new Bundle();
        args.putBoolean("isTypeProtocol",true);
        measureBPFragment.setArguments(args);
        FragmentUtil.loadFragment(this,R.id.container_fragment,measureBPFragment,MeasureBPFragment.FRAGMENT_TAG,"MeasureBPFragmentTransition");
    }

    @Override
    public void onNegativeClick(int request_code) {
        //Snooz
        AlarmSound.getInstance(this).stopSound();
    }

    @Override
    public void onNeuralClick(int request_code) {
        //Cancel
        AlarmSound.getInstance(this).stopSound();
    }

}

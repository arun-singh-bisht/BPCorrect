package com.protechgene.android.bpconnect.ui.home;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.lifesense.ble.LsBleManager;
import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.AlarmSound;
import com.protechgene.android.bpconnect.Utils.DateUtils;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.local.db.DatabaseHelper;
import com.protechgene.android.bpconnect.data.local.sp.PreferencesHelper;
import com.protechgene.android.bpconnect.ui.base.BaseActivity;
import com.protechgene.android.bpconnect.ui.custom.CustomAlertDialog;
//import com.protechgene.android.bpconnect.ui.devices.DevicesFragment;
import com.protechgene.android.bpconnect.ui.devices.PairedDevice.DevicesFragment;
import com.protechgene.android.bpconnect.ui.login.LoginActivity;
import com.protechgene.android.bpconnect.ui.measureBP.MeasureBPFragmentNew;
import com.protechgene.android.bpconnect.ui.profile.ProfileFragment;
import com.protechgene.android.bpconnect.ui.readingHistory.BPReadingFragment;
import com.protechgene.android.bpconnect.ui.reminder.AlarmReceiver;
import com.protechgene.android.bpconnect.ui.settings.SettingsFragment;
import com.protechgene.android.bpconnect.ui.tutorial.TutorialFragment;

import butterknife.OnClick;


public class MainActivity extends BaseActivity  implements  CustomAlertDialog.I_CustomAlertDialog{

    private DrawerLayout drawerLayout;
    AppBarLayout appBarLayout;
    public PreferencesHelper mSharedPrefsHelper;
    public DatabaseHelper mDatabaseHelper;
    @Override
    protected int layoutRes() {
        return R.layout.app_bar_main;

    }

    @OnClick(R.id.nav_profile_redirect)
    public void redirect_to_profile(){
        drawerLayout.closeDrawer(Gravity.LEFT);
        FragmentUtil.loadFragment(this,R.id.container_fragment,new ProfileFragment(),ProfileFragment.FRAGMENT_TAG,"ProfileFragmentTransition");

    }

    @OnClick(R.id.nav_measure_redirect)
    public void redirect_to_measure(){
        drawerLayout.closeDrawer(Gravity.LEFT);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Caution");
        alertDialog.setMessage("It's important to rest for 5 minutes before measuring your blood pressure to get an accurate reading.");
        alertDialog.setPositiveButton("Ready to check my BP", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                FragmentUtil.loadFragment(getApplicationContext(),R.id.container_fragment,new MeasureBPFragmentNew(),MeasureBPFragmentNew.FRAGMENT_TAG,"MeasureBPFragmentTransition");
            }
        });


        alertDialog.setNegativeButton("Start 5-minute timer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event

                dialog.dismiss();

                Dialog dialog1 = new Dialog(getApplicationContext());
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.count_down_layout_dialog, null);
                dialog1.setCancelable(false);
                dialog1.setContentView(view);

                // dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                Window window = dialog1.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView tv = (TextView) dialog1.findViewById(R.id.count_down_tv);
                final VideoView videoView = dialog1.findViewById(R.id.video_view);
                TextView close_btn = (TextView) dialog1.findViewById(R.id.close_btn);
                ImageView play_button = (ImageView) dialog1.findViewById(R.id.play_image) ;
                videoView.setBackgroundColor(getResources().getColor(android.R.color.black));
                final ProgressBar progressBar = (ProgressBar) dialog1.findViewById(R.id.dailog_progress_bar);
                close_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.dismiss();
                    }
                });
                Uri uri = Uri.parse("http://67.211.223.164:8080/video/bp_video.mp4");
                videoView.setVideoURI(uri);
                progressBar.setVisibility(View.VISIBLE);
                videoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (videoView.isPlaying()){
                            videoView.pause();
                            play_button.setVisibility(View.VISIBLE);
                        }
                        else{
                            videoView.start();
                            play_button.setVisibility(View.GONE);
                        }
                    }
                });
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        mp.start();
                        mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                            @Override
                            public void onVideoSizeChanged(MediaPlayer mp, int arg1,
                                                           int arg2) {
                                videoView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                                progressBar.setVisibility(View.GONE);
                                mp.start();
                            }
                        });
                    }
                });

                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        videoView.stopPlayback();
                        //dialog1.dismiss();
                    }
                });
                dialog1.show();

                new CountDownTimer(1000*60*5, 1000) {
                    public void onTick(long millisUntilFinished) {

                        long min = millisUntilFinished/(1000*60);
                        long sec = (millisUntilFinished%(1000*60))/1000;

                        String second = (sec < 10)? "0"+sec : ""+sec;
                        tv.setText("Time Left - 0"+min+":"+second);
                    }

                    public void onFinish() {
                        videoView.stopPlayback();
                        dialog1.dismiss();
                        FragmentUtil.loadFragment(getApplicationContext(),R.id.container_fragment,new MeasureBPFragmentNew(),MeasureBPFragmentNew.FRAGMENT_TAG,"MeasureBPFragmentTransition");
                    }
                }.start();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }
    @OnClick(R.id.nav_learn_redirect)
    public void redirect_to_learn(){
        drawerLayout.closeDrawer(Gravity.LEFT);
        FragmentUtil.loadFragment(this,R.id.container_fragment,new TutorialFragment(),TutorialFragment.FRAGMENT_TAG,"TutorialFragmentTransition");
    }
    @OnClick(R.id.nav_manage_devices_redirect)
    public void redirect_to_manage_devices(){
        drawerLayout.closeDrawer(Gravity.LEFT);
        FragmentUtil.loadFragment(this,R.id.container_fragment,new DevicesFragment(),DevicesFragment.FRAGMENT_TAG,"DevicesFragmentTransition");

    }

    @OnClick(R.id.nav_readings_redirect)
    public void redirect_to_readings(){
        drawerLayout.closeDrawer(Gravity.LEFT);
        FragmentUtil.loadFragment(this,R.id.container_fragment,new BPReadingFragment(),BPReadingFragment.FRAGMENT_TAG,"BPReadingFragmentTransition");

    }

    @OnClick(R.id.nav_logout)
    public void logout(){
        drawerLayout.closeDrawer(Gravity.LEFT);
        CustomAlertDialog.showDialog(this,0, "Do you want to sign out from app?","YES","CANCEL",R.layout.custom_dialo,this);

    }

    @Override
    public void onPositiveClick(Dialog dialog,int request_code) {
        onSignOut();
    }

    @Override
    public void onNegativeClick(Dialog dialog,int request_code) {

    }

    public void onSignOut() {
         signOut();
        this.startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        this.finish();
    }

    public void signOut()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Repository.getInstance(getApplication()).clearSharedPref();
                Repository.getInstance(getApplication()).deleteAllHealthRecords();
                Repository.getInstance(getApplication()).deleteAllProtocol();
               // mSharedPrefsHelper = new PreferencesHelper();
                //mSharedPrefsHelper.clearSharedPref();
                //mDatabaseHelper.deleteAllHealthRecords();
                //mDatabaseHelper.deleteAllProtocol();
            }
        });

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
        // updates by rajat
        /*Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);*/
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.master_fragment_container);
        // ---------------

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

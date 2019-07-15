package com.protechgene.android.bpconnect.ui.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.AlarmSound;
import com.protechgene.android.bpconnect.Utils.DateUtils;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.custom.CustomAlertDialog;
import com.protechgene.android.bpconnect.ui.devices.PairedDevice.DevicesFragment;
import com.protechgene.android.bpconnect.ui.measureBP.MeasureBPFragmentNew;
import com.protechgene.android.bpconnect.ui.profile.ProfileEditFragment;
import com.protechgene.android.bpconnect.ui.profile.ProfileFragment;
import com.protechgene.android.bpconnect.ui.readingHistory.BPReadingFragment;
import com.protechgene.android.bpconnect.ui.reminder.AlarmReceiver;
import com.protechgene.android.bpconnect.ui.reminder.ReminderFragment;
import com.protechgene.android.bpconnect.ui.settings.SettingsFragment;
import com.protechgene.android.bpconnect.ui.tutorial.TutorialFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeFragment extends BaseFragment implements  HomeFragmentNavigator,CustomAlertDialog.I_CustomAlertDialog,CustomAlertDialog.I_CustomAlertDialogThreeButton {

    public static final String FRAGMENT_TAG = "HomeFragment";
    private HomeViewModel mHomeViewModel;
    boolean isprotocol_active = false;
    @BindView(R.id.text_profile_name)
    TextView text_profile_name;


    @BindView(R.id.text_profile_email)
    TextView text_profile_email;

    @BindView(R.id.image_profile_pic)
    CircularImageView image_profile_pic;


    private boolean protocolStatus;
    private String alarmFireTime;
    private  Dialog videoDialog;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_home;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(HomeViewModel.class);
        mHomeViewModel.setNavigator(this);
        Log.d("HomeFragment","initialize");

        boolean isAlarmFired = false;
        boolean isNewUser = false;

        Bundle args = getArguments();
        if(args!=null) {
            isAlarmFired = args.getBoolean("isAlarmFired");
            isNewUser = args.getBoolean("isNewUser");
        }

        if(isAlarmFired)
        {
            alarmFireTime = args.getString("FireTime");
            String msg = "It's time to check your blood pressure. You can also snooz it for some time.";
            CustomAlertDialog.showThreeButtonDialog(getBaseActivity(),1001,msg,"Check Now","Snooz","Cancel",this);
        }else
        {
            //CustomAlertDialog.showInstructionDialog(getBaseActivity());
            if(isNewUser)
            {
                //mHomeViewModel.checkActiveProtocol();
                videoDialog = CustomAlertDialog.dialogPlayVideoNew(getBaseActivity(), new CustomAlertDialog.VideoDialogCallback() {

                    @Override
                    public void onVideoEnd(int request_code) {

                        //if (!protocolStatus)
                        openRemiderFragment();
                    }
                });
            }else
            {
                //Syn History Reading Data
                //showProgress("Syn app data...");
                mHomeViewModel.synHistoryData();
            }


        }
    }

    private void getPRotocolStatus() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Insert Data
                 System.out.println("protocol data ---"+mHomeViewModel.getprotocol());
                if (mHomeViewModel.getprotocol())
                    isprotocol_active = true;
                else
                    isprotocol_active = false;
                // Get Data
            }
        });
    }

    @Override
    protected void initialize()
    {
        //  getBaseActivity().getSupportActionBar().show();
        mHomeViewModel.getProfileDetails();
    }

    @OnClick(R.id.image_menu)
    public void show_drawer() {
        DrawerLayout drawerLayout = getBaseActivity().findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    @OnClick(R.id.card_measure_bp)
    public void openMeasureBPFragment() {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Caution");
        alertDialog.setMessage("It's important to rest for 5 minutes before measuring your blood pressure to get an accurate reading.");
        alertDialog.setPositiveButton("Ready to check my BP", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                FragmentUtil.loadFragment(getBaseActivity(),R.id.container_fragment,new MeasureBPFragmentNew(),MeasureBPFragmentNew.FRAGMENT_TAG,"MeasureBPFragmentTransition");
            }
        });

        alertDialog.setNegativeButton("Start 5-minute timer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event

                dialog.dismiss();

                Dialog dialog1 = new Dialog(getContext());
                View view = LayoutInflater.from(getContext()).inflate(R.layout.count_down_layout_dialog, null);
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
                        FragmentUtil.loadFragment(getBaseActivity(),R.id.container_fragment,new MeasureBPFragmentNew(),MeasureBPFragmentNew.FRAGMENT_TAG,"MeasureBPFragmentTransition");
                    }
                }.start();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @OnClick(R.id.card_readings)
    public void openReadingsFragment() {
        FragmentUtil.loadFragment(getBaseActivity(),R.id.container_fragment,new BPReadingFragment(),BPReadingFragment.FRAGMENT_TAG,"BPReadingFragmentTransition");
    }

    @OnClick(R.id.card_learn)
    public void openTutorialFragment() {
          FragmentUtil.loadFragment(getBaseActivity(),R.id.container_fragment,new TutorialFragment(),TutorialFragment.FRAGMENT_TAG,"TutorialFragmentTransition");
    }

    @OnClick(R.id.card_devices)
    public void openDeviceFragment() {
        FragmentUtil.loadFragment(getBaseActivity(),R.id.container_fragment,new DevicesFragment(),DevicesFragment.FRAGMENT_TAG,"DevicesFragmentTransition");
    }

    @OnClick(R.id.card_reminder)
    public void openRemiderFragment() {
        FragmentUtil.loadFragment(getBaseActivity(),R.id.container_fragment,new ReminderFragment(), ReminderFragment.FRAGMENT_TAG,"ReminderNewFragmentTransition");
    }

    @OnClick(R.id.card_settings)
    public void openSettingsFragment() {
        FragmentUtil.loadFragment(getBaseActivity(),R.id.container_fragment,new SettingsFragment(),SettingsFragment.FRAGMENT_TAG,"SettingsFragmentTransition");
    }

    @OnClick(R.id.layout_profile)
    public void openProfileFragment() {
        FragmentUtil.loadFragment(getBaseActivity(),R.id.container_fragment,new ProfileFragment(),ProfileFragment.FRAGMENT_TAG,"ProfileFragmentTransition");
    }


    @Override
    public void onPositiveClick(Dialog dialog,int request_code) {

    }

    @Override
    public void onNegativeClick(Dialog dialog,int request_code) {

    }



    @Override
    public void showProfileDetails() {


        String userName = mHomeViewModel.getUserFirstName();
        String userEmail = mHomeViewModel.getUserEmail();
        // update by rajat
        TextView username = getBaseActivity().findViewById(R.id.nav_profile_name);
        TextView address = getBaseActivity().findViewById(R.id.nav_profile_address);
        CircularImageView nav_profile_image = getBaseActivity().findViewById(R.id.nav_profile_img);
        String name = username.toString();
        if(userName==null || userName.equalsIgnoreCase("null")) {
            //First Time App User
            if(videoDialog!=null)
                videoDialog.dismiss();

            userName = "BPConnect User";
            userEmail = "View and edit profile";
            text_profile_name.setText(userName + "");
            text_profile_email.setText(userEmail + "");

            // For nav  drawer by rajat
            username.setText(userName + "");
            address.setText("NA");


            //Open Profile Edit Screen
            ProfileEditFragment profileEditFragment = new ProfileEditFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isProfileComplete",false);
            profileEditFragment.setArguments(bundle);
            FragmentUtil.loadFragment(getActivity(),R.id.container_fragment,profileEditFragment,ProfileEditFragment.FRAGMENT_TAG,null);
            return;
        }else if(!isprotocol_active && mHomeViewModel.getFirstTimeUser() && mHomeViewModel.getUserFirstName()!=null) {
            // user from web panel
            if(videoDialog!=null)
                videoDialog.dismiss();

            videoDialog = CustomAlertDialog.dialogPlayVideoNew(getBaseActivity(), request_code -> {

                if (!isprotocol_active)
                openRemiderFragment();
            });
            mHomeViewModel.setFirstTimeUser();
        }

        else {
            //Already an App User
            userName =  mHomeViewModel.getUserFirstName() +" "+ mHomeViewModel.getUserLastName();
            text_profile_name.setText(userName + "");
            text_profile_email.setText(userEmail + "");

            // For nav  drawer by rajat
            username.setText(userName + "");
            address.setText(mHomeViewModel.getAddress());

            String image_url = mHomeViewModel.getProfilePic();
            if(image_url != null)
                Glide.with(getContext()).load("http://67.211.223.164:8080"+image_url).placeholder(R.drawable.default_pic).into(image_profile_pic);
            Glide.with(getContext()).load("http://67.211.223.164:8080"+image_url).placeholder(R.drawable.default_pic).into(nav_profile_image);
        }
    }

    @Override
    public void handleError(Throwable throwable) {

    }

    @Override
    public void isProtocolExists(boolean status) {
        protocolStatus = status;
    }

    @Override
    public void historyDataSyncStatus(boolean status) {
        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress();
                if(status)
                    getBaseActivity().showSnakeBar("App Data Sync Successful");
                else
                    getBaseActivity().showSnakeBar("App Data Sync Failure");
            }
        });
        //get protocol status
        getPRotocolStatus();
    }


    //Alarm Pop Up Response

    @Override
    public void onPositiveClick(int request_code) {
        //Take Now
        AlarmSound.getInstance(getBaseActivity()).stopSound();

        MeasureBPFragmentNew MeasureBPFragmentNew = new MeasureBPFragmentNew();
        Bundle args = new Bundle();
        args.putBoolean("isTypeProtocol",true);
        MeasureBPFragmentNew.setArguments(args);
        FragmentUtil.loadFragment(getBaseActivity(),R.id.container_fragment,MeasureBPFragmentNew,MeasureBPFragmentNew.FRAGMENT_TAG,"MeasureBPFragmentTransition");
    }

    @Override
    public void onNegativeClick(int request_code) {
        //Snooz
        AlarmSound.getInstance(getBaseActivity()).stopSound();
        Log.d("onNegativeClick","Alarm Sound Stopped");
        //Remove all saved alarm
        //AlarmReceiver.deleteAllAlarm(MainActivity.this);
        //Log.d("onNegativeClick","All Alarm removed");
        //Set alarm for next 10 min
        String newTime = DateUtils.addTime(alarmFireTime, "HH:mm", 0, 10);
        String[] split = newTime.split(":");
        Log.d("onNegativeClick","Setting Alarm from "+alarmFireTime +" To "+newTime);
        AlarmReceiver.setAlarm(getBaseActivity(),Integer.parseInt(split[0]),Integer.parseInt(split[1]),0);

    }

    @Override
    public void onNeuralClick(int request_code) {
        //Cancel
        AlarmSound.getInstance(getBaseActivity()).stopSound();
    }
}

package com.protechgene.android.bpconnect.ui.home;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.AlarmSound;
import com.protechgene.android.bpconnect.Utils.DateUtils;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.custom.CustomAlertDialog;
import com.protechgene.android.bpconnect.ui.devices.DevicesFragment;
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

    @BindView(R.id.text_profile_name)
    TextView text_profile_name;

    @BindView(R.id.text_profile_email)
    TextView text_profile_email;

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

            }
        }
    }

    @Override
    protected void initialize() {
        mHomeViewModel.getProfileDetails();
    }


    @OnClick(R.id.card_measure_bp)
    public void openMeasureBPFragment() {
        FragmentUtil.loadFragment(getBaseActivity(),R.id.container_fragment,new MeasureBPFragmentNew(),MeasureBPFragmentNew.FRAGMENT_TAG,"MeasureBPFragmentTransition");
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
        FragmentUtil.loadFragment(getBaseActivity(),R.id.container_fragment,new ReminderFragment(),ReminderFragment.FRAGMENT_TAG,"ReminderFragmentTransition");
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
        String userName = mHomeViewModel.getUserName();
        String userEmail = mHomeViewModel.getUserEmail();

        if(userName==null || userName.equalsIgnoreCase("null")) {
            //First Time App User
            if(videoDialog!=null)
                videoDialog.dismiss();

            userName = "BPConnect User";
            userEmail = "View and edit profile";
            text_profile_name.setText(userName + "");
            text_profile_email.setText(userEmail + "");

            //Open Profile Edit Screen
            ProfileEditFragment profileEditFragment = new ProfileEditFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isProfileComplete",false);
            profileEditFragment.setArguments(bundle);

            FragmentUtil.loadFragment(getActivity(),R.id.container_fragment,profileEditFragment,ProfileEditFragment.FRAGMENT_TAG,null);

        }else {
            //Already an App User

            text_profile_name.setText(userName + "");
            text_profile_email.setText(userEmail + "");
        }
    }

    @Override
    public void handleError(Throwable throwable) {

    }

    @Override
    public void isProtocolExists(boolean status) {
        protocolStatus = status;
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

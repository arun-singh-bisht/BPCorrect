package com.protechgene.android.bpconnect.ui.home;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.custom.CustomAlertDialog;
import com.protechgene.android.bpconnect.ui.devices.DevicesFragment;
import com.protechgene.android.bpconnect.ui.fragments.ReminderFragment;
import com.protechgene.android.bpconnect.ui.measureBP.MeasureBPFragment;
import com.protechgene.android.bpconnect.ui.profile.ProfileFragment;
import com.protechgene.android.bpconnect.ui.readingHistory.BPReadingFragment;
import com.protechgene.android.bpconnect.ui.tutorial.TutorialFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeFragment extends BaseFragment implements  HomeFragmentNavigator,CustomAlertDialog.I_CustomAlertDialog {

    public static final String FRAGMENT_TAG = "HomeFragment";
    private HomeViewModel mHomeViewModel;

    @BindView(R.id.text_profile_name)
    TextView text_profile_name;

    @BindView(R.id.text_profile_email)
    TextView text_profile_email;



    @Override
    protected int layoutRes() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initialize() {

        mHomeViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(HomeViewModel.class);
        mHomeViewModel.setNavigator(this);

        mHomeViewModel.getProfileDetails();
        //CustomAlertDialog.showDialog(getActivity(), "Do you want to share your readings\nwith your doctor's office?","ALLOW","DON'T ALLOW",R.layout.custom_dialo_with_checkbox,this);

    }


    @OnClick(R.id.card_measure_bp)
    public void openMeasureBPFragment() {
        FragmentUtil.loadFragment(getBaseActivity(),R.id.container_fragment,new MeasureBPFragment(),MeasureBPFragment.FRAGMENT_TAG,"MeasureBPFragmentTransition");
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

    @OnClick(R.id.layout_profile)
    public void openProfileFragment() {
        FragmentUtil.loadFragment(getBaseActivity(),R.id.container_fragment,new ProfileFragment(),ProfileFragment.FRAGMENT_TAG,"ProfileFragmentTransition");
    }


    @Override
    public void onPositiveClick(Dialog dialog) {

    }

    @Override
    public void onNegativeClick(Dialog dialog) {

    }

    @Override
    public void showProfileDetails() {
        String userName = mHomeViewModel.getUserName();
        String userEmail = mHomeViewModel.getUserEmail();

        if(userName==null || userName.equalsIgnoreCase("null")) {
            userName = "BPConnect User";
            userEmail = "View and edit profile";
        }
        text_profile_name.setText(userName+"");
        text_profile_email.setText(userEmail+"");
    }

    @Override
    public void handleError(Throwable throwable) {

    }


}

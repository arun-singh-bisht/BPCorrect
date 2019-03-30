package com.protechgene.android.bpconnect.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.activites.MainActivity;
import com.protechgene.android.bpconnect.ui.custom.CustomAlertDialog;

public class HomeFragment extends Fragment implements View.OnClickListener, CustomAlertDialog.I_CustomAlertDialog {

    public static final String FRAGMENT_TAG = "HomeFragment";
    private View view;
    private MainActivity mainActivity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(view==null)
        {
            view = inflater.inflate(R.layout.fragment_home,container,false);
            initView();
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void initView()
    {
        view.findViewById(R.id.card_measure_bp).setOnClickListener(this);
        view.findViewById(R.id.card_readings).setOnClickListener(this);
        view.findViewById(R.id.card_learn).setOnClickListener(this);
        view.findViewById(R.id.card_devices).setOnClickListener(this);
        view.findViewById(R.id.layout_profile).setOnClickListener(this);
        view.findViewById(R.id.card_reminder).setOnClickListener(this);

        CustomAlertDialog.showDialog(getActivity(), "Do you want to share your readings\nwith your doctor's office?","ALLOW","DON'T ALLOW",R.layout.custom_dialo_with_checkbox,this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.card_measure_bp : {
                FragmentUtil.loadFragment(mainActivity,R.id.container_fragment,new MeasureBPFragment(),MeasureBPFragment.FRAGMENT_TAG,"MeasureBPFragmentTransition");
            }
            break;
            case R.id.card_readings : {
                FragmentUtil.loadFragment(mainActivity,R.id.container_fragment,new BPReadingFragment(),BPReadingFragment.FRAGMENT_TAG,"BPReadingFragmentTransition");
            }
            break;
            case R.id.card_learn : {
                FragmentUtil.loadFragment(mainActivity,R.id.container_fragment,new TutorialFragment(),TutorialFragment.FRAGMENT_TAG,"TutorialFragmentTransition");
            }
            break;
            case R.id.card_devices : {
                FragmentUtil.loadFragment(mainActivity,R.id.container_fragment,new DevicesFragment(),DevicesFragment.FRAGMENT_TAG,"DevicesFragmentTransition");
            }
            break;
            case R.id.layout_profile : {
                FragmentUtil.loadFragment(mainActivity,R.id.container_fragment,new ProfileFragment(),ProfileFragment.FRAGMENT_TAG,"ProfileFragmentTransition");
            }
            break;
            case R.id.card_reminder : {
                FragmentUtil.loadFragment(mainActivity,R.id.container_fragment,new ReminderFragment(),ReminderFragment.FRAGMENT_TAG,"ReminderFragmentTransition");
            }
            break;
        }
    }

    @Override
    public void onPositiveClick(Dialog dialog) {

    }

    @Override
    public void onNegativeClick(Dialog dialog) {

    }
}

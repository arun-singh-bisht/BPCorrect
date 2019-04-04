package com.protechgene.android.bpconnect.ui.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.data.local.models.TutorialModel;
import com.protechgene.android.bpconnect.ui.adapters.DevicesAdapter;
import com.protechgene.android.bpconnect.ui.custom.CustomAlertDialog;
import com.protechgene.android.bpconnect.ui.custom.MyTimePickerFragment;
import com.protechgene.android.bpconnect.ui.custom.TimePickerFragment;

import java.util.ArrayList;
import java.util.Calendar;

public class ReminderFragment extends Fragment implements View.OnClickListener, TimePickerFragment.TimePickedListener, CustomAlertDialog.I_CustomAlertDialog {

    public static final String FRAGMENT_TAG = "ReminderFragment";
    private View view;
    private DevicesAdapter bpReadingAdapter;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
            view = inflater.inflate(R.layout.fragment_reminder,container,false);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView()
    {
        TextView txt_title =  view.findViewById(R.id.txt_title);
        txt_title.setText("Reminder");

        view.findViewById(R.id.fab_add).setOnClickListener(this);
        view.findViewById(R.id.image_delete).setOnClickListener(this);

        //boolean is =SharedPreferenceHelper.getSharedPreferenceBoolean(getActivity(),"isAlarmSet",false);

        boolean is = false;
        if(is)
        {
            view.findViewById(R.id.layout_create).setVisibility(View.GONE);
            view.findViewById(R.id.layout_active_alarm).setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.img_right:{
                FragmentUtil.loadFragment(getActivity(),R.id.container_fragment,new PairNewDevicesFragment(),PairNewDevicesFragment.FRAGMENT_TAG,"PairNewDevicesFragmentTransition");
            }
            break;
            case R.id.fab_add:{
                // show the time picker dialog
                TimePickerFragment newFragmentNight = new TimePickerFragment(this,1001,"Morning Reminder Time");
                newFragmentNight.show(getFragmentManager(), "timePicker");

            }
            break;
            case R.id.image_delete:{
                CustomAlertDialog.showDialog(getActivity(), "Do you want to delete current reminder?","YES","CANCEL",R.layout.custom_dialo,this);
            }
            break;
        }
    }

    @Override
    public void onTimePicked(Calendar time, int id) {
        if(id == 1001)
        {
            TimePickerFragment newFragmentNight = new TimePickerFragment(this,1002,"Evening Reminder Time");
            newFragmentNight.show(getFragmentManager(), "timePicker");
        }else
        {

            view.findViewById(R.id.layout_create).setVisibility(View.GONE);
            view.findViewById(R.id.layout_active_alarm).setVisibility(View.VISIBLE);
        }
       // SharedPreferenceHelper.setSharedPreferenceBoolean(getActivity(),"isAlarmSet",true);
    }

    @Override
    public void onPositiveClick(Dialog dialog) {
       // SharedPreferenceHelper.setSharedPreferenceBoolean(getActivity(),"isAlarmSet",false);
        view.findViewById(R.id.layout_create).setVisibility(View.VISIBLE);
        view.findViewById(R.id.layout_active_alarm).setVisibility(View.GONE);
    }

    @Override
    public void onNegativeClick(Dialog dialog) {

    }
}

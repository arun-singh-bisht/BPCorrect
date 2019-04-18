package com.protechgene.android.bpconnect.ui.reminder;

import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.adapters.DevicesAdapter;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.custom.CustomAlertDialog;
import com.protechgene.android.bpconnect.ui.custom.TimePickerFragment;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

public class ReminderFragment extends BaseFragment implements  TimePickerFragment.TimePickedListener, CustomAlertDialog.I_CustomAlertDialog {

    public static final String FRAGMENT_TAG = "ReminderFragment";
    private View view;
    private DevicesAdapter bpReadingAdapter;

    @BindView(R.id.layout_create)
    View layout_create;

    @BindView(R.id.layout_active_alarm)
    View layout_active_alarm;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_reminder;
    }

    @Override
    protected void initialize() {
        initView();
    }

    @OnClick(R.id.img_left)
    public void onBackIconClick()
    {
        FragmentUtil.removeFragment(getBaseActivity());
    }

    @OnClick(R.id.fab_add)
    public void onAddClick()
    {
        TimePickerFragment newFragmentNight = new TimePickerFragment(this,1001,"Morning Reminder Time");
        newFragmentNight.show(getFragmentManager(), "timePicker");
    }

    @OnClick(R.id.image_delete)
    public void onDeleteAlarmClick()
    {
        CustomAlertDialog.showDialog(getActivity(), "Do you want to delete current reminder?","YES","CANCEL",R.layout.custom_dialo,this);
    }

    private void initView()
    {
        TextView txt_title =  getView().findViewById(R.id.txt_title);
        txt_title.setText("Reminder");

        //boolean is =SharedPreferenceHelper.getSharedPreferenceBoolean(getActivity(),"isAlarmSet",false);

        boolean is = false;
        if(is)
        {
            layout_create.setVisibility(View.GONE);
            layout_active_alarm.setVisibility(View.VISIBLE);
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

            layout_create.setVisibility(View.GONE);
            layout_active_alarm.setVisibility(View.VISIBLE);
        }
       // SharedPreferenceHelper.setSharedPreferenceBoolean(getActivity(),"isAlarmSet",true);
    }

    @Override
    public void onPositiveClick(Dialog dialog) {
       // SharedPreferenceHelper.setSharedPreferenceBoolean(getActivity(),"isAlarmSet",false);
        layout_create.setVisibility(View.VISIBLE);
        layout_active_alarm.setVisibility(View.GONE);
    }

    @Override
    public void onNegativeClick(Dialog dialog) {

    }
}

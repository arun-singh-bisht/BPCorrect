package com.protechgene.android.bpconnect.ui.reminder;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.DateUtils;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.custom.CustomAlertDialog;

import butterknife.BindView;
import butterknife.OnClick;

public class ActiveProtocolFragment extends BaseFragment implements ActiveProtocolFragmentNavigator, CustomAlertDialog.I_CustomAlertDialog {

    public static final String FRAGMENT_TAG = "ReminderFragment";
    private int DIALOG_REQUEST_CODE_DELETE = 1;
    private int DIALOG_REQUEST_CODE_EDIT_ALARM_MORNING = 2;
    private int DIALOG_REQUEST_CODE_EDIT_ALARM_EVENING = 3;

    private ActiveProtocolViewModel activeProtocolViewModel;
    private ProtocolModel activeProtocol;

    @BindView(R.id.layout_create)
    View layout_create;
    @BindView(R.id.layout_active_alarm)
    View layout_active_alarm;
    @BindView(R.id.text_start_day)
    TextView text_start_day;
    @BindView(R.id.text_end_day)
    TextView text_end_day;
    @BindView(R.id.text_morning_time)
    TextView text_morning_time;
    @BindView(R.id.text_evening_time)
    TextView text_evening_time;
    @BindView(R.id.switch_morning_alram)
    Switch switch_morning_alram;
    @BindView(R.id.switch_evening_alarm)
    Switch switch_evening_alarm;



    @Override
    protected int layoutRes() {
        return R.layout.fragment_reminder;
    }

    @Override
    protected void initialize() {
        activeProtocolViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(ActiveProtocolViewModel.class);
        activeProtocolViewModel.setNavigator(this);
        initView();
    }

    private void initView()
    {

        activeProtocolViewModel.checkActiveProtocol();
    }

    @OnClick(R.id.fab_add)
    public void onAddClick()
    {
        activeProtocolViewModel.createProtocol(getBaseActivity());
    }


    @OnClick(R.id.save_btn)
    public void onsaveClick()
    {

        FragmentUtil.removeFragment(getBaseActivity());
        //CustomAlertDialog.showDialog(getActivity(),DIALOG_REQUEST_CODE_DELETE ,"Do you want to delete current reminder?","YES","CANCEL",R.layout.custom_dialo,this);
    }


    @OnClick(R.id.image_edit_morning_alarm)
    public void onEditMorningAlarm()
    {
        CustomAlertDialog.showDialog(getActivity(),DIALOG_REQUEST_CODE_EDIT_ALARM_MORNING ,"Do you want to change morning alarm time?","YES","CANCEL",R.layout.custom_dialo,this);
    }

    @OnClick(R.id.image_edit_evening_alarm)
    public void onEditEveningAlarm()
    {
        CustomAlertDialog.showDialog(getActivity(),DIALOG_REQUEST_CODE_EDIT_ALARM_EVENING ,"Do you want to change evening alarm time?","YES","CANCEL",R.layout.custom_dialo,this);
    }

    @OnClick(R.id.image_delete)
    public void onDeleteAlarmClick()
    {
        CustomAlertDialog.showDialog(getActivity(),DIALOG_REQUEST_CODE_DELETE ,"Do you want to delete current reminder?","YES","CANCEL",R.layout.custom_dialo,this);
    }

    @Override
    public void onPositiveClick(Dialog dialog,int request_code) {
        if(request_code == DIALOG_REQUEST_CODE_DELETE)
        {
            //Delete current Protocol
            activeProtocolViewModel.deleteProtocol(getBaseActivity(),activeProtocol);


        }else if(request_code == DIALOG_REQUEST_CODE_EDIT_ALARM_MORNING)
        {
            activeProtocolViewModel.updateMorningAlarmTime(getBaseActivity(),activeProtocol);
        }else if(request_code == DIALOG_REQUEST_CODE_EDIT_ALARM_EVENING)
        {
            activeProtocolViewModel.updateEveningAlarmTime(getBaseActivity(),activeProtocol);
        }

    }

    @Override
    public void onNegativeClick(Dialog dialog,int request_code) {

    }

    @Override
    public void handleError(Throwable throwable) {

    }

    @Override
    public void showSearchingProgress() {
        showProgress("Searching for protocol...");
    }

    @Override
    public void isProtocolExists(final boolean status, final ProtocolModel protocolModel) {
        //getBaseActivity().showSnakeBar("Is Protocol Exists ? "+status);
            getBaseActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    hideProgress();

                    if(status) {
                        setProtocolDetails(protocolModel);
                    }else
                    {
                        layout_create.setVisibility(View.VISIBLE);
                        layout_active_alarm.setVisibility(View.GONE);
                        activeProtocol = null;
                    }
                }
            });
    }

    @Override
    public void invalidTimeSelection(String message) {
        showAlert("Invalid Time", message, "OK", new AlertDialogCallback() {
            @Override
            public void onPositiveClick() {

            }
        });
    }

    @Override
    public void onProtocolCreated(ProtocolModel protocolModel) {

        setProtocolDetails(protocolModel);
    }

    @Override
    public void onProtocolDeleted() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout_create.setVisibility(View.VISIBLE);
                layout_active_alarm.setVisibility(View.GONE);
                getBaseActivity().showSnakeBar("Protocol deleted successfully");
            }
        });
    }

    private void setProtocolDetails(ProtocolModel protocolModel)
    {

        layout_create.setVisibility(View.GONE);
        layout_active_alarm.setVisibility(View.VISIBLE);
        //Set Details
        text_start_day.setText(protocolModel.getStartDay());
        text_end_day.setText(protocolModel.getEndDay());
        text_morning_time.setText(DateUtils.conver24hourformatTo12hour(protocolModel.getMorningReadingTime()));
        text_evening_time.setText(DateUtils.conver24hourformatTo12hour(protocolModel.getEveningReadingTime()));
        switch_morning_alram.setChecked(protocolModel.isMorningActive());
        switch_evening_alarm.setChecked(protocolModel.isIseveningActive());
        activeProtocol = protocolModel;

        switch_morning_alram.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                activeProtocol.setMorningActive(isChecked);
                activeProtocolViewModel.saveProtocol(activeProtocol);
            }
        });

        switch_evening_alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                activeProtocol.setIseveningActive(isChecked);
                activeProtocolViewModel.saveProtocol(activeProtocol);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activeProtocolViewModel.onDestroy();
    }

}

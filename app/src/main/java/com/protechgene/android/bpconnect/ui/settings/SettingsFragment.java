package com.protechgene.android.bpconnect.ui.settings;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.custom.CustomAlertDialog;
import com.protechgene.android.bpconnect.ui.login.LoginActivity;
import com.protechgene.android.bpconnect.ui.measureBP.MeasureBPFragmentViewModel;

import butterknife.OnClick;

public class SettingsFragment extends BaseFragment implements  SettingFragmentNavigator,CustomAlertDialog.I_CustomAlertDialog {

    public static final String FRAGMENT_TAG = "SettingsFragment";

    private SettingFragmentViewModel settingFragmentViewModel;


    @Override
    protected int layoutRes() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void initialize() {

        settingFragmentViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(SettingFragmentViewModel.class);
        settingFragmentViewModel.setNavigator(this);
        initView();
    }

    @OnClick(R.id.img_left)
    public void onBackIconClick()
    {
        FragmentUtil.removeFragment(getBaseActivity());
    }


    @OnClick(R.id.text_signout)
    public void onSignOutClick()
    {
        CustomAlertDialog.showDialog(getActivity(),0, "Do you want to sign out from app?","YES","CANCEL",R.layout.custom_dialo,this);
    }

    private void initView()
    {
        TextView txt_title =  getView().findViewById(R.id.txt_title);
        txt_title.setText("Settings");
    }

    @Override
    public void onPositiveClick(Dialog dialog,int request_code) {
        settingFragmentViewModel.signOut();
    }

    @Override
    public void onNegativeClick(Dialog dialog,int request_code) {

    }

    @Override
    public void onSignOut() {
        getBaseActivity().startActivity(new Intent(getContext(), LoginActivity.class));
        getBaseActivity().finish();
    }

    @Override
    public void handleError(Throwable throwable) {

    }


}

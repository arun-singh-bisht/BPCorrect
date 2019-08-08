package com.protechgene.android.bpconnect.ui.settings;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.changepassword.ChangePasswordFragment;
import com.protechgene.android.bpconnect.ui.custom.CustomAlertDialog;
import com.protechgene.android.bpconnect.ui.login.LoginActivity;
import com.protechgene.android.bpconnect.ui.measureBP.MeasureBPFragmentViewModel;
import com.protechgene.android.bpconnect.ui.profile.ProfileFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingsFragment extends BaseFragment implements  SettingFragmentNavigator,CustomAlertDialog.I_CustomAlertDialog, CompoundButton.OnCheckedChangeListener {

    public static final String FRAGMENT_TAG = "SettingsFragment";

    private SettingFragmentViewModel settingFragmentViewModel;


    @BindView(R.id.text_not_provider)
    TextView text_not_provider;

    @BindView(R.id.switcher_provider)
    Switch switcher_provider;

//    @BindView(R.id.text_change_password)
//    TextView changepassword;


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

    @OnClick(R.id.text_change_password)
    public void changePassword() {
        FragmentUtil.loadFragment(getBaseActivity(),R.id.container_fragment,new ChangePasswordFragment(),ChangePasswordFragment.FRAGMENT_TAG,"ProfileFragmentTransition");
    }

    private void initView()
    {
        TextView txt_title =  getView().findViewById(R.id.txt_title);
        txt_title.setText("Settings");

        String org = settingFragmentViewModel.getOrgName();
         if(org.equals("")){
             switcher_provider.setEnabled(false);
             text_not_provider.setVisibility(View.VISIBLE);
         }else{
             text_not_provider.setVisibility(View.VISIBLE);
             text_not_provider.setTextColor(getResources().getColor(R.color.color_light_green));
             text_not_provider.setText("Your are part of "+org);
             switcher_provider.setOnCheckedChangeListener(this);
             settingFragmentViewModel.fetchSwitcher("get",true);
         }
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
// fetch switch
    @Override
    public void fetchSwitcher(Boolean status) {
            switcher_provider.setChecked(status);
    }

    @Override
    public void snackBar(String msg) {
        getBaseActivity().showSnakeBar(msg);
    }

    @Override
    public void handleError(Throwable throwable) {

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d("sohit", "onCheckedChanged: "+isChecked);
         settingFragmentViewModel.fetchSwitcher("post",isChecked);
    }
}

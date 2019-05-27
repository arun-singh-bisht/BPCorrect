package com.protechgene.android.bpconnect.ui.devices;


import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.Utils.GpsUtils;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class DevicesListFragment extends BaseFragment implements DevicesListNavigator{

    public static final String FRAGMENT_TAG = "DevicesListFragment";

    private DevicesListViewModel devicesListFragmentViewModel;
    @BindView(R.id.list_devies_ll)
    LinearLayout list_devies_ll;
    @BindView(R.id.instruction_constraint_layout)
    ConstraintLayout instruction_layout;
    @BindView(R.id.instruction_constraint_layout_ihealth)
    ConstraintLayout instruction_layout_ihealth;
    @BindView(R.id.instruction_img_ihealth)
    TextView instruction_text;
    @BindView(R.id.instruction_img)
    ImageView instruction_img;

    public DevicesListFragment() {
        // Required empty public constructor
    }


    @Override
    protected int layoutRes() {
        return R.layout.fragment_devices_list;
    }

    @Override
    protected void initialize() {
        devicesListFragmentViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(DevicesListViewModel.class);
        devicesListFragmentViewModel.setNavigator(this);

        ((TextView)getView().findViewById(R.id.txt_title)).setText("Devices");
    }

    @OnClick(R.id.img_left)
    public void onBackIconClick()
    {
        FragmentUtil.removeFragment(getBaseActivity());
    }

    @OnClick(R.id.device_omron_ll)
    public void omron_ll(){
        list_devies_ll.setVisibility(View.GONE);
        // instruction_layout.setVisibility(View.VISIBLE);
        instruction_layout_ihealth.setVisibility(View.VISIBLE);
        instruction_text.setText("Coming Soon.......");
        //  instruction_img.setImageDrawable(getResources().getDrawable(R.drawable.instruction_bp_pairing));
    }

    @OnClick(R.id.device_ihealth_ll)
    public void device_ihealth_ll(){
        list_devies_ll.setVisibility(View.GONE);
       // instruction_layout.setVisibility(View.VISIBLE);
        instruction_layout_ihealth.setVisibility(View.VISIBLE);
       // instruction_img.setImageDrawable(getResources().getDrawable(R.drawable.instruction_bp_pairing));
    }

    @OnClick(R.id.device_a_d_ll)
    public void device_a_d_ll(){
        list_devies_ll.setVisibility(View.GONE);
        instruction_layout.setVisibility(View.VISIBLE);
        instruction_img.setImageDrawable(getResources().getDrawable(R.drawable.instruction_bp_pairing));
    }

    @OnClick(R.id.device_trans_prak_ll)
    public void device_trans_prak_ll(){
        list_devies_ll.setVisibility(View.GONE);
       // instruction_layout.setVisibility(View.VISIBLE);
        instruction_layout_ihealth.setVisibility(View.VISIBLE);
        instruction_text.setText("Coming Soon.......");
      //  instruction_img.setImageDrawable(getResources().getDrawable(R.drawable.instruction_bp_pairing));
    }

    @OnClick(R.id.next_btn)
    public void next_btn(){
        OpenPairNewDevices();
    }
    @OnClick(R.id.next_btn_ihealth)
    public void next_btn_ihealth(){
        FragmentUtil.removeFragment(getContext());
    }

    private void OpenPairNewDevices(){
        GpsUtils gpsUtils = new GpsUtils(getBaseActivity());
        gpsUtils.turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                if(isGPSEnable) {
                    openScanningScreen(0);
                    //showDeviceMenu();
                }
            }
        });
    }


    private void openScanningScreen(int forModel)
    {
        PairNewDevicesFragment pairNewDevicesFragment = new PairNewDevicesFragment();
        Bundle bundle = new Bundle();


        if(forModel==0)
        {
            //for A&D Device
            bundle.putString("deviceModel","A&D__651BLE");
            pairNewDevicesFragment.setArguments(bundle);
            FragmentUtil.loadFragment(getActivity(),R.id.container_fragment,pairNewDevicesFragment,PairNewDevicesFragment.FRAGMENT_TAG,"PairNewDevicesFragmentTransition");
        }else if(forModel==1)
        {
            //for iHealth BP3L
            boolean isPass = devicesListFragmentViewModel.isAuthorizeForBP3L(getBaseActivity());
            if (isPass) {
                bundle.putString("deviceModel","iHealth_BP3L");
                pairNewDevicesFragment.setArguments(bundle);
                FragmentUtil.loadFragment(getActivity(),R.id.container_fragment,pairNewDevicesFragment,PairNewDevicesFragment.FRAGMENT_TAG,"PairNewDevicesFragmentTransition");
            } else {
                getBaseActivity().showSnakeBar("Not authorized to access this device.");
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GpsUtils.GPS_REQUEST && resultCode==getBaseActivity().RESULT_OK)
        {
            showDeviceMenu();
            //openScanningScreen(0);
        }


        Log.d("onActivityResult",""+requestCode+" "+resultCode);
    }


    private void showDeviceMenu()
    {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        builder.setTitle("Select Model");

        // add a list
        final String[] gender = {"A&D UA-651BLE", "iHealth BP3L"};
        builder.setItems(gender, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openScanningScreen(which);
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }




}

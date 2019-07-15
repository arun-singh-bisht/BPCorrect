package com.protechgene.android.bpconnect.ui.devices.ConnectDevice;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.deviceManager.iHealthbp3l.DeviceCharacteristic;
import com.protechgene.android.bpconnect.ui.adapters.DevicesAdapter;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.skyfishjy.library.RippleBackground;

import butterknife.BindView;
import butterknife.OnClick;

public class PairNewDevicesFragment extends BaseFragment implements PairNewDeviceNavigator {

    public static final String FRAGMENT_TAG = "PairNewDevicesFragment";
    private DevicesAdapter bpReadingAdapter;
    RippleBackground rippleBackground;
    private PairDeviceViewModelInterface pairNewDeviceViewModel;
    private DeviceCharacteristic mdeviceCharacteristic;
    private String deviceModel = "";


    @BindView(R.id.image_found_device)
    View deviceFound;
    @BindView(R.id.content)
    View content;
    @BindView(R.id.layout_pair)
    View layout_pair;
    @BindView(R.id.text_device_name)
    TextView device_name;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_pair_new_devices;
    }

    @Override
    protected void initialize() {
        //Get Device Model Type
        Bundle arguments = getArguments();
        deviceModel = arguments.getString("deviceModel");

        if(deviceModel.equalsIgnoreCase("A&D__651BLE"))
        {
            pairNewDeviceViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(PairNewDeviceViewModel.class);
            ((PairNewDeviceViewModel)pairNewDeviceViewModel).setNavigator(this);
            //pairNewDeviceViewModel.initScan(getBaseActivity());
        }else if(deviceModel.equalsIgnoreCase("iHealth_BP3L"))
        {
            pairNewDeviceViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(PairNewDeviceViewModelBP3N.class);
            ((PairNewDeviceViewModelBP3N)pairNewDeviceViewModel).setNavigator(this);

        }else if(deviceModel.equalsIgnoreCase("Transtek_1491B"))
        {
            pairNewDeviceViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(PairNewDeviceViewModelTranstek.class);
            ((PairNewDeviceViewModelTranstek)pairNewDeviceViewModel).setNavigator(this);
        }

        initView();
    }

    @OnClick(R.id.img_left)
    public void onBackIconClick()
    {
        FragmentUtil.removeFragment(getBaseActivity());
    }

    private void initView()
    {
        TextView txt_title =  getView().findViewById(R.id.txt_title);
        txt_title.setText("Pair New Device");

        rippleBackground=(RippleBackground)getView().findViewById(R.id.content);
        ImageView imageView=(ImageView)getView().findViewById(R.id.centerImage);
        rippleBackground.startRippleAnimation();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        pairNewDeviceViewModel.initScan(getBaseActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rippleBackground.stopRippleAnimation();
        pairNewDeviceViewModel.onDestroy();
    }


    @Override
    public void handleError(Throwable throwable) {
        getBaseActivity().showSnakeBar(throwable.getMessage());
    }

    @Override
    public void onDeviceFound(DeviceCharacteristic deviceCharacteristic) {

        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceFound.setVisibility(View.VISIBLE);
                TextView text_device_name = (TextView)getView().findViewById(R.id.text_device_name);
                text_device_name.setText(deviceCharacteristic.getDeviceName());
                mdeviceCharacteristic = deviceCharacteristic;
            }
        });
    }

    @OnClick(R.id.image_found_device)
    public void onSelectDevice()
    {
        deviceFound.setClickable(false);
        device_name.setText("Connecting...");
        pairNewDeviceViewModel.connectToDevice(mdeviceCharacteristic.getDeviceName(),mdeviceCharacteristic.getDeviceMac(),"");
    }

    @Override
    public void onDevicePaired() {

        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FragmentUtil.removeFragment(getContext());
                getBaseActivity().showSnakeBar("Device Paired Successfully");
            }
        });
    }

}

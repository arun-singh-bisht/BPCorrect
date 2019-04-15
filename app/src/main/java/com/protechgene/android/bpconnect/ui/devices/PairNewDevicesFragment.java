package com.protechgene.android.bpconnect.ui.devices;

import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.adapters.DevicesAdapter;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.skyfishjy.library.RippleBackground;

import butterknife.BindView;
import butterknife.OnClick;

public class PairNewDevicesFragment extends BaseFragment implements PairNewDeviceNavigator{

    public static final String FRAGMENT_TAG = "PairNewDevicesFragment";
    private DevicesAdapter bpReadingAdapter;
    RippleBackground rippleBackground;
    private PairNewDeviceViewModel pairNewDeviceViewModel;
    private BluetoothDevice device;

    @BindView(R.id.image_found_device)
    View deviceFound;
    @BindView(R.id.content)
    View content;
    @BindView(R.id.layout_pair)
    View layout_pair;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_pair_new_devices;
    }

    @Override
    protected void initialize() {
        pairNewDeviceViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(PairNewDeviceViewModel.class);
        pairNewDeviceViewModel.setNavigator(this);

        initView();
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
    public void onDeviceFound(BluetoothDevice device) {
        deviceFound.setVisibility(View.VISIBLE);
        TextView text_device_name = (TextView)getView().findViewById(R.id.text_device_name);
        text_device_name.setText(device.getName());
        this.device = device;
    }

    @OnClick(R.id.image_found_device)
    public void onSelectDevice()
    {
        pairNewDeviceViewModel.connectToDevice(device.getAddress());
    }

    @Override
    public void onDevicePaired() {
        FragmentUtil.removeFragment(getContext());
        getBaseActivity().showSnakeBar("Device Paired Successfully");
    }


}

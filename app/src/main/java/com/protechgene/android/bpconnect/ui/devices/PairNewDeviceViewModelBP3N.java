package com.protechgene.android.bpconnect.ui.devices;

import android.content.Context;

import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.ble.BleConnectService;
import com.protechgene.android.bpconnect.deviceManager.iHealthbp3l.DeviceCharacteristic;
import com.protechgene.android.bpconnect.deviceManager.iHealthbp3l.IHealthDeviceController;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;



public class PairNewDeviceViewModelBP3N extends BaseViewModel<PairNewDeviceNavigator> implements PairDeviceViewModelInterface,IHealthDeviceController.iHealthCallback {


    private Context context;
    protected BleConnectService mBleService;
    private IHealthDeviceController iHealthDeviceController;

    String PREF_KEY_BP_DEVICE_NAME_iHealthbp3l = "PREF_KEY_BP_DEVICE_NAME_iHealthbp3l";
    String PREF_KEY_BP_DEVICE_ADDRESS_iHealthbp3l = "PREF_KEY_BP_DEVICE_ADDRESS_iHealthbp3l";

    public PairNewDeviceViewModelBP3N(Repository repository) {
        super(repository);

    }

    public void initScan(Context context)
    {
        this.context = context;
        iHealthDeviceController = new IHealthDeviceController(context);
        iHealthDeviceController.discoverDevice(this);
    }


    public void connectToDevice(String deviceName,String mac,String username)
    {
        iHealthDeviceController.ConnectDevice(deviceName, mac, username);
    }

    public void onDestroy() {
        iHealthDeviceController.onStop();
    }

    //-------------------------------- iHealthCallback methods --------------------------------------------------------
    @Override
    public void onDeviceDetected_BP3L(DeviceCharacteristic deviceCharacteristic) {
        getNavigator().onDeviceFound(deviceCharacteristic);
    }

    @Override
    public void onScanCompleted_BP3L() {

    }

    @Override
    public void onDeviceConnected_BP3L(String deviceName, String deviceMac) {
        getRespository().setDeviceName_iHealthbp3l(deviceName);
        getRespository().setDeviceAddress_iHealthbp3l(deviceMac);
        getNavigator().onDevicePaired();
    }

    @Override
    public void onConnectionError_BP3L(String messg) {
        Throwable throwable = new Throwable(messg);
        getNavigator().handleError(throwable);
    }

    @Override
    public void onDeviceDisconnected_BP3L() {

    }

    @Override
    public void onReadingResult(String sys, String dia, String pulse) {

    }

    @Override
    public void onError(String msg) {

    }


}

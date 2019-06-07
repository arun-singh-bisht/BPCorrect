package com.protechgene.android.bpconnect.ui.devices.ConnectDevice;

import android.content.Context;

import com.lifesense.ble.bean.LsDeviceInfo;
import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.ble.BleConnectService;
import com.protechgene.android.bpconnect.deviceManager.Transtek.TranstekDeviceController;
import com.protechgene.android.bpconnect.deviceManager.iHealthbp3l.DeviceCharacteristic;
import com.protechgene.android.bpconnect.deviceManager.iHealthbp3l.IHealthDeviceController;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;


public class PairNewDeviceViewModelTranstek extends BaseViewModel<PairNewDeviceNavigator> implements PairDeviceViewModelInterface,TranstekDeviceController.TranstekControllerCallback {


    private Context context;
    protected BleConnectService mBleService;
    TranstekDeviceController transtekDeviceController;

    String PREF_KEY_BP_DEVICE_NAME_iHealthbp3l = "PREF_KEY_BP_DEVICE_NAME_iHealthbp3l";
    String PREF_KEY_BP_DEVICE_ADDRESS_iHealthbp3l = "PREF_KEY_BP_DEVICE_ADDRESS_iHealthbp3l";

    public PairNewDeviceViewModelTranstek(Repository repository) {
        super(repository);

    }

    public void initScan(Context context)
    {
        this.context = context;
        transtekDeviceController = new TranstekDeviceController(context);
        transtekDeviceController.discoverDevice(this);
    }


    public void connectToDevice(String deviceName,String mac,String username)
    {
        transtekDeviceController.ConnectDevice(deviceName, mac, username);
    }

    public void onDestroy() {
        transtekDeviceController.onStop();
    }

    //-------------------------------- iHealthCallback methods --------------------------------------------------------

    @Override
    public void onDeviceDetected_Transtek(LsDeviceInfo foundDevice) {
        DeviceCharacteristic deviceCharacteristic = new DeviceCharacteristic();
        deviceCharacteristic.setDeviceMac(foundDevice.getMacAddress());
        deviceCharacteristic.setDeviceName(foundDevice.getDeviceName());
        deviceCharacteristic.setDeviceType(0);
        getNavigator().onDeviceFound(deviceCharacteristic);
    }

    @Override
    public void onScanCompleted_Transtek() {

    }

    @Override
    public void onDeviceConnected_Transtek(String deviceName, String deviceMac) {
        //getRespository().setDeviceName_iHealthbp3l(deviceName);
        //getRespository().setDeviceAddress_iHealthbp3l(deviceMac);
        getNavigator().onDevicePaired();
    }

    @Override
    public void onConnectionError_Transtek(String messg) {
        Throwable throwable = new Throwable(messg);
        getNavigator().handleError(throwable);
    }

    @Override
    public void onDeviceDisconnected_Transtek() {

    }

    @Override
    public void onReadingResult(String sys, String dia, String pulse) {

    }

    @Override
    public void onError(String msg) {

    }


}

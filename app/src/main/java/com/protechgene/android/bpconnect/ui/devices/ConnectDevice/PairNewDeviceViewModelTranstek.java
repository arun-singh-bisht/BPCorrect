package com.protechgene.android.bpconnect.ui.devices.ConnectDevice;

import android.content.Context;

import com.lifesense.ble.bean.LsDeviceInfo;
import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.ble.BleConnectService;
import com.protechgene.android.bpconnect.deviceManager.Transtek.AsyncTaskRunner;
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

    @Override
    public void connectToDevice(String deviceName, String mac, String username) {

    }


    @Override
    public void connectToDevice(LsDeviceInfo lsDeviceInfo) {
        transtekDeviceController.ConnectDevice(lsDeviceInfo);
    }

    public void onDestroy() {
        transtekDeviceController.onStop();
    }

    //-------------------------------- iHealthCallback methods --------------------------------------------------------

    @Override
    public void onDeviceDetected_Transtek(LsDeviceInfo foundDevice) {
        getNavigator().onDeviceFound(foundDevice);
    }

    @Override
    public void onScanCompleted_Transtek() {

    }

    @Override
    public void onDeviceConnected_Transtek(LsDeviceInfo lsDeviceInfo) {
        //save connected device info in shared preference
        AsyncTaskRunner.savePairedDeviceInfo(context,lsDeviceInfo);
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
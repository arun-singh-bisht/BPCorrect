package com.protechgene.android.bpconnect.ui.devices.ConnectDevice;

import android.content.Context;

import com.lifesense.ble.bean.LsDeviceInfo;

public interface PairDeviceViewModelInterface {

    void initScan(Context context);
    void connectToDevice(String deviceName, String mac, String username);
    void connectToDevice(LsDeviceInfo foundDevice);
    void onDestroy();
}

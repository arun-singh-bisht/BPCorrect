package com.protechgene.android.bpconnect.ui.devices.ConnectDevice;

import android.content.Context;

public interface PairDeviceViewModelInterface {

    void initScan(Context context);
    void connectToDevice(String deviceName, String mac, String username);
    void onDestroy();
}

package com.protechgene.android.bpconnect.ui.devices.ConnectDevice;

import com.protechgene.android.bpconnect.deviceManager.iHealthbp3l.DeviceCharacteristic;

public interface PairNewDeviceNavigator {


    void handleError(Throwable throwable);
    void onDeviceFound(DeviceCharacteristic deviceCharacteristic);
    void onDevicePaired();

}

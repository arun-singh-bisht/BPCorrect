package com.protechgene.android.bpconnect.ui.devices;

import android.bluetooth.BluetoothDevice;

public interface PairNewDeviceNavigator {


    void handleError(Throwable throwable);
    void onDeviceFound(BluetoothDevice device);
    void onDevicePaired();

}

package com.protechgene.android.bpconnect.ui.devices.PairedDevice;

import com.protechgene.android.bpconnect.data.local.models.BPDeviceModel;

import java.util.List;

public interface DeviceFragmentNavigator {


    void handleError(Throwable throwable);

    void bluetoothNotSupported(String msg);

    void turningOnBluetooth();

    void pairedDevices(List<BPDeviceModel> bpDeviceList);
}

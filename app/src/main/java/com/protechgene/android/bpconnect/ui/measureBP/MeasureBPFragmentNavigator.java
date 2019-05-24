package com.protechgene.android.bpconnect.ui.measureBP;

import com.protechgene.android.bpconnect.data.ble.Lifetrack_infobean;
import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.deviceManager.iHealthbp3l.DeviceCharacteristic;

public interface MeasureBPFragmentNavigator {


    void handleError(Throwable throwable);

    void turningOnBluetooth();
    void AND_DevicePairedStatus(boolean status);
    void onScanningStarted_iHealthBP3L(boolean status);
    void onDeviceFound_iHealthBP3L(String deviceName,String deviceMac,String deviceType);
    void onDeviceConnected_iHealthBP3L(String deviceName,String deviceMac);

    void setIndicatorMessage(String message);
    void showIndicator(String message);
    void dismissIndicator();
    void result(HealthReading healthReading);

    void isReadingForProtocol_Result(boolean b,String protocolCode,int readingTaken);
}

package com.protechgene.android.bpconnect.ui.measureBP;

import com.protechgene.android.bpconnect.data.ble.Lifetrack_infobean;

public interface MeasureBPFragmentNavigator {


    void handleError(Throwable throwable);

    void bpDevicePairedStatus(boolean status);
    void turningOnBluetooth();

    void setIndicatorMessage(String message);
    void showIndicator(String message);
    void dismissIndicator();

    void result(Lifetrack_infobean lifetrackInfobean);

}

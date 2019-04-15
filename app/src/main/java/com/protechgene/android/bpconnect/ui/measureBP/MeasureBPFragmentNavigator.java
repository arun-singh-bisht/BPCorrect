package com.protechgene.android.bpconnect.ui.measureBP;

public interface MeasureBPFragmentNavigator {


    void handleError(Throwable throwable);

    void connectedToDevice();
    void disconnectedFromDevice();

}

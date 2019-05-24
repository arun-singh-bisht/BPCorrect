package com.protechgene.android.bpconnect.data.local.models;

public class BPDeviceModel {
    private String deviceName;
    private String deviceAddress;

    public BPDeviceModel(String deviceName, String deviceAddress) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }
}

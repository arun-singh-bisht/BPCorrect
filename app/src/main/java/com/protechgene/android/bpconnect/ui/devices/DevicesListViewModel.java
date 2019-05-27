package com.protechgene.android.bpconnect.ui.devices;

import android.content.Context;

import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.deviceManager.iHealthbp3l.IHealthDeviceController;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

public class DevicesListViewModel extends BaseViewModel<DevicesListNavigator> {
    public DevicesListViewModel(Repository repository) {
        super(repository);
    }


    public boolean isAuthorizeForBP3L(Context mContext)
    {
        return IHealthDeviceController.isAuthorizedToAccessDevice(mContext);
    }

}

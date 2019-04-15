package com.protechgene.android.bpconnect.ui.devices;


import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.local.models.BPDeviceModel;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;
import com.protechgene.android.bpconnect.ui.profile.ProfileFragmentNavigator;

import java.util.ArrayList;
import java.util.List;


public class DeviceFragmentViewModel extends BaseViewModel<DeviceFragmentNavigator> {


    public DeviceFragmentViewModel(Repository repository) {
        super(repository);
    }

    public List<BPDeviceModel> getPairedDeviceList()
    {
        List<BPDeviceModel> modelList = new ArrayList<>();

        String bpDeviceAddress = getRespository().getBPDeviceAddress();
        String bpDeviceName = getRespository().getBPDeviceName();

        if(bpDeviceAddress!=null)
            modelList.add(new BPDeviceModel(bpDeviceName,bpDeviceAddress));

        return modelList;

    }
}

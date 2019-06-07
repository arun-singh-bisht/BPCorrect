package com.protechgene.android.bpconnect.ui.devices.PairedDevice;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.ble.BleConnectService;
import com.protechgene.android.bpconnect.data.local.models.BPDeviceModel;
import com.protechgene.android.bpconnect.deviceManager.iHealthbp3l.IHealthDeviceController;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class DeviceFragmentViewModel extends BaseViewModel<DeviceFragmentNavigator> {

    private Context mContext;

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

    public void findAllPairedDevices(Context context)
    {
        mContext = context;

        // Checks if BlueTooth is unSupported on the device.
        if (!BleConnectService.isEnableBluetoothFunction(context)) {
            //Throwable throwable = new IllegalArgumentException("Bluetooth not supported");
            getNavigator().bluetoothNotSupported("Bluetooth not supported");
            return;
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager.getAdapter().isEnabled())
        {
            //Bluetooth is ON
            // Get paired devices.
            List<BPDeviceModel> bpDeviceList= new ArrayList<>();
            Set<BluetoothDevice> pairedDevices = bluetoothManager.getAdapter().getBondedDevices();
            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    if(!deviceName.contains("651BLE"))
                        continue;
                    String deviceHardwareAddress = device.getAddress();
                    BPDeviceModel bpDeviceModel = new BPDeviceModel(deviceName,deviceHardwareAddress);
                    bpDeviceList.add(bpDeviceModel);
                    Log.d("PairedDevices","deviceName:"+deviceName+" deviceHardwareAddress:"+deviceHardwareAddress);
                }
            }
           // IHealthDeviceController iHealthDeviceController = new IHealthDeviceController(context);
            //List<String> connectedDevices = iHealthDeviceController.getConnectedDevices();

            String deviceName_iHealthbp3l = getRespository().getDeviceName_iHealthbp3l();
            String deviceAddress_iHealthbp3l = getRespository().getDeviceAddress_iHealthbp3l();
            if(deviceName_iHealthbp3l!=null && !deviceName_iHealthbp3l.isEmpty())
            {
                BPDeviceModel bpDeviceModel = new BPDeviceModel("iHealth Ease "+deviceName_iHealthbp3l,deviceAddress_iHealthbp3l);
                bpDeviceList.add(bpDeviceModel);
            }

            getNavigator().pairedDevices(bpDeviceList);

        }else {
            bluetoothManager.getAdapter().enable();
            getNavigator().turningOnBluetooth();
            //Bluetooth is OFF
        }
    }

    public boolean isAuthorizeForBP3L(Context mContext)
    {
        return IHealthDeviceController.isAuthorizedToAccessDevice(mContext);
    }
}

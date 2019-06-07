package com.protechgene.android.bpconnect.deviceManager.Transtek;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.lifesense.ble.LsBleManager;
import com.lifesense.ble.PairCallback;
import com.lifesense.ble.ReceiveDataCallback;
import com.lifesense.ble.SearchCallback;
import com.lifesense.ble.bean.BloodPressureData;
import com.lifesense.ble.bean.LsDeviceInfo;
import com.lifesense.ble.bean.PairedConfirmInfo;
import com.lifesense.ble.bean.constant.BroadcastType;
import com.lifesense.ble.bean.constant.DeviceConnectState;
import com.lifesense.ble.bean.constant.DeviceRegisterState;
import com.lifesense.ble.bean.constant.DeviceType;
import com.lifesense.ble.bean.constant.OperationCommand;
import com.lifesense.ble.bean.constant.PairedConfirmState;

import java.util.ArrayList;
import java.util.List;

public class TranstekDeviceController {

    private static final String TAG = "TranstekDeviceController";
    private Context mContext;

    private LsDeviceInfo foundDevice;
    private TranstekControllerCallback mTranstekControllerCallback;

    public interface TranstekControllerCallback
    {
        void onDeviceDetected_Transtek(LsDeviceInfo foundDevice);
        void onScanCompleted_Transtek();
        void onDeviceConnected_Transtek(String deviceName,String deviceMac);
        void onConnectionError_Transtek(String messg);
        void onDeviceDisconnected_Transtek();
        void onReadingResult(String sys,String dia,String pulse);
        void onError(String msg);
    }

    public TranstekDeviceController(Context context) {
        this.mContext = context;
        LsBleManager.getInstance().initialize(mContext);
        //register bluetooth broadacst receiver
        LsBleManager.getInstance().registerBluetoothBroadcastReceiver(mContext);
    }

    public TranstekDeviceController(Context context,TranstekControllerCallback transtekControllerCallback) {
        this.mContext = context;
        mTranstekControllerCallback = transtekControllerCallback;
        LsBleManager.getInstance().initialize(mContext);
        //register bluetooth broadacst receiver
        LsBleManager.getInstance().registerBluetoothBroadcastReceiver(mContext);
    }

    public void discoverDevice(TranstekControllerCallback transtekControllerCallback)
    {
            if(mTranstekControllerCallback==null)
                mTranstekControllerCallback = transtekControllerCallback;

            List<DeviceType> list=new ArrayList<DeviceType>();
            list.add(DeviceType.SPHYGMOMANOMETER);
            LsBleManager.getInstance().searchLsDevice(mSearchCallback, list, BroadcastType.ALL);
    }


    public void ConnectDevice(String type, String mac, String userName) {
        LsBleManager.getInstance().stopSearch();
        LsBleManager.getInstance().bindDeviceUser(foundDevice.getMacAddress(),1, "bpcorrect");
        LsBleManager.getInstance().pairingWithDevice(foundDevice, mPairCallback);
    }

    public void onStop() {

    }

    public void measeureReading(String mDeviceName,String mDeviceMac)
    {
        LsDeviceInfo lsDevice = new LsDeviceInfo();
        lsDevice.setDeviceName(mDeviceName);
        lsDevice.setMacAddress(mDeviceMac);
        LsBleManager.getInstance().connectDeviceWithAddress(lsDevice,mDataCallback);
    }

    public void stopMeaseureReading()
    {

    }

    public void disconnect()
    {

    }

    private SearchCallback mSearchCallback= new SearchCallback()
    {
        @Override
        public void onSearchResults(final LsDeviceInfo lsDevice)
        {
            foundDevice = lsDevice;
            mTranstekControllerCallback.onDeviceDetected_Transtek(foundDevice);
        }
    };

    // device pairing callback status
    private PairCallback mPairCallback=new PairCallback()
    {
        @Override
        public void onPairResults(final LsDeviceInfo lsDevice,final int status)
        {
            showLog("mPairCallback onPairResults:"+lsDevice.getMacAddress()+" "+status);
            mTranstekControllerCallback.onDeviceConnected_Transtek(lsDevice.getDeviceName(),lsDevice.getMacAddress());
        }

        @Override
        public void onDeviceOperationCommandUpdate(String macAddress,
                                                   OperationCommand cmd, Object obj) {
            showLog("mPairCallback onDeviceOperationCommandUpdate operation command update >> "+cmd+"; from device="+macAddress);
            if(OperationCommand.CMD_DEVICE_ID == cmd)
            {
                //input device id
                String deviceId=macAddress.replace(":", "");//"ff028a0003e1";
                //register device's id for device
                LsBleManager.getInstance().registeringDeviceID(macAddress, deviceId, DeviceRegisterState.NORMAL_UNREGISTER);
            }
            else if(OperationCommand.CMD_PAIRED_CONFIRM == cmd){
                PairedConfirmInfo confirmInfo=new PairedConfirmInfo(PairedConfirmState.PAIRING_SUCCESS);
                confirmInfo.setUserNumber(1);
                LsBleManager.getInstance().inputOperationCommand(macAddress, cmd, confirmInfo);
            }
        }

        @Override
        public void onDiscoverUserInfo(String macAddress,final List userList)
        {
            showLog("mPairCallback onDiscoverUserInfo: "+macAddress+" ");
            LsBleManager.getInstance().bindDeviceUser(foundDevice.getMacAddress(),2, "bpcorrect");
        }
    };


    /**
     * Device measurement data synchronization callback object
     */
    private ReceiveDataCallback mDataCallback=new ReceiveDataCallback()
    {
        @Override
        public void onDeviceConnectStateChange(DeviceConnectState connectState, String broadcastId)
        {
            //Device Connection Status
            //updateDeviceConnectState(connectState);
            showLog("onDeviceConnectStateChange "+connectState.toString());
        }

        @Override
        public void onReceiveBloodPressureData(BloodPressureData bpData)
        {
           // updateNewDatMessage();
            showLog("onReceiveBloodPressureData "+bpData.toString());
        }
    };

    public void showLog(String log) {
        Log.d(TAG, log);
        //iHealthDevicesManager.getInstance().get
    }
}

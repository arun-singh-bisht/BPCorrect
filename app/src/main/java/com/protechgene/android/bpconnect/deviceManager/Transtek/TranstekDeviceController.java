package com.protechgene.android.bpconnect.deviceManager.Transtek;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.lifesense.ble.LsBleManager;
import com.lifesense.ble.PairCallback;
import com.lifesense.ble.ReceiveDataCallback;
import com.lifesense.ble.SearchCallback;
import com.lifesense.ble.bean.BloodGlucoseData;
import com.lifesense.ble.bean.BloodPressureData;
import com.lifesense.ble.bean.KitchenScaleData;
import com.lifesense.ble.bean.LsDeviceInfo;
import com.lifesense.ble.bean.PairedConfirmInfo;
import com.lifesense.ble.bean.PedometerData;
import com.lifesense.ble.bean.SportNotify;
import com.lifesense.ble.bean.WeightData_A2;
import com.lifesense.ble.bean.WeightData_A3;
import com.lifesense.ble.bean.WeightUserInfo;
import com.lifesense.ble.bean.constant.BroadcastType;
import com.lifesense.ble.bean.constant.DeviceConnectState;
import com.lifesense.ble.bean.constant.DeviceRegisterState;
import com.lifesense.ble.bean.constant.DeviceType;
import com.lifesense.ble.bean.constant.DeviceTypeConstants;
import com.lifesense.ble.bean.constant.ManagerStatus;
import com.lifesense.ble.bean.constant.OperationCommand;
import com.lifesense.ble.bean.constant.PacketProfile;
import com.lifesense.ble.bean.constant.PairedConfirmState;
import com.lifesense.ble.bean.constant.ProtocolType;
import com.lifesense.ble.bean.constant.SexType;
import com.lifesense.ble.bean.constant.UnitType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TranstekDeviceController {

    private static final String TAG = "TrnstekDevicController";
    private Context mContext;

    private LsDeviceInfo foundDevice;
    private TranstekControllerCallback mTranstekControllerCallback;

    public interface TranstekControllerCallback {
        void onDeviceDetected_Transtek(LsDeviceInfo foundDevice);

        void onScanCompleted_Transtek();

        void onDeviceConnected_Transtek(LsDeviceInfo lsDeviceInfo);

        void onConnectionError_Transtek(String messg);

        void onDeviceDisconnected_Transtek();

        void onReadingResult(String sys, String dia, String pulse);

        void onError(String msg);
    }

    public TranstekDeviceController(Context context) {
        this.mContext = context;
        LsBleManager.getInstance().initialize(mContext);
        //register bluetooth broadacst receiver
        LsBleManager.getInstance().registerBluetoothBroadcastReceiver(mContext);
    }

    public TranstekDeviceController(Context context, TranstekControllerCallback transtekControllerCallback) {
        this.mContext = context;
        mTranstekControllerCallback = transtekControllerCallback;
        LsBleManager.getInstance().initialize(mContext);
        //register bluetooth broadacst receiver
        LsBleManager.getInstance().registerBluetoothBroadcastReceiver(mContext);
    }

    public void discoverDevice(TranstekControllerCallback transtekControllerCallback) {
        if (mTranstekControllerCallback == null)
            mTranstekControllerCallback = transtekControllerCallback;

        List<DeviceType> list = new ArrayList<DeviceType>();
        list.add(DeviceType.SPHYGMOMANOMETER);
        LsBleManager.getInstance().searchLsDevice(mSearchCallback, list, BroadcastType.ALL);
    }


    public void ConnectDevice(LsDeviceInfo lsDeviceInfo) {
        LsBleManager.getInstance().stopSearch();
        LsBleManager.getInstance().bindDeviceUser(lsDeviceInfo.getMacAddress(), 1, "bpcorrect");
        LsBleManager.getInstance().pairingWithDevice(lsDeviceInfo, mPairCallback);
    }

    public void onStop() {
        LsBleManager.getInstance().stopSearch();
    }

    public void measeureReading(String mDeviceName, String mDeviceMac) {
        LsDeviceInfo lsDevice = new LsDeviceInfo();
        lsDevice.setDeviceName(mDeviceName);
        lsDevice.setMacAddress(mDeviceMac);
        LsBleManager.getInstance().connectDeviceWithAddress(lsDevice, mDataCallback);
    }

    public void stopMeaseureReading() {

    }

    public void disconnect() {

    }

    private SearchCallback mSearchCallback = new SearchCallback() {
        @Override
        public void onSearchResults(final LsDeviceInfo lsDevice) {
            foundDevice = lsDevice;
            mTranstekControllerCallback.onDeviceDetected_Transtek(foundDevice);
        }
    };

    // device pairing callback status
    private PairCallback mPairCallback = new PairCallback() {
        @Override
        public void onPairResults(final LsDeviceInfo lsDevice, final int status) {

            if (lsDevice != null && status == 0) {
                showLog("mPairCallback onPairResults:" + lsDevice.getMacAddress() + " " + status);
                mTranstekControllerCallback.onDeviceConnected_Transtek(lsDevice);
            } else {
                showLog("mPairCallback onPairResults: Pairing failed, please try again");
                // showPromptDialog("Prompt", "Pairing failed, please try again");
            }
        }

        @Override
        public void onDeviceOperationCommandUpdate(String macAddress,
                                                   OperationCommand cmd, Object obj) {
            showLog("mPairCallback onDeviceOperationCommandUpdate operation command update >> " + cmd + "; from device=" + macAddress);
            if (OperationCommand.CMD_DEVICE_ID == cmd) {
                //input device id
                String deviceId = macAddress.replace(":", "");//"ff028a0003e1";
                //register device's id for device
                LsBleManager.getInstance().registeringDeviceID(macAddress, deviceId, DeviceRegisterState.NORMAL_UNREGISTER);
            } else if (OperationCommand.CMD_PAIRED_CONFIRM == cmd) {
                PairedConfirmInfo confirmInfo = new PairedConfirmInfo(PairedConfirmState.PAIRING_SUCCESS);
                confirmInfo.setUserNumber(1);
                LsBleManager.getInstance().inputOperationCommand(macAddress, cmd, confirmInfo);
            }
        }

        @Override
        public void onDiscoverUserInfo(String macAddress, final List userList) {
            showLog("mPairCallback onDiscoverUserInfo: " + macAddress + " ");
            LsBleManager.getInstance().bindDeviceUser(foundDevice.getMacAddress(), 2, "bpcorrect");
        }
    };


    /**
     * Device measurement data synchronization callback object
     */
    private ReceiveDataCallback mDataCallback = new ReceiveDataCallback() {
        @Override
        public void onDeviceConnectStateChange(DeviceConnectState connectState, String broadcastId) {
            //Device Connection Status
            //updateDeviceConnectState(connectState);
            showLog("onDeviceConnectStateChange " + connectState.toString());
        }

        @Override
        public void onReceiveBloodPressureData(BloodPressureData bpData) {
            // updateNewDatMessage();
            showLog("onReceiveBloodPressureData " + bpData.toString());
        }
    };

    public void showLog(String log) {
        Log.d(TAG, log);
        //iHealthDevicesManager.getInstance().get
    }


    //------------------- get paired Device Info--------------------------------------
    public static List<LsDeviceInfo> getPairedDeviceInfo(Context appContext) {
        List<LsDeviceInfo> deviceList = null;
        String key = PairedDeviceInfo.class.getName();
        PairedDeviceInfo mPairedDeviceInfo = readPairedDeviceInfoFromFile(appContext, key);
        if (mPairedDeviceInfo != null && mPairedDeviceInfo.getPairedDeviceMap() != null) {
            deviceList = new ArrayList<LsDeviceInfo>();
            Map<String, LsDeviceInfo> deviceMap = mPairedDeviceInfo.getPairedDeviceMap();
            Iterator<Map.Entry<String, LsDeviceInfo>> it = deviceMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, LsDeviceInfo> entry = it.next();
                LsDeviceInfo lsDevice = entry.getValue();
                if (lsDevice != null) {
                    deviceList.add(lsDevice);
                }
            }
        }
        return deviceList;
    }

    private static PairedDeviceInfo readPairedDeviceInfoFromFile(Context appContext, String key) {

        PairedDeviceInfo pairedDeice = null;
        //从文件中读取已保存的设备对象信息
        SharedPreferences readPrefs = appContext.getSharedPreferences(
                appContext.getApplicationInfo().name, Context.MODE_PRIVATE);
        if (readPrefs != null) {
            //key=PairedDeviceInfo.class.getName()
            String jsonString = readPrefs.getString(key, null);
            Gson gson = new Gson();
            Log.e("read info=", jsonString + "");
            pairedDeice = gson.fromJson(jsonString, PairedDeviceInfo.class);
            return pairedDeice;
        } else return null;

    }

    //------------------------------- Measure Readings From Paired Device------------------------------

    private  DeviceSettingReceiver mSettingReceiver = new DeviceSettingReceiver();
    public void startScannForData(LsDeviceInfo currentDevice) {

        IntentFilter filter=new IntentFilter();
        filter.addAction("com.bluetooth.demo.setting.profiles.ACTION");
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mSettingReceiver, filter);

        List<Object> cacheDatas = DeviceDataUtils.getDeviceMeasurementData(currentDevice.getBroadcastID());
        if (cacheDatas != null && cacheDatas.size() > 0) {
            for (Object obj : cacheDatas) {
                showDeviceMeasuringData(obj);
            }
            DeviceDataUtils.clearCacheData();
        }


        //try to connected device
        connectDevice(currentDevice);
    }

     class DeviceSettingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent == null) {
                return;
            }
            /*if(!DeviceSettiingProfiles.SETTING_PROFIES_ACTION.equalsIgnoreCase(intent.getAction().toString())){
                return ;
            }*/
            String msg = intent.getStringExtra("errorMsg");
            showDeviceMeasuringData(msg);
        }
    }

    private void showDeviceMeasuringData(final Object obj) {
        if (obj == null) {
            return;
        }
        Log.d(TAG, formatStringValue(obj.toString()));
    }

    private static String formatStringValue(String message) {
        String tempMessage = message.replace("[", "\r\n");
        tempMessage = tempMessage.replace(",", ",\r\n");
        tempMessage = tempMessage.replace("]", "\r\n");
        return tempMessage;
    }

    private void connectDevice(LsDeviceInfo currentDevice) {

        if (currentDevice == null) {
            //DialogUtils.showPromptDialog(getActivity(),"Prompt", "No Devices!");
            return;
        }
        if (LsBleManager.getInstance().checkDeviceConnectState(currentDevice.getMacAddress()) == DeviceConnectState.CONNECTED_SUCCESS) {
            LsBleManager.getInstance().registerDataSyncCallback(mMeasureDataCallback);
            //connectingProgressBar.setVisibility(View.GONE);
            //stateTextView.setTextColor(Color.BLUE);
            //stateTextView.setText(getResources().getString(R.string.state_connected));
            return;
        }
        if (LsBleManager.getInstance().getLsBleManagerStatus() == ManagerStatus.DATA_RECEIVE) {
            return;
        }
        LsBleManager.getInstance().stopDataReceiveService();
        //clear measure device list
        LsBleManager.getInstance().setMeasureDevice(null);
        //add target measurement device
        LsBleManager.getInstance().addMeasureDevice(currentDevice);
        //set product user info on data syncing mode
        setProductUserInfoOnSyncingMode(currentDevice);
        //start data syncing service
        LsBleManager.getInstance().startDataReceiveService(mMeasureDataCallback);

        //update connect state
        updateDeviceConnectState(DeviceConnectState.CONNECTING);
    }

    /**
     * Device measurement data synchronization callback object
     */
    private ReceiveDataCallback mMeasureDataCallback = new ReceiveDataCallback() {
        @Override
        public void onDeviceConnectStateChange(DeviceConnectState connectState,
                                               String broadcastId) {
            //Device Connection Status
            updateDeviceConnectState(connectState);
        }

        @Override
        public void onReceiveWeightData_A3(WeightData_A3 wData) {
            LsBleManager.getInstance().setLogMessage("object data >> " + wData.toString());

            /**
             * Weight Scale Measurement Data
             * A3 product
             */
            showDeviceMeasuringData(wData);

        }

        @Override
        public void onReceiveUserInfo(WeightUserInfo proUserInfo) {
            /**
             * Weight Scale Product User Info
             * A3 product
             */
            showDeviceMeasuringData(proUserInfo);
        }

        @Override
        public void onReceivePedometerMeasureData(Object dataObject,
                                                  PacketProfile packetType, String sourceData) {
            int devicePower = DeviceDataUtils.getDevicePowerPercent(dataObject, packetType);
            //updateDevicePower(devicePower);
            //update new data message
            //updateNewDatMessage();
            /**
             * Pedoemter Measurement Data
             * Product：BonbonC、Mambo、MamboCall、MamboHR、Mambo Watch、MT/Gold、ZIVA
             */
            showDeviceMeasuringData(dataObject);
            LsBleManager.getInstance().setLogMessage("object data >> " + dataObject.toString());
        }

        @Override
        public void onReceiveWeightDta_A2(WeightData_A2 wData) {
            //updateNewDatMessage();
            /**
             * Weight Scale Measurement Data
             * A2 product
             */
            showDeviceMeasuringData(wData);
        }

        @Override
        public void onReceiveBloodPressureData(BloodPressureData bpData) {
            //updateNewDatMessage();
            /**
             * Blood Pressure Measurement Data
             * A2/A3 product
             */
            Log.d("onReceiveBloodPressureData"," "+bpData.toString());
            showDeviceMeasuringData(bpData);

            mTranstekControllerCallback.onReadingResult((int)bpData.getSystolic()+"",(int)bpData.getDiastolic()+"",(int)bpData.getPulseRate()+"");
        }

        @Override
        public void onReceivePedometerData(PedometerData pData) {
            /*if (pData.getBatteryPercent() > 0) {
                updateDevicePower(pData.getBatteryPercent());
            }*/
            //updateNewDatMessage();
            /**
             * Pedometer Measurement Data
             * A2 product
             */
            showDeviceMeasuringData(pData);
        }

        @Override
        public void onReceiveKitchenScaleData(KitchenScaleData kiScaleData) {
           // updateNewDatMessage();
            /**
             * Kitchen Scale Measurement Data
             */
            showDeviceMeasuringData(kiScaleData);
        }

        @Override
        public void onReceiveDeviceInfo(LsDeviceInfo lsDevice) {
            /*if (lsDevice == null || foundDevice == null) {
                return;
            }
            Log.e("LS-BLE", "Demo-Update Device Info:" + lsDevice.toString());
            //update and reset device's firmware version
            currentDevice.setFirmwareVersion(lsDevice.getFirmwareVersion());
            currentDevice.setHardwareVersion(lsDevice.getHardwareVersion());
            currentDevice.setModelNumber(lsDevice.getModelNumber());
            if (getActivity() != null) {
                //update and save device information
                AsyncTaskRunner runner = new AsyncTaskRunner(getActivity(), currentDevice);
                runner.execute();
                //show device information
                StringBuffer strBuffer = new StringBuffer();
                strBuffer.append("Device Version Information....,");
                strBuffer.append("ModelNumber:" + currentDevice.getModelNumber() + ",");
                strBuffer.append("firmwareVersion:" + currentDevice.getFirmwareVersion() + ",");
                strBuffer.append("hardwareVersion:" + currentDevice.getHardwareVersion() + ",");

                showDeviceMeasuringData(strBuffer);
            }*/
        }

        @Override
        public void onPedometerSportsModeNotify(final String macAddress, final SportNotify sportNotify) {
            showDeviceMeasuringData(sportNotify);
            /*if (mSportsNotify != null &&
                    mSportsNotify.getRequestType() == sportNotify.getRequestType()
                    && mSportsNotify.getSportsType() == sportNotify.getSportsType()) {
                Log.e("LS-BLE", "the same sport notify:" + sportNotify.toString() + "; local >> " + mSportsNotify.toString());
                return;
            }
            mSportsNotify = sportNotify;
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    //回复GPS的定位状态
                    DeviceSettiingProfiles.updatePhoneGpsStatus(macAddress, sportNotify, mSportNotifyConfirmListener);
                }
            });*/
        }

        @Override
        public void onReceiveBloodGlucoseData(BloodGlucoseData bgData) {
            //updateNewDatMessage();
            showDeviceMeasuringData(bgData);
        }


    };

    private static void setProductUserInfoOnSyncingMode(LsDeviceInfo lsDevice) {
        if (lsDevice == null) {
            return;
        }
        /**
         * in some old products, such as A2, A3,
         * you can change the device's settings info before syncing
         */
        if (DeviceTypeConstants.FAT_SCALE.equalsIgnoreCase(lsDevice.getDeviceType())
                || DeviceTypeConstants.WEIGHT_SCALE.equalsIgnoreCase(lsDevice.getDeviceType())) {
            /**
             * optional step,set product user info in syncing mode
             */
            WeightUserInfo weightUserInfo = new WeightUserInfo();
            weightUserInfo.setAge(30);                    //user age
            weightUserInfo.setHeight((float) 1.88);    //unit of measurement is m
            weightUserInfo.setGoalWeight(78);            //unit of measurement is kg
            weightUserInfo.setUnit(UnitType.UNIT_KG);    //measurement unit
            weightUserInfo.setSex(SexType.FEMALE);        //user gender
            weightUserInfo.setAthlete(true);            //it is an athlete
            weightUserInfo.setAthleteActivityLevel(3);    //athlete level
            weightUserInfo.setProductUserNumber(lsDevice.getDeviceUserNumber());
            weightUserInfo.setMacAddress(lsDevice.getMacAddress());
            weightUserInfo.setDeviceId(lsDevice.getDeviceId()); //set target device's id
            //calling interface
            LsBleManager.getInstance().setProductUserInfo(weightUserInfo);
        }
    }

    private void updateDeviceConnectState(final DeviceConnectState connectState) {
        if (DeviceConnectState.CONNECTED_SUCCESS == connectState) {
            /*connectingProgressBar.setVisibility(View.GONE);
            stateTextView.setTextColor(Color.BLUE);
            newDataTextView.setVisibility(View.VISIBLE);
            newDataTextView.setText(getResources().getString(R.string.str_no_data));
            newDataTextView.setTextColor(Color.GRAY);
            logTextView.setText("");
            stateTextView.setText(getResources().getString(R.string.state_connected));
            DialogUtils.showToastMessage(getActivity(), "Successful connection.");*/
            Log.d(TAG, "DeviceConnectState.CONNECTED_SUCCESS");
        } else {

            if (DeviceConnectState.DISCONNECTED == connectState) {
              /*  stateTextView.setTextColor(Color.RED);
                stateTextView.setText(getResources().getString(R.string.state_disconnect));
                DialogUtils.showToastMessage(getActivity(), stateTextView.getText().toString());*/
                Log.d(TAG, "DeviceConnectState.DISCONNECTED");
            } else {
                /*stateTextView.setTextColor(Color.GRAY);
                stateTextView.setText(getResources().getString(R.string.state_connecting));*/
                Log.d(TAG, "DeviceConnectState.CONNECTING");
            }
            //
            if (LsBleManager.getInstance().getLsBleManagerStatus() == ManagerStatus.DATA_RECEIVE) {
                /*connectingProgressBar.setVisibility(View.VISIBLE);
                newDataTextView.setVisibility(View.GONE);*/
                Log.d(TAG, "DeviceConnectState.DATA_RECEIVE");
            } else {
                /*newDataTextView.setVisibility(View.GONE);
                connectingProgressBar.setVisibility(View.GONE);
                stateTextView.setTextColor(Color.BLACK);
                stateTextView.setText(getResources().getString(R.string.state_unknown));*/
                Log.d(TAG, "DeviceConnectState.state_unknown");
            }
        }
    }

    public void stopScanningForData()
    {
        LsBleManager.getInstance().stopDataReceiveService();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mSettingReceiver);
    }
}

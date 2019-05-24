package com.protechgene.android.bpconnect.deviceManager.iHealthbp3l;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ihealth.communication.control.BtmControl;
import com.ihealth.communication.control.HsProfile;
import com.ihealth.communication.manager.DiscoveryTypeEnum;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IHealthDeviceController implements BP3L.BP3L_Callbacks {

    private static final String TAG = "IHealthDeviceController";

    public static final int HANDLER_SCAN = 101;
    public static final int HANDLER_CONNECTED = 102;
    public static final int HANDLER_DISCONNECT = 103;
    public static final int HANDLER_CONNECT_FAIL = 104;
    public static final int HANDLER_RECONNECT = 105;
    public static final int HANDLER_USER_STATUE = 106;

    private Context context;
    String mDeviceName ;
    private int callbackId;
    private List<DeviceCharacteristic> list_ScanDevices = new ArrayList<>();
    private iHealthCallback iHealthCallback ;
    private BP3L bp3L;


    public interface iHealthCallback
    {
        void onDeviceDetected_BP3L(DeviceCharacteristic deviceCharacteristic);
        void onScanCompleted_BP3L();
        void onDeviceConnected_BP3L(String deviceName,String deviceMac);
        void onConnectionError_BP3L(String messg);
        void onDeviceDisconnected_BP3L();
        void onReadingResult(String sys,String dia,String pulse);
    }

    public static boolean isAuthorizedToAccessDevice(Context context)
    {
        try {
            //Please apply for authorization on the server and download the. PEM file,
            //then put it in the assets folder and modify the corresponding name to call the following method
            //When ispass replays true, it indicates that the authentication has passed
            InputStream is = context.getAssets().open("com_protechgene_android_bpconnect_android.pem");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            boolean isPass = iHealthDevicesManager.getInstance().sdkAuthWithLicense(buffer);
            return isPass;
            /*if (isPass) {
                //mMainActivity.showDevicesFragment("", "");
                //mMainActivity.clearLogInfo();
                FragmentUtil.loadFragment(context, R.id.container_fragment,new PairNewDevicesFragment(),PairNewDevicesFragment.FRAGMENT_TAG,"PairNewDevicesFragmentTransition");
            } else {
                getBaseActivity().showSnakeBar("Not authorized to access this device.");
                //mMainActivity.showCertificationLogFragment("", "");
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public IHealthDeviceController(Context context) {
        this.context = context;
        mDeviceName = iHealthDevicesManager.TYPE_BP3L;
        callbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);
    }

    public IHealthDeviceController(Context context,iHealthCallback iHealthCallback) {
        this.context = context;
        mDeviceName = iHealthDevicesManager.TYPE_BP3L;
        callbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);
        this.iHealthCallback =iHealthCallback;
    }

    public void discoverDevice(iHealthCallback iHealthCallback)
    {
        if(iHealthCallback!=null)
            this.iHealthCallback =iHealthCallback;
        iHealthDevicesManager.getInstance().startDiscovery(getDiscoveryTypeEnum(mDeviceName));
    }

    public void ConnectDevice(String type, String mac, String userName) {
        boolean req;
        if (type.equals(iHealthDevicesManager.TYPE_FDIR_V3)) {
            req = iHealthDevicesManager.getInstance().connectTherm(userName, mac, type,
                    BtmControl.TEMPERATURE_UNIT_C, BtmControl.MEASURING_TARGET_BODY,
                    BtmControl.FUNCTION_TARGET_OFFLINE, 0, 1, 0);
        } else {
            req = iHealthDevicesManager.getInstance().connectDevice(userName, mac, type);
        }

        if (!req) {
            iHealthCallback.onConnectionError_BP3L("Haven’t permission to connect this device or the mac is not valid");
            showLog("Haven’t permission to connect this device or the mac is not valid");
        }
    }


    private DiscoveryTypeEnum getDiscoveryTypeEnum(String deviceName) {
        for (DiscoveryTypeEnum type : DiscoveryTypeEnum.values()) {
            if (deviceName.equals(type.name())) {
                return type;
            }
        }
        return null;
    }

    public void onStop() {
        iHealthDevicesManager.getInstance().unRegisterClientCallback(callbackId);
    }

    private iHealthDevicesCallback miHealthDevicesCallback = new iHealthDevicesCallback() {

        @Override
        public void onScanDevice(String mac, String deviceType, int rssi, Map manufactorData) {
            Log.i(TAG, "onScanDevice - mac:" + mac + " - deviceType:" + deviceType + " - rssi:" + rssi + " - manufactorData:" + manufactorData);
            Bundle bundle = new Bundle();
            bundle.putString("mac", mac);
            bundle.putString("type", deviceType);
            bundle.putInt("rssi", rssi);

            Message msg = new Message();
            msg.what = HANDLER_SCAN;
            msg.setData(bundle);
            myHandler.sendMessage(msg);

            //设备附加信息    无线mac后缀表
            //Device additional information wireless MAC suffix table
            if (manufactorData != null) {
                Log.d(TAG, "onScanDevice mac suffix = " + manufactorData.get(HsProfile.SCALE_WIFI_MAC_SUFFIX));
            }
        }

        @Override
        public void onDeviceConnectionStateChange(String mac, String deviceType, int status, int errorID, Map manufactorData) {
            Log.e(TAG, "mac:" + mac + " deviceType:" + deviceType + " status:" + status + " errorid:" + errorID + " -manufactorData:" + manufactorData);
            Bundle bundle = new Bundle();
            bundle.putString("mac", mac);
            bundle.putString("type", deviceType);
            Message msg = new Message();

            if (status == iHealthDevicesManager.DEVICE_STATE_CONNECTED) {
                msg.what = HANDLER_CONNECTED;
            } else if (status == iHealthDevicesManager.DEVICE_STATE_DISCONNECTED) {
                msg.what = HANDLER_DISCONNECT;
            } else if (status == iHealthDevicesManager.DEVICE_STATE_CONNECTIONFAIL) {
                msg.what = HANDLER_CONNECT_FAIL;
            } else if (status == iHealthDevicesManager.DEVICE_STATE_RECONNECTING) {
                msg.what = HANDLER_RECONNECT;
            }
            msg.setData(bundle);
            myHandler.sendMessage(msg);
        }

        /**
         * Callback indicating an error happened during discovery.
         *
         * @param reason A string for the reason why discovery failed.
         */
        @Override
        public void onScanError(String reason, long latency) {
            Log.e(TAG, reason);
            Log.e(TAG, "please wait for " + latency + " ms");
            /*if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
            }*/
        }

        @Override
        public void onScanFinish() {
            super.onScanFinish();
            iHealthCallback.onScanCompleted_BP3L();
           /* if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
            }*/

        }
    };

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_SCAN:
                    Bundle bundleScan = msg.getData();
                    String macScan = bundleScan.getString("mac");
                    String typeScan = bundleScan.getString("type");
                    int rssi = bundleScan.getInt("rssi");

                    DeviceCharacteristic device = new DeviceCharacteristic();
                    device.setDeviceName(typeScan);
                    device.setRssi(rssi);
                    device.setDeviceMac(macScan);
                    //ECG devices should deal with multiple USB connection duplicate display
                    if (mDeviceName.equals("ECGUSB")||mDeviceName.equals("ECG3USB")) {
                        for (int x = 0; x < list_ScanDevices.size(); x++) {
                            if (list_ScanDevices.get(x).getDeviceMac().equals(device.getDeviceMac())) {
                                list_ScanDevices.remove(x);
                                //mAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }

                    list_ScanDevices.add(device);
                    iHealthCallback.onDeviceDetected_BP3L(device);
                    /*mAdapter.setList(list_ScanDevices);
                    mAdapter.notifyDataSetChanged();
                    mLoadingDialog.dismiss();*/

                    showLog("scan device : type:" + typeScan + " maconDeviceDetected:" + macScan + " rssi:" + rssi);
                    break;

                case HANDLER_CONNECTED:
                    Bundle bundleConnect = msg.getData();
                    String macConnect = bundleConnect.getString("mac");
                    String typeConnect = bundleConnect.getString("type");
                    iHealthDevicesManager.getInstance().stopDiscovery();
                    iHealthCallback.onDeviceConnected_BP3L(typeConnect,macConnect);
                    //mMainActivity.showFunctionActivity(macConnect, typeConnect);
                    //mLoadingDialog.dismiss();
                    //ToastUtils.showToast(mContext, mContext.getString(R.string.connect_main_tip_connected));
                    showLog("connected device : type:" + typeConnect + " mac:" + macConnect);
                    break;
                case HANDLER_DISCONNECT:
                    iHealthCallback.onDeviceDisconnected_BP3L();
                    //mLoadingDialog.dismiss();
                    //ToastUtils.showToast(mContext, mContext.getString(R.string.connect_main_tip_disconnect));
                    showLog("The device has been disconnected");
                    break;
                case HANDLER_CONNECT_FAIL:
                    //ToastUtils.showToast(mContext, mContext.getString(R.string.connect_main_tip_connect_fail));
                    break;
                case HANDLER_RECONNECT:
                    //mLoadingDialog.show();
                   //ToastUtils.showToast(mContext, mContext.getString(R.string.connect_main_tip_reconnect));
                    break;
                case HANDLER_USER_STATUE:
                    break;

                default:
                    break;
            }
        }
    };

    public void measeureReading(String mDeviceName,String mDeviceMac)
    {
        if(bp3L==null)
            bp3L = new BP3L(context);
        bp3L.initializeDevice(mDeviceMac,mDeviceName,this);
        bp3L.measeureReading();
    }

    @Override
    public void onReadingResult(String sys, String dia, String pulse) {
        iHealthCallback.onReadingResult(sys,dia,pulse);
    }

    public void stopMeaseureReading()
    {
        if(bp3L!=null)
            bp3L.stopMeaseureReading();
    }

    public void disconnect()
    {
        if(bp3L!=null)
            bp3L.disconnect();
    }

    public void showLog(String log) {
        Log.d(TAG, log);
        //iHealthDevicesManager.getInstance().get
    }
}

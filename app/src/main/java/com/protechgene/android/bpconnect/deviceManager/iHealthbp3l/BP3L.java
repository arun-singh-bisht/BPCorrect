package com.protechgene.android.bpconnect.deviceManager.iHealthbp3l;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.ihealth.communication.control.Bp3lControl;
import com.ihealth.communication.control.BpProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * bp3l activity
 */
public class BP3L  {

    /** Handle Message What Code*/
    public static final int HANDLER_MESSAGE = 101;

    private Context mContext;
    private static final String TAG = "BP3L";
    private Bp3lControl mBp3lControl;
    private int mClientCallbackId;

    private String mDeviceMac;
    private String mDeviceName;
    private BP3L_Callbacks bp3L_callbacks;

    public BP3L(Context mContext) {
        this.mContext = mContext;
    }

    public interface BP3L_Callbacks
    {
        void onReadingResult(String sys,String dia,String pulse);
        void onError(String msg);
    }


    public void initializeDevice( String mDeviceMac,String mDeviceName,BP3L_Callbacks bp3L_callbacks) {

        this.mDeviceMac = mDeviceMac;
        this.mDeviceName = mDeviceName;
        this.bp3L_callbacks = bp3L_callbacks;

        /* register ihealthDevicesCallback id */
        mClientCallbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);
        /* Limited wants to receive notification specified device */
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(mClientCallbackId, iHealthDevicesManager.TYPE_BP3L);
        /* Get bp3 controller */
        mBp3lControl = iHealthDevicesManager.getInstance().getBp3lControl(mDeviceMac);

    }

    private iHealthDevicesCallback miHealthDevicesCallback = new iHealthDevicesCallback() {

        @Override
        public void onDeviceConnectionStateChange(String mac, String deviceType, int status, int errorID) {
            showLog("mac: " + mac);
            showLog("deviceType: " + deviceType);
            showLog("status: " + status);
            if (status == iHealthDevicesManager.DEVICE_STATE_DISCONNECTED) {
                showLog("The device has been disconnected.");
                //ToastUtils.showToast(mContext, mContext.getString(R.string.connect_main_tip_disconnect));
                //finish();
            }
        }

        @Override
        public void onUserStatus(String username, int userStatus) {
            showLog("username: " + username);
            showLog("userState: " + userStatus);
        }

        @Override
        public void onDeviceNotify(String mac, String deviceType, String action, String message) {
            showLog( "mac: " + mac);
            showLog( "deviceType: " + deviceType);
            showLog( "action: " + action);
            showLog("message: " + message);

            if (BpProfile.ACTION_BATTERY_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String battery = info.getString(BpProfile.BATTERY_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "ACTION_BATTERY_BP: battery: " + battery;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else if (BpProfile.ACTION_DISENABLE_OFFLINE_BP.equals(action)) {
                Log.i(TAG, "disable operation is success");

            } else if (BpProfile.ACTION_ENABLE_OFFLINE_BP.equals(action)) {
                Log.i(TAG, "enable operation is success");

            } else if (BpProfile.ACTION_ERROR_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String num = info.getString(BpProfile.ERROR_NUM_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "ACTION_ERROR_BP: error num: " + num;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bp3L_callbacks.onError("Error! Not found proper reading.\nPlease try again.");

            } else if (BpProfile.ACTION_HISTORICAL_DATA_BP.equals(action)) {
                String str = "";
                try {
                    JSONObject info = new JSONObject(message);
                    if (info.has(BpProfile.HISTORICAL_DATA_BP)) {
                        JSONArray array = info.getJSONArray(BpProfile.HISTORICAL_DATA_BP);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String date = obj.getString(BpProfile.MEASUREMENT_DATE_BP);
                            String hightPressure = obj.getString(BpProfile.HIGH_BLOOD_PRESSURE_BP);
                            String lowPressure = obj.getString(BpProfile.LOW_BLOOD_PRESSURE_BP);
                            String pulseWave = obj.getString(BpProfile.PULSE_BP);
                            String ahr = obj.getString(BpProfile.MEASUREMENT_AHR_BP);
                            String hsd = obj.getString(BpProfile.MEASUREMENT_HSD_BP);
                            str = "date:" + date
                                    + "hightPressure:" + hightPressure + "\n"
                                    + "lowPressure:" + lowPressure + "\n"
                                    + "pulseWave" + pulseWave + "\n"
                                    + "ahr:" + ahr + "\n"
                                    + "hsd:" + hsd + "\n";
                        }
                    }
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "ACTION_HISTORICAL_DATA_BP:"+str;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_HISTORICAL_NUM_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String num = info.getString(BpProfile.HISTORICAL_NUM_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "ACTION_HISTORICAL_NUM_BP: num: " + num;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_IS_ENABLE_OFFLINE.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String isEnableoffline = info.getString(BpProfile.IS_ENABLE_OFFLINE);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "ACTION_IS_ENABLE_OFFLINE: isEnableoffline: " + isEnableoffline;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_ONLINE_PRESSURE_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String pressure = info.getString(BpProfile.BLOOD_PRESSURE_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "ACTION_ONLINE_PRESSURE_BP: pressure: " + pressure;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_ONLINE_PULSEWAVE_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String pressure = info.getString(BpProfile.BLOOD_PRESSURE_BP);
                    String wave = info.getString(BpProfile.PULSEWAVE_BP);
                    String heartbeat = info.getString(BpProfile.FLAG_HEARTBEAT_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "ACTION_ONLINE_PULSEWAVE_BP: pressure:" + pressure + "\n"
                            + "wave: " + wave + "\n"
                            + " - heartbeat:" + heartbeat;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_ONLINE_RESULT_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String highPressure = info.getString(BpProfile.HIGH_BLOOD_PRESSURE_BP);
                    String lowPressure = info.getString(BpProfile.LOW_BLOOD_PRESSURE_BP);
                    String ahr = info.getString(BpProfile.MEASUREMENT_AHR_BP);
                    String pulse = info.getString(BpProfile.PULSE_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "ACTION_ONLINE_RESULT_BP: highPressure: " + highPressure
                            + "lowPressure: " + lowPressure
                            + "ahr: " + ahr
                            + "pulse: " + pulse;
                    myHandler.sendMessage(msg);
                    bp3L_callbacks.onReadingResult(highPressure,lowPressure,pulse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_ZOREING_BP.equals(action)) {
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
                msg.obj = "ACTION_ZOREING_BP: zoreing";
                myHandler.sendMessage(msg);

            } else if (BpProfile.ACTION_ZOREOVER_BP.equals(action)) {
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
                msg.obj = "ACTION_ZOREOVER_BP: zoreover";
                myHandler.sendMessage(msg);

            }
        }
    };

    public void measeureReading()
    {
        mBp3lControl.startMeasure();
        showLog("startMeasure()");
    }

    public void stopMeaseureReading()
    {
        mBp3lControl.interruptMeasure();
        showLog("interruptMeasure()");
    }

    public void getBatteryStatus()
    {
        mBp3lControl.getBattery();
        showLog("getBattery()");
    }

    public void disconnect()
    {
        mBp3lControl.disconnect();
        showLog("disconnect()");
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MESSAGE:
                    showLog((String) msg.obj);
                    System.out.println("Hello my response message is"+ msg.obj);
                    break;
            }
            super.handleMessage(msg);
        }
    };


    protected void onDestroy() {
        mBp3lControl.disconnect();
        iHealthDevicesManager.getInstance().unRegisterClientCallback(mClientCallbackId);
        mContext = null;
    }

    public void showLog(String log) {
        Log.d(TAG, log);
    }
}

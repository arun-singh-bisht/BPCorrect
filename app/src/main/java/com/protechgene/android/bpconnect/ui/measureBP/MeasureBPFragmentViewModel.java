package com.protechgene.android.bpconnect.ui.measureBP;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lifesense.ble.LsBleManager;
import com.lifesense.ble.ReceiveDataCallback;
import com.lifesense.ble.bean.BloodPressureData;
import com.lifesense.ble.bean.LsDeviceInfo;
import com.lifesense.ble.bean.constant.DeviceConnectState;
import com.lifesense.ble.bean.constant.ManagerStatus;
import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.DateUtils;
import com.protechgene.android.bpconnect.Utils.GpsUtils;
import com.protechgene.android.bpconnect.Utils.MathUtil;
import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.ble.ADGattService;
import com.protechgene.android.bpconnect.data.ble.ADGattUUID;
import com.protechgene.android.bpconnect.data.ble.BleReceivedService;
import com.protechgene.android.bpconnect.data.ble.Lifetrack_infobean;
import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;
import com.protechgene.android.bpconnect.data.local.models.ProfileDetailModel;
import com.protechgene.android.bpconnect.data.remote.responseModels.AddBPReading.AddBpReadingResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.profile.ProfileResponse;
import com.protechgene.android.bpconnect.deviceManager.Transtek.TranstekDeviceController;
import com.protechgene.android.bpconnect.deviceManager.iHealthbp3l.DeviceCharacteristic;
import com.protechgene.android.bpconnect.deviceManager.iHealthbp3l.IHealthDeviceController;
import com.protechgene.android.bpconnect.deviceManager.iHealthbp3l.IHealthDeviceController.iHealthCallback;
import com.protechgene.android.bpconnect.ui.ApplicationBPConnect;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.protechgene.android.bpconnect.ui.ApplicationBPConnect.PROTOCOL_EVENING_MAXIMUM_TIME;
import static com.protechgene.android.bpconnect.ui.ApplicationBPConnect.PROTOCOL_EVENING_MINIMUM_TIME;
import static com.protechgene.android.bpconnect.ui.ApplicationBPConnect.PROTOCOL_MORNING_MAXIMUM_TIME;
import static com.protechgene.android.bpconnect.ui.ApplicationBPConnect.PROTOCOL_MORNING_MINIMUM_TIME;
import static com.protechgene.android.bpconnect.ui.ApplicationBPConnect.readingUploadToServer;


public class MeasureBPFragmentViewModel extends BaseViewModel<MeasureBPFragmentNavigator> implements iHealthCallback , TranstekDeviceController.TranstekControllerCallback {

    private String TAG = "MeasureBPFragmentViewModel";

    public static final int MEASU_DATA_TYPE_UNKNOW = -1;
    public static final int MEASU_DATA_TYPE_AM = 0;
    public static final int MEASU_DATA_TYPE_BP = 1;
    public static final int MEASU_DATA_TYPE_WS = 2;
    public static final int MEASU_DATA_TYPE_TH = 3;

    public static final String ACTION_AM_DATA_UPDATE = "com.andmedical.action_am_data_update";
    public static final String ACTION_BP_DATA_UPDATE = "com.andmedical.action_bp_data_update";
    public static final String ACTION_WS_DATA_UPDATE = "com.andmedical.action_ws_data_update";
    public static final String ACTION_TH_DATA_UPDATE = "com.andmedical.action_tm_data_update";

    public static IntentFilter MeasuDataUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_AM_DATA_UPDATE);
        intentFilter.addAction(ACTION_BP_DATA_UPDATE);
        intentFilter.addAction(ACTION_WS_DATA_UPDATE);
        intentFilter.addAction(ACTION_TH_DATA_UPDATE);
        return intentFilter;
    }

    private int[] dataTypes = {
            MEASU_DATA_TYPE_AM,
            MEASU_DATA_TYPE_BP,
            MEASU_DATA_TYPE_WS,
            MEASU_DATA_TYPE_TH,
    };

    public static String DEFAULT_WEIGHT_SCALE_UNITS = "lbs";
    public static final int REQUEST_ENABLE_BLUETOOTH = 1000;

    public static final String BP_DEVICE_MODEL_AND_UA_651BLE = "AND_UA_651BLE";
    public static final String BP_DEVICE_MODEL_IHEALTH_BP3L = "IHEALTH_BP3L";
    public static final String BP_DEVICE_MODEL_TRANSTREK_1491B = "TRANSTEK_1491B";

    private boolean mIsBindBleReceivedServivce = false;
    private boolean mIsCheckBleetoothEnabled = false;

    private boolean isScanning = false;
    private boolean shouldStartConnectDevice = false;
    private boolean mIsBleReceiver = false;
    private boolean mIsSendCancel = false;
    private boolean isTryScanning;

    private long setDateTimeDelay = Long.MIN_VALUE;
    private long indicationDelay = Long.MIN_VALUE;
    private Handler uiThreadHandler = new Handler();

    private ArrayList<BluetoothDevice> deviceList;
    ArrayList<String> pairedDeviceList = new ArrayList<String>(); //ACGS-10

    private boolean isReadingTaken = false;
    private boolean measureReadingForProtocol;
    private String protocolId;
    private Context mContext;
    private int total_protocolReadingTaken;

    private TranstekDeviceController transtekDeviceController;

    public MeasureBPFragmentViewModel(Repository repository) {
        super(repository);
    }


    public void connectToDevice(Context context, String bpDeviceModelName,boolean measureReadingForProtocol,String protocolId,int total_protocolReadingTaken) {

        this.measureReadingForProtocol = measureReadingForProtocol;
        this.total_protocolReadingTaken = total_protocolReadingTaken;
        this.protocolId = protocolId;

        mContext = context;
        // 1. Enable Bluetooth in background
        if (!getBluetoothManager().getAdapter().isEnabled()) {
            getBluetoothManager().getAdapter().enable();
            getNavigator().turningOnBluetooth();
            return;
        }

        // 2. Check if GPS is Active, If Not ask user to turn it on ,receive user action in onActivityResult
        GpsUtils gpsUtils = new GpsUtils((Activity) mContext);
        gpsUtils.turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {

                if (isGPSEnable) {

                    if (bpDeviceModelName.equalsIgnoreCase(BP_DEVICE_MODEL_AND_UA_651BLE)) {
                        deviceList = new ArrayList<BluetoothDevice>();
                        // 3. Check Device Paired Status
                        boolean isBpDevicePaired = isDevicePaired();
                        if (isBpDevicePaired) {
                            mContext.registerReceiver(mMeasudataUpdateReceiver, MeasuDataUpdateIntentFilter());
                            getNavigator().AND_DevicePairedStatus(true);
                            doStartService();
                        } else {
                            getNavigator().AND_DevicePairedStatus(false);
                        }
                    } else if (bpDeviceModelName.equalsIgnoreCase(BP_DEVICE_MODEL_IHEALTH_BP3L)) {
                        boolean authorizeForBP3L = isAuthorizeForBP3L(mContext);
                        getNavigator().onScanningStarted_iHealthBP3L(authorizeForBP3L);
                        if (authorizeForBP3L) {
                            scanBP3LDevice();
                        }
                    } else if (bpDeviceModelName.equalsIgnoreCase(BP_DEVICE_MODEL_TRANSTREK_1491B)) {
                        // 3. Check Device Paired Status
                        List<LsDeviceInfo> pairedDeviceInfo = TranstekDeviceController.getPairedDeviceInfo(context);
                        if (pairedDeviceInfo != null && pairedDeviceInfo.size() > 0) {

                            getNavigator().AND_DevicePairedStatus(true);
                            isReadingTaken = false;
                            transtekDeviceController = new TranstekDeviceController(context,MeasureBPFragmentViewModel.this);
                            transtekDeviceController.startScannForData(pairedDeviceInfo.get(0));
                        } else {
                            getNavigator().AND_DevicePairedStatus(false);
                        }

                    }

                }
            }
        });

    }

    protected void onResume(boolean measureReadingForProtocol, String protocolId) {

        this.measureReadingForProtocol = measureReadingForProtocol;
        this.protocolId = protocolId;

        isReadingTaken = false;

        //isDevicePaired();

        if (mIsSendCancel) {
            mIsSendCancel = false;
        }

        BluetoothManager bluetoothManager = getBluetoothManager();
        if (bluetoothManager != null) {
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter != null) {
                if (!bluetoothAdapter.isEnabled()) {
                    if (!mIsCheckBleetoothEnabled) {
                        mIsCheckBleetoothEnabled = true;
                        //getNavigator().tunrOnBluetooth();
                        return;
                    }
                } else {
                    doBindBleReceivedService();
                }
            }
        }
        mIsCheckBleetoothEnabled = false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == ((Activity) mContext).RESULT_OK) {
                doBindBleReceivedService();
            }
        }
    }

    protected void onPause() {
        doStopLeScan();
        doUnbindBleReceivedService();

        if (!mIsSendCancel) {
            mIsSendCancel = true;
        }
    }

    Runnable disableIndicationRunnable = new Runnable() {
        @Override
        public void run() {
            getNavigator().setIndicatorMessage(mContext.getResources().getString(R.string.indicator_complete_receive));
            BluetoothGatt gatt = BleReceivedService.getGatt();
            if (BleReceivedService.getInstance() != null) {
                boolean writeResult = BleReceivedService.getInstance().setIndication(gatt, false);
                if (writeResult == false) {
                    getNavigator().dismissIndicator();
                }
                //Add disconnect from smartphone.
                if (gatt != null) {
                    gatt.disconnect();
                }

                getNavigator().dismissIndicator();
            }
        }
    };

    private final BroadcastReceiver mMeasudataUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_AM_DATA_UPDATE.equals(action)) {
                //refreshActivityMonitorLayout();
            } else if (ACTION_BP_DATA_UPDATE.equals(action)) {
                refreshBloodPressureLayout();
            } else if (ACTION_WS_DATA_UPDATE.equals(action)) {
                //refreshWeightScaleLayout();
            } else if (ACTION_TH_DATA_UPDATE.equals(action)) {
                // refreshThermometerLayout();
            }
        }
    };


    private final BroadcastReceiver bleServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getExtras().getString(BleReceivedService.EXTRA_TYPE);

            if (BleReceivedService.TYPE_GATT_CONNECTED.equals(type)) {
                getNavigator().showIndicator(mContext.getResources().getString(R.string.indicator_start_receive));
                //getNavigator().connectedToDevice();

                BleReceivedService.getGatt().discoverServices();
                setDateTimeDelay = Long.MIN_VALUE;
                indicationDelay = Long.MIN_VALUE;
            } else if (BleReceivedService.TYPE_GATT_DISCONNECTED.equals(type)) {
                getNavigator().dismissIndicator();
                //getNavigator().disconnectedFromDevice();
                if (shouldStartConnectDevice) {
                    BleReceivedService.getInstance().disconnectDevice();
                    uiThreadHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            shouldStartConnectDevice = false;
                            if (!isScanning) {
                                doStartLeScan();
                            }
                        }
                    }, 80L);
                }
            } else if (BleReceivedService.TYPE_GATT_ERROR.equals(type)) {
                int status = intent.getExtras().getInt(BleReceivedService.EXTRA_STATUS);
                if (status == 19) {
                    return;
                }
                if (shouldStartConnectDevice) {
                    if (BleReceivedService.getInstance() != null) {
                        if (!BleReceivedService.getInstance().isConnectedDevice()) {
                            shouldStartConnectDevice = false;
                            getNavigator().dismissIndicator();
                            //getNavigator().disconnectedFromDevice();
                            doStartLeScan();
                        } else {
                            BluetoothGatt gatt = BleReceivedService.getGatt();
                            if (gatt != null) {
                                gatt.connect();
                            }
                        }
                    }
                } else {
                    getNavigator().dismissIndicator();
                    //getNavigator().disconnectedFromDevice();
                }
            } else {
                if (BleReceivedService.TYPE_GATT_SERVICES_DISCOVERED.equals(type)) {
                    if (shouldStartConnectDevice) {
                        if (BleReceivedService.getInstance() != null) {

                            String device_name = intent.getExtras().getString(BleReceivedService.EXTRA_DEVICE_NAME);
                            if (device_name.contains("UW-302BLE")) {
                                BleReceivedService.getInstance().setUW302Notfication();
                            } else {
                                uiThreadHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        BleReceivedService.getInstance().requestReadFirmRevision();
                                    }
                                }, 500L);
                            }

                        }
                    }
                } else if (BleReceivedService.TYPE_CHARACTERISTIC_READ.equals(type)) {
                    if (shouldStartConnectDevice) {
                        byte[] firmRevisionBytes = intent.getByteArrayExtra(BleReceivedService.EXTRA_VALUE);
                        String firmRevision = null;
                        if (firmRevisionBytes == null) {
                            return;
                        }
                        firmRevision = new String(firmRevisionBytes);
                        if (firmRevision == null || firmRevision.isEmpty()) {
                            return;
                        }
                        String[] firmRevisionArray = mContext.getResources().getStringArray(R.array.firm_revision_group1);
                        boolean isGroup1 = false;
                        for (String revision : firmRevisionArray) {
                            if (revision.contains(firmRevision)) {
                                isGroup1 = true;
                                break;
                            }
                        }

                        String[] cesFirmRevisionCesArray = mContext.getResources().getStringArray(R.array.firm_revision_ces);
                        boolean isCesGroup = false;
                        for (String cesRevision : cesFirmRevisionCesArray) {
                            if (cesRevision.contains(firmRevision)) {
                                isCesGroup = true;
                                break;
                            }
                        }

                        if (isGroup1) {
                            setDateTimeDelay = 40L;
                            indicationDelay = 40L;
                        } else if (isCesGroup) {
                            setDateTimeDelay = 0L;
                            indicationDelay = 0L;
                        } else {
                            setDateTimeDelay = 100L;
                            indicationDelay = 100L;
                        }
                        uiThreadHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                BluetoothGatt gatt = BleReceivedService.getGatt();
                                boolean settingResult;
                                if (gatt != null) {
                                    String deviceName = gatt.getDevice().getName();
                                    settingResult = BleReceivedService.getInstance().setupDateTime(gatt);
                                    if (!settingResult) {
                                        getNavigator().dismissIndicator();
                                    }
                                } else {
                                    getNavigator().dismissIndicator();
                                }
                            }
                        }, setDateTimeDelay);
                    }
                } else if (BleReceivedService.TYPE_CHARACTERISTIC_WRITE.equals(type)) {
                    String serviceUuidString = intent.getStringExtra(BleReceivedService.EXTRA_SERVICE_UUID);
                    String characteristicUuidString = intent.getExtras().getString(BleReceivedService.EXTRA_CHARACTERISTIC_UUID);
                    if (serviceUuidString.equals(ADGattUUID.CurrentTimeService.toString())
                            || characteristicUuidString.equals(ADGattUUID.DateTime.toString())) {
                        if (shouldStartConnectDevice) {
                            uiThreadHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    BluetoothGatt gatt = BleReceivedService.getGatt();
                                    boolean writeResult = BleReceivedService.getInstance().setIndication(gatt, true);
                                    if (writeResult == false) {
                                        getNavigator().dismissIndicator();
                                    }
                                }
                            }, indicationDelay);
                        }
                    }
                } else if (BleReceivedService.TYPE_INDICATION_VALUE.equals(type)) {
                    getNavigator().setIndicatorMessage(mContext.getResources().getString(R.string.indicator_during_receive));
                    Bundle bundle = intent.getBundleExtra(BleReceivedService.EXTRA_VALUE);
                    String uuidString = intent.getExtras().getString(BleReceivedService.EXTRA_CHARACTERISTIC_UUID);
                    receivedData(uuidString, bundle);
                    uiThreadHandler.removeCallbacks(disableIndicationRunnable);
                    //uiThreadHandler.postDelayed(disableIndicationRunnable, 4000L);
                } else if (BleReceivedService.TYPE_DESCRIPTOR_WRITE.equals(type)) {
                    //For now do this only for the UW-302
                    String device_name = intent.getExtras().getString(BleReceivedService.EXTRA_DEVICE_NAME);
                    if (device_name.contains("UW-302BLE")) {

                    }

                }
            }
        }
    };


    public boolean isBluetoothEnabled() {
        return getBluetoothManager().getAdapter().isEnabled();
        /*if(!getBluetoothManager().getAdapter().isEnabled())
        {
            getBluetoothManager().getAdapter().enable();
            getNavigator().turningOnBluetooth();
            return;
        }*/
    }

    public void enableBluetooth() {
        getBluetoothManager().getAdapter().enable();
    }

    boolean isDevicePaired() {
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        Set<BluetoothDevice> pairingDevices = null;
        pairingDevices = bluetoothManager.getAdapter().getBondedDevices();
        pairedDeviceList.clear();//Clear the list , then initialize the list
        if (pairingDevices == null) {
            return false;
        } else {
            for (BluetoothDevice bdevice : pairingDevices) {
                String name = bdevice.getName();
                if (name.contains("A&D")) {
                    //Check if its 651 or 352
                    if (name.contains("651")) {
                        //This is BP
                        if (!pairingDevices.contains("bpDevice")) {
                            pairedDeviceList.add("bpDevice");
                        }

                    } else if (name.contains("352")) {
                        //This is weight scale
                        if (!pairingDevices.contains("wsDevice")) {
                            pairedDeviceList.add("wsDevice");
                        }

                    }
                } else if (name.contains("UW-302")) {
                    //This is activity tracker
                    if (!pairingDevices.contains("activityDevice")) {
                        pairedDeviceList.add("activityDevice");
                    }
                }
            }

            if (pairedDeviceList.contains("bpDevice"))
                return true;
            else
                return false;
        }
    }

    //------- Background Service -------------------------------------------------------------

    private void doStartService() {
        Intent intent1 = new Intent(mContext, BleReceivedService.class);
        mContext.startService(intent1);
        if (!mIsBleReceiver) {
            IntentFilter filter = new IntentFilter(BleReceivedService.ACTION_BLE_SERVICE);
            mContext.registerReceiver(bleServiceReceiver, filter);
            mIsBleReceiver = true;
        }

    }

    private void doStopService() {
        try {
            if (mIsBleReceiver) {
                mContext.unregisterReceiver(bleServiceReceiver);
                mIsBleReceiver = false;
            }
            Intent intent1 = new Intent(mContext, BleReceivedService.class);
            mContext.stopService(intent1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //--------- Bind Service ---------------------------------------------------------

    private void doBindBleReceivedService() {
        if (!mIsBindBleReceivedServivce) {
            mContext.bindService(new Intent(mContext,
                    BleReceivedService.class), mBleReceivedServiceConnection, Context.BIND_AUTO_CREATE);
            mIsBindBleReceivedServivce = true;
        }
    }

    private void doUnbindBleReceivedService() {
        if (mIsBindBleReceivedServivce) {
            mContext.unbindService(mBleReceivedServiceConnection);
            mIsBindBleReceivedServivce = false;
        }
    }

    private ServiceConnection mBleReceivedServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            doStartLeScan();
        }
    };


    //---------------------------------  Scan ------------------------------------------

    private void doStartLeScan() {
        isTryScanning = true;
        doTryLeScan();
    }

    private void doTryLeScan() {
        if (!isTryScanning) {
            return;
        }
        if (!isScanning) {
            startScan();
        }
        isTryScanning = false;
    }

    private void startScan() {
        if (shouldStartConnectDevice) {
            return;
        }
        if (BleReceivedService.getInstance() != null) {
            if (BleReceivedService.getInstance().isConnectedDevice()) {
                BleReceivedService.getInstance().disconnectDevice();
            }
            isScanning = true;
            ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  // For UW-302BLE
            deviceList.clear();
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boolean result = BleReceivedService.getInstance().getBluetoothManager().getAdapter().startLeScan(mLeScanCallback);
                }
            });
        }
    }

    private void doStopLeScan() {
        if (isScanning) {
            stopScan();
        }
    }

    private void stopScan() {
        if (BleReceivedService.getInstance() != null) {
            isScanning = false;
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (BleReceivedService.getInstance().getBluetoothManager().getAdapter() != null) {
                        BleReceivedService.getInstance().getBluetoothManager().getAdapter().stopLeScan(mLeScanCallback);
                    }
                }
            });
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (!isScanning) {
                return;
            }
            if (device.getName() != null) {
                if (isAbleToConnectDevice(device, scanRecord) && !shouldStartConnectDevice) {

                    shouldStartConnectDevice = true;
                    if (device.getName() != null) {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                doStopLeScan();
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                BleReceivedService.getInstance().connectDevice(device);
                            }
                        });
                    }

                } else if (device.getName().contains("UW-302")) {

                    /*if (isAbleToConnectDeviceUW(device, scanRecord) && !shouldStartConnectDevice) {

                        final ImageView syncImage = (ImageView) findViewById(R.id.dashboard_icon_display);
                        syncImage.setVisibility(View.VISIBLE);
                        syncImage.setImageResource(R.drawable.syncpurple);
                        syncImage.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        shouldStartConnectDevice = true;
                                        doStopLeScan();
                                        try {
                                            Thread.sleep(200);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        BleReceivedService.getInstance().connectDevice(device);

                                        // UW-302BLE sync may takes long time, so disable Android sleep.
                                        // When start again, reset sleep disabling.
                                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                                        syncImage.setImageResource(R.drawable.dashboard_walk_icon);
                                    }
                                });
                            }
                        });

                    }*/
                }
            }
        }
    };

    private boolean isAbleToConnectDevice(BluetoothDevice device, byte[] scanRecord) {
        if (BleReceivedService.getInstance().isConnectedDevice()) {
            return false;
        }
        BluetoothAdapter bluetoothAdapter = BleReceivedService.getInstance().getBluetoothManager().getAdapter();
        if (bluetoothAdapter != null) {

            Set<BluetoothDevice> pairingDevices = bluetoothAdapter.getBondedDevices();

            if (device.getName() != null) {
                return pairingDevices.contains(device) && device.getName().contains("A&D");
            }
        }
        return false;
    }

    private void refreshBloodPressureLayout() {
       /* MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
        Lifetrack_infobean data = measuDataManager.getCurrentDispData(MeasuDataManager.MEASU_DATA_TYPE_BP);
        boolean isExistData = (data != null);
        if (isExistData) {
            bloodpressure.setData(data);
        }
        bloodpressure.setHide(!isExistData);*/
    }

    private void receivedData(String characteristicUuidString, Bundle bundle) {

        if (ADGattUUID.WeightScaleMeasurement.toString().equals(characteristicUuidString) ||
                ADGattUUID.AndCustomWeightScaleMeasurement.toString().equals(characteristicUuidString)) {

            double weight = bundle.getDouble(ADGattService.KEY_WEIGHT);
            String units = bundle.getString(ADGattService.KEY_UNIT, DEFAULT_WEIGHT_SCALE_UNITS);

            int year = bundle.getInt(ADGattService.KEY_YEAR);
            int month = bundle.getInt(ADGattService.KEY_MONTH);
            int day = bundle.getInt(ADGattService.KEY_DAY);
            int hours = bundle.getInt(ADGattService.KEY_HOURS);
            int minutes = bundle.getInt(ADGattService.KEY_MINUTES);
            int seconds = bundle.getInt(ADGattService.KEY_SECONDS);

            String weightString = String.format(Locale.getDefault(), "%.1f", weight);
            String finaldate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
            String finaltime = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
            String finalTimeStamp = String.format(Locale.getDefault(), "%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hours, minutes, seconds);

            Lifetrack_infobean infoBeanObj = new Lifetrack_infobean();
            infoBeanObj.setWeight(weightString);
            infoBeanObj.setDate(finaldate);
            infoBeanObj.setTime(finaltime);
            infoBeanObj.setWeightUnit(units);

            //ADSharedPreferences.putString(ADSharedPreferences.KEY_WEIGHT_SCALE_UNITS, units);

            infoBeanObj.setIsSynced("no");
            long dateValue = convertDateintoMs(finalTimeStamp);
            infoBeanObj.setDateTimeStamp(String.valueOf(dateValue));

            String weightDeviceId = "9DEA020D-1795-3B89-D184-DE7CD609FAD0";

            infoBeanObj.setDeviceId(weightDeviceId);

            //Insert in DB and Sync

            /*final ArrayList<Lifetrack_infobean> insertObjectList = new ArrayList<Lifetrack_infobean>();
            insertObjectList.add(infoBeanObj);
            db.weighttrackentry(insertObjectList);
            insertObjectList.clear();
            MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
            measuDataManager.syncMeasudata(MeasuDataManager.MEASU_DATA_TYPE_WS, true);*/

        } else if (ADGattUUID.BloodPressureMeasurement.toString().equals(characteristicUuidString)) {


            int sys = (int) bundle.getFloat(ADGattService.KEY_SYSTOLIC);
            int dia = (int) bundle.getFloat(ADGattService.KEY_DIASTOLIC);
            int pul = (int) bundle.getFloat(ADGattService.KEY_PULSE_RATE);
            int irregularPulseDetection = bundle.getInt(ADGattService.KEY_IRREGULAR_PULSE_DETECTION);

            int year = bundle.getInt(ADGattService.KEY_YEAR);
            int month = bundle.getInt(ADGattService.KEY_MONTH);
            int day = bundle.getInt(ADGattService.KEY_DAY);

            int hours = bundle.getInt(ADGattService.KEY_HOURS);
            int minutes = bundle.getInt(ADGattService.KEY_MINUTES);
            int seconds = bundle.getInt(ADGattService.KEY_SECONDS);

            String finaldate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
            String finaltime = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
            String finalTimeStamp = String.format(Locale.getDefault(), "%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hours, minutes, seconds);

            Lifetrack_infobean infoBeanObj = new Lifetrack_infobean();
            infoBeanObj.setDate(finaldate);
            infoBeanObj.setTime(finaltime);
            infoBeanObj.setPulse(String.valueOf(pul));
            infoBeanObj.setSystolic(String.valueOf(sys));
            infoBeanObj.setDiastolic(String.valueOf(dia));
            infoBeanObj.setPulseUnit("bpm");
            infoBeanObj.setSystolicUnit("mmhg");
            infoBeanObj.setDiastolicUnit("mmhg");
            infoBeanObj.setIsSynced("no");
            infoBeanObj.setIrregularPulseDetection(String.valueOf(irregularPulseDetection));
            long dateValue = convertDateintoMs(finalTimeStamp);
            infoBeanObj.setDateTimeStamp(String.valueOf(dateValue));

            /*String weightDeviceId = "web." + ADSharedPreferences.getString(ADSharedPreferences.KEY_USER_ID, "");
            infoBeanObj.setDeviceId(weightDeviceId);*/

            getNavigator().setIndicatorMessage(mContext.getResources().getString(R.string.indicator_complete_receive));

            //Insert in DB and sync

            /*final ArrayList<Lifetrack_infobean> insertObjectList = new ArrayList<Lifetrack_infobean>();
            insertObjectList.add(infoBeanObj);
            db.bpEntry(insertObjectList);
            insertObjectList.clear();
            MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
            measuDataManager.syncMeasudata(MeasuDataManager.MEASU_DATA_TYPE_BP, true);*/

            //Notify new data to UI
            //getNavigator().result(infoBeanObj);

            //Save Reading data - Local DB + Server
            Log.d("saveReading", "ADGattUUID.BloodPressureMeasurement.toString().equals(characteristicUuidString)");
            saveReading(infoBeanObj);

            //Insert new data into DB
           /* final HealthReading healthReading = new HealthReading(0,infoBeanObj.getSystolic(),infoBeanObj.getDiastolic(),infoBeanObj.getPulse(),infoBeanObj.getDateTimeStamp(),false);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    getRespository().addNewHealthRecord(healthReading);
                }
            });*/

        } else if (ADGattUUID.TemperatureMeasurement.toString().equals(characteristicUuidString)) {
            BluetoothGatt gatt = BleReceivedService.getGatt();

            String deviceName = gatt.getDevice().getName();
            float value = (float) bundle.getFloat(ADGattService.KEY_TEMPERATURE_VALUE);
            String unit = (String) bundle.getString(ADGattService.KEY_TEMPERATURE_UNIT);

            int year = (int) bundle.getInt(ADGattService.KEY_YEAR);
            int month = (int) bundle.getInt(ADGattService.KEY_MONTH);
            int day = (int) bundle.getInt(ADGattService.KEY_DAY);

            int hours = (int) bundle.getInt(ADGattService.KEY_HOURS);
            int minutes = (int) bundle.getInt(ADGattService.KEY_MINUTES);
            int seconds = (int) bundle.getInt(ADGattService.KEY_SECONDS);

            String finaldate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
            String finaltime = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
            String finalTimeStamp = String.format(Locale.getDefault(), "%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hours, minutes, seconds);

            Lifetrack_infobean thermometerInfo = new Lifetrack_infobean();
            thermometerInfo.setDate(finaldate);
            thermometerInfo.setTime(finaltime);
            long dateValue = convertDateintoMs(finalTimeStamp);
            thermometerInfo.setDateTimeStamp(String.valueOf(dateValue));

            thermometerInfo.setThermometerDeviceName(deviceName);
            thermometerInfo.setThermometerValue(String.valueOf(value));
            thermometerInfo.setThermometerUnit(unit);

            thermometerInfo.setIsSynced("no");

            /*String weightDeviceId = "web." + ADSharedPreferences.getString(ADSharedPreferences.KEY_USER_ID, "");
            thermometerInfo.setDeviceId(weightDeviceId);*/

            getNavigator().setIndicatorMessage(mContext.getResources().getString(R.string.indicator_complete_receive));

            // insert into db and sync

            /*final ArrayList<Lifetrack_infobean> insertObjectList = new ArrayList<Lifetrack_infobean>();

            if (unit.equalsIgnoreCase(ADSharedPreferences.VALUE_TEMPERATURE_UNIT_F)) {
                if (!Locale.getDefault().equals(Locale.JAPAN)) {
                    insertObjectList.add(thermometerInfo);
                } else {
                    getNavigator().dismissIndicator();
                    return;
                }
            } else {
                insertObjectList.add(thermometerInfo);
            }

            if (unit.equalsIgnoreCase(ADSharedPreferences.VALUE_TEMPERATURE_UNIT_C)) {
                ADSharedPreferences.putString(ADSharedPreferences.KEY_TEMPERATURE_UNITS, ADSharedPreferences.VALUE_TEMPERATURE_UNIT_C);
            } else {
                ADSharedPreferences.putString(ADSharedPreferences.KEY_TEMPERATURE_UNITS, ADSharedPreferences.VALUE_TEMPERATURE_UNIT_F);
            }

            db.entryThermometerInfo(insertObjectList);

            insertObjectList.clear();
            MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
            measuDataManager.syncMeasudata(MeasuDataManager.MEASU_DATA_TYPE_TH, true);*/

        } else if (ADGattUUID.AndCustomtrackerService.toString().equals(characteristicUuidString)) {
/*
            ArrayList<Lifetrack_infobean> hashmapList = (ArrayList<Lifetrack_infobean>) bundle.getSerializable("activity_data");

            MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
            measuDataManager.syncMeasudata(MeasuDataManager.MEASU_DATA_TYPE_AM, true);

            //Checking if there is any BP data
            ArrayList<HashMap> bpMapList = (ArrayList<HashMap>) bundle.getSerializable("bp_data");
            ArrayList<Lifetrack_infobean> insertObjectList = new ArrayList<Lifetrack_infobean>(0);
            for (int i = 0; i < bpMapList.size(); i++) {
                HashMap<String, Object> bpData = bpMapList.get(i);
                Lifetrack_infobean infoBeanObj = new Lifetrack_infobean();

                //Extracting values from the hashmap
                int sys = Integer.parseInt(bpData.get("systolic").toString());
                int dia = Integer.parseInt(bpData.get("diastolic").toString());
                int pul = Integer.parseInt(bpData.get("pulse").toString());
                int year = Integer.parseInt(bpData.get("year").toString());
                int month = Integer.parseInt(bpData.get("month").toString());
                int day = Integer.parseInt(bpData.get("day").toString());
                int hours = Integer.parseInt(bpData.get("hour").toString());
                int minutes = Integer.parseInt(bpData.get("minutes").toString());
                int seconds = Integer.parseInt(bpData.get("seconds").toString());

                String finaldate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
                String finaltime = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
                String finalTimeStamp = String.format(Locale.getDefault(), "%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hours, minutes, seconds);
                infoBeanObj.setDate(finaldate);
                infoBeanObj.setTime(finaltime);
                infoBeanObj.setPulse(String.valueOf(pul));
                infoBeanObj.setSystolic(String.valueOf(sys));
                infoBeanObj.setDiastolic(String.valueOf(dia));
                infoBeanObj.setPulseUnit("bpm");
                infoBeanObj.setSystolicUnit("mmhg");
                infoBeanObj.setDiastolicUnit("mmhg");
                infoBeanObj.setIsSynced("no");
                //infoBeanObj.setIrregularPulseDetection(String.valueOf(irregularPulseDetection));
                long dateValue = convertDateintoMs(finalTimeStamp);
                infoBeanObj.setDateTimeStamp(String.valueOf(dateValue));
                infoBeanObj.setDeviceId("UW-302");
                insertObjectList.add(infoBeanObj);

            } //End of for loop , now add to database
            db.bpEntry(insertObjectList);
            getNavigator().setIndicatorMessage(mContext.getResources().getString(R.string.indicator_complete_receive));
            insertObjectList.clear();
            bpMapList.clear();
            measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
            measuDataManager.syncMeasudata(MeasuDataManager.MEASU_DATA_TYPE_BP, true);

            //Check if there is weight scale data
            ArrayList<HashMap> wsMapList = (ArrayList<HashMap>) bundle.getSerializable("weight_data");

            for (int i = 0; i < wsMapList.size(); i++) {
                HashMap<String, Object> wsData = wsMapList.get(i);
                Lifetrack_infobean infoBeanObj = new Lifetrack_infobean();

                //Extracting values from the hashmap

                double weight = Double.parseDouble(wsData.get("weight").toString());
                String weightString = String.format(Locale.getDefault(), "%.1f", weight);
                String unit = wsData.get("unit").toString();
                //Add the weight value to the shared preference
                ADSharedPreferences.putString(ADSharedPreferences.KEY_WEIGHT_SCALE_UNITS, unit);
                int year = Integer.parseInt(wsData.get("year").toString());
                int month = Integer.parseInt(wsData.get("month").toString());
                int day = Integer.parseInt(wsData.get("day").toString());
                int hours = Integer.parseInt(wsData.get("hour").toString());
                int minutes = Integer.parseInt(wsData.get("minutes").toString());
                int seconds = Integer.parseInt(wsData.get("seconds").toString());
                String finaldate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
                String finaltime = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
                String finalTimeStamp = String.format(Locale.getDefault(), "%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hours, minutes, seconds);

                infoBeanObj.setWeight(weightString);
                infoBeanObj.setDate(finaldate);
                infoBeanObj.setTime(finaltime);
                infoBeanObj.setWeightUnit(unit);
                infoBeanObj.setIsSynced("no");
                //infoBeanObj.setIrregularPulseDetection(String.valueOf(irregularPulseDetection));
                long dateValue = convertDateintoMs(finalTimeStamp);
                infoBeanObj.setDateTimeStamp(String.valueOf(dateValue));
                infoBeanObj.setDeviceId("UW-302");
                insertObjectList.add(infoBeanObj);

            } //End of for loop , now add to database
            db.weighttrackentry(insertObjectList);
            setIndicatorMessage(getResources().getString(R.string.indicator_complete_receive));
            insertObjectList.clear();
            wsMapList.clear();
            measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
            measuDataManager.syncMeasudata(MeasuDataManager.MEASU_DATA_TYPE_WS, true);*/

        }
    }

    public BluetoothManager getBluetoothManager() {
        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(mContext.BLUETOOTH_SERVICE);
        return bluetoothManager;
    }

    public long convertDateintoMs(String date) {
        long final_birth_date_timestamp = 0;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Date converteddate = null;
        try {
            converteddate = (Date) formatter.parse(date);
            final_birth_date_timestamp = converteddate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return final_birth_date_timestamp;
    }

    protected void onDestroy() {
        try {
            getNavigator().dismissIndicator();
            doStopService();

            if(transtekDeviceController!=null) {
                transtekDeviceController.stopScanningForData();
                transtekDeviceController = null;
            }

            //Register or UnRegister your broadcast receiver here
            mContext.unregisterReceiver(mMeasudataUpdateReceiver);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public void sendDummyReading() {
        for (int i = 0; i < 3; i++) {
            Lifetrack_infobean lifetrackInfobean = new Lifetrack_infobean();
            lifetrackInfobean.setSystolic(MathUtil.getRandomNumber(100, 130) + "");
            lifetrackInfobean.setDiastolic(MathUtil.getRandomNumber(80, 100) + "");
            lifetrackInfobean.setPulse(MathUtil.getRandomNumber(60, 90) + "");
            saveReading(lifetrackInfobean);
        }
    }

    private List<Lifetrack_infobean> lifetrackInfobeanList = new ArrayList<>();
    private Handler handler = new Handler();

    public void saveReading(final Lifetrack_infobean lifetrackInfobean) {
        //getNavigator().showIndicator("Processing data...");
        //Log.d(TAG, "new Reading "+lifetrackInfobean.getSystolic()+"/"+lifetrackInfobean.getDiastolic()+"/"+lifetrackInfobean.getPulse() +"/"+lifetrackInfobean.getDateTimeStamp());
        //if(lifetrackInfobean.getSystolic().equalsIgnoreCase("2047"))
        //  return;

        lifetrackInfobean.setDateTimeStamp((System.currentTimeMillis()) + "");
        Log.d(TAG, "1 new Reading To Save: " + lifetrackInfobean.getSystolic() + "/" + lifetrackInfobean.getDiastolic() + "/" + lifetrackInfobean.getPulse() + "/" + lifetrackInfobean.getDateTimeStamp());
        lifetrackInfobean.setDateTimeStamp((System.currentTimeMillis()) + "");
        lifetrackInfobeanList.add(lifetrackInfobean);

        //handler.removeCallbacks(runnable);
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(runnable, 6000);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "executing... saveReading_backgroudProcess()");
            saveReading_backgroudProcess();
        }
    };

    private void saveReading_backgroudProcess() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {


                if (lifetrackInfobeanList.size() == 0 || lifetrackInfobeanList.get(lifetrackInfobeanList.size() - 1).getSystolic().equalsIgnoreCase("2047")) {
                    Log.d(TAG, "saveReading_backgroudProcess lifetrackInfobean==null");
                    getNavigator().onReadingError("Error! Not found proper reading.\nPlease try again.");
                    return;
                }
                Lifetrack_infobean lifetrackInfobean = lifetrackInfobeanList.get(lifetrackInfobeanList.size() - 1);

                final HealthReading healthReading = new HealthReading();

                //case 1: Check for Aberrant reading
                long logTime = Long.parseLong(lifetrackInfobean.getDateTimeStamp());
                String s = DateUtils.convertMillisecToDateTime(logTime, "HH");
                int HH = Integer.parseInt(s);
                //int HH = Integer.parseInt(hh);
                if (HH < PROTOCOL_MORNING_MINIMUM_TIME || (HH >= PROTOCOL_MORNING_MAXIMUM_TIME && HH < PROTOCOL_EVENING_MINIMUM_TIME) || HH >= PROTOCOL_EVENING_MAXIMUM_TIME) {
                    //yes It is aberrant reading
                    //healthReading.setIs_abberant("1");
                    Log.d(TAG, "2 saveReading_backgroudProcess setIs_abberant(\"1\")");
                } else {
                    // During recommanded time zone
                    //healthReading.setIs_abberant("0");
                    Log.d(TAG, "2 saveReading_backgroudProcess setIs_abberant(\"0\")");
                }

                //case 2: Check for Protocol bound reading

                if (measureReadingForProtocol) {
                    healthReading.setProtocol_id(protocolId);
                    healthReading.setIs_abberant("0");
                    Log.d(TAG, "3 saveReading_backgroudProcess setProtocol_id(protocolId)");
                } else {
                    healthReading.setProtocol_id("");
                    healthReading.setIs_abberant("1");
                    Log.d(TAG, "3 saveReading_backgroudProcess setProtocol_id(\"\")");
                }

                //Set Other Details
                healthReading.setSync(false);
                healthReading.setLogTime(lifetrackInfobean.getDateTimeStamp());
                healthReading.setReading_time(Long.parseLong(lifetrackInfobean.getDateTimeStamp()));
                healthReading.setPulse(lifetrackInfobean.getPulse());
                healthReading.setDiastolic(lifetrackInfobean.getDiastolic());
                healthReading.setSystolic(lifetrackInfobean.getSystolic());
                healthReading.setReadingID(0);
                healthReading.setProtocol_reading_no(total_protocolReadingTaken);


                if (isReadingTaken)
                    return;

                //Save This reading IN DB

                getRespository().addNewHealthRecord(healthReading);
                Log.d(TAG, "4 saveReading_backgroudProcess addNewHealthRecord(healthReading)");

                //Deliver Reading to UI
                if (getNavigator() != null) {
                    getNavigator().result(healthReading);
                    Log.d(TAG, "5 result(healthReading)");
                }

                if (readingUploadToServer) {
                    Log.d(TAG, "6 saveReading_backgroudProcess uploadReadingToServer(healthReading)");
                    uploadReadingToServer(healthReading);
                }

                isReadingTaken = true;
                lifetrackInfobeanList.clear();
                lifetrackInfobean = null;
            }
        });
    }

    private void uploadReadingToServer(final HealthReading healthReading) {
        String accessToken = getRespository().getAccessToken();
        String patientId = getRespository().getPatientId();
        String userId = getRespository().getCurrentUserId();

        //Send Reading Data to server
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < 1; i++) {
            JsonObject jsonObject = new JsonObject();
            try {

                jsonObject.addProperty("patientId", userId);
                jsonObject.addProperty("systolic", healthReading.getSystolic());
                jsonObject.addProperty("diastolic", healthReading.getDiastolic());
                jsonObject.addProperty("pulse_data", healthReading.getPulse());
                jsonObject.addProperty("reading_time", (Long.parseLong(healthReading.getLogTime()) / 1000) + "");
                jsonObject.addProperty("device_id", "12345");
                jsonObject.addProperty("device_name", "AND");
                jsonObject.addProperty("device_mac_address", "12:34:56:78:90");
                jsonObject.addProperty("is_abberant", healthReading.getIs_abberant());
                jsonObject.addProperty("protocol_id", healthReading.getProtocol_id());
                jsonObject.addProperty("protocol_no", healthReading.getProtocol_reading_no()+"");
                jsonArray.add(jsonObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (jsonArray.size() == 0)
            return;

        String json = jsonArray.toString();
        Log.d(TAG, "7 uploadReadingToServer jsonArray: " + json);

        disposables.add(getRespository().addBpReadings(accessToken, jsonArray)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .subscribe(new Consumer<AddBpReadingResponse>() {
                    @Override
                    public void accept(AddBpReadingResponse addBpReadingResponse) throws Exception {

                        //Throwable throwable = new Throwable("Data Syn to server");
                        //getNavigator().handleError(throwable);
                        Log.d(TAG, "8 uploadReadingToServer accept successfull");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        //getNavigator().handleError(throwable);
                        Log.d(TAG, "8 uploadReadingToServer accept Throwable");
                    }
                }));
    }

    public void isReadingForProtocol() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                List<ProtocolModel> allProtocol = getRespository().getAllProtocol();

                if (allProtocol != null && allProtocol.size() > 0) {
                    ProtocolModel protocolModel = allProtocol.get(0);
                    String protocolCode = protocolModel.getProtocolCode();
                    String startDay = protocolModel.getStartDay();
                    String todayDate = DateUtils.getDateString(0, "MMM dd,yyyy");

                    long l = DateUtils.compareTimeString(startDay, todayDate, "MMM dd,yyyy");
                    if (l <= 0) {
                        //Protocol has Started

                        String morningReadingStartTime = ApplicationBPConnect.PROTOCOL_MORNING_MINIMUM_TIME + ":" + "00";
                        String morningReadingEndTime = ApplicationBPConnect.PROTOCOL_MORNING_MAXIMUM_TIME + ":" + "00";

                        String eveningReadingStartTime = ApplicationBPConnect.PROTOCOL_EVENING_MINIMUM_TIME + ":" + "00";
                        String eveningReadingEndTime = ApplicationBPConnect.PROTOCOL_EVENING_MAXIMUM_TIME + ":" + "00";

                        String currentTime = DateUtils.getTimeString(0, "HH:mm");

                        long l1 = DateUtils.compareTimeString(currentTime, eveningReadingStartTime, "HH:mm");
                        //Check For Evening Alarm Readings
                        if (l1 >= 0) {
                            //Reading at Evening

                            String eveningReadingStartTime_InMilli = DateUtils.convertDateStringToMillisec(todayDate + " " + eveningReadingStartTime, "MMM dd,yyyy HH:mm");
                            String eveningReadingEndTime_InMilli = DateUtils.convertDateStringToMillisec(todayDate + " " + eveningReadingEndTime, "MMM dd,yyyy HH:mm");


                            long l2 = Long.parseLong(eveningReadingStartTime_InMilli) * 1000;
                            long l3 = Long.parseLong(eveningReadingEndTime_InMilli) * 1000;

                            List<HealthReading> lastAlarmRecords = getRespository().getLastAlarmRecords(l2, l3);

                            if (lastAlarmRecords != null && lastAlarmRecords.size() >= 2) {
                                //Evening Alarm Protocol Reading has already been taken
                                getNavigator().isReadingForProtocol_Result(false, null, 0);
                            } else {
                                //Evening Alarm Protocol Reading not taken yet
                                if (lastAlarmRecords == null)
                                    getNavigator().isReadingForProtocol_Result(true, protocolCode, 0);
                                else
                                    getNavigator().isReadingForProtocol_Result(true, protocolCode, lastAlarmRecords.size());
                            }
                        } else {
                            //Check For Morning Alarm Readings
                            l1 = DateUtils.compareTimeString(currentTime, morningReadingStartTime, "HH:mm");
                            long l2 = DateUtils.compareTimeString(currentTime, morningReadingEndTime, "HH:mm");
                            if (l1 >= 0 && l2 <= 0) {
                                //After Morning Alarm Time

                                String morningReadingStartTime_InMilli = DateUtils.convertDateStringToMillisec(todayDate + " " + morningReadingStartTime, "MMM dd,yyyy HH:mm");
                                String morningReadingEndTime_InMilli = DateUtils.convertDateStringToMillisec(todayDate + " " + morningReadingEndTime, "MMM dd,yyyy HH:mm");

                                long l3 = Long.parseLong(morningReadingStartTime_InMilli) * 1000;
                                long l4 = Long.parseLong(morningReadingEndTime_InMilli) * 1000;

                                List<HealthReading> lastAlarmRecords = getRespository().getLastAlarmRecords(l3, l4);

                                if (lastAlarmRecords != null && lastAlarmRecords.size() >= 2) {
                                    //Morning Alarm Protocol Reading has already been taken
                                    getNavigator().isReadingForProtocol_Result(false, null, 0);
                                } else {
                                    //Morning Alarm Protocol Reading not taken yet
                                    if (lastAlarmRecords == null)
                                        getNavigator().isReadingForProtocol_Result(true, protocolCode, 0);
                                    else
                                        getNavigator().isReadingForProtocol_Result(true, protocolCode, lastAlarmRecords.size());
                                }
                            } else {
                                getNavigator().isReadingForProtocol_Result(false, null, 0);
                            }
                        }

                    } else {
                        getNavigator().isReadingForProtocol_Result(false, null, 0);
                    }
                } else {
                    getNavigator().isReadingForProtocol_Result(false, null, 0);
                }
            }
        });
    }

    //------------------------------- iHealth BP3L Device Callbacks------------------------------------------------------------------

    final DeviceCharacteristic BP3LDevice = new DeviceCharacteristic();
    IHealthDeviceController iHealthDeviceController = new IHealthDeviceController(mContext, this);

    public void scanBP3LDevice() {
        iHealthDeviceController.discoverDevice(null);
    }

    public void connectBP3LDevice(String deviceName, String deviceMac, String deviceType) {
        iHealthDeviceController.ConnectDevice(deviceName, deviceMac, "");
    }

    public void startMeasuringBPFromBP3LDevice(String deviceName, String deviceMac, boolean measureReadingForProtocol, String protocolId,int total_protocolReadingTaken) {
        this.measureReadingForProtocol = measureReadingForProtocol;
        this.protocolId = protocolId;
        this.total_protocolReadingTaken = total_protocolReadingTaken;

        isReadingTaken = false;
        iHealthDeviceController.measeureReading(deviceName, deviceMac);
    }

    public void stopMeaseureReading() {
        iHealthDeviceController.stopMeaseureReading();
    }

    public void disconnectFromBP3LDevice() {
        iHealthDeviceController.disconnect();
        iHealthDeviceController.onStop();
    }

    @Override
    public void onDeviceDetected_BP3L(DeviceCharacteristic deviceCharacteristic) {
        BP3LDevice.setDeviceName(deviceCharacteristic.getDeviceName());
        BP3LDevice.setDeviceMac(deviceCharacteristic.getDeviceMac());
        BP3LDevice.setDeviceType(deviceCharacteristic.getDeviceType());
    }

    @Override
    public void onScanCompleted_BP3L() {
        Log.d("onScanCompleted_BP3L", "onScanCompleted_BP3L");
        getNavigator().onDeviceFound_iHealthBP3L(BP3LDevice.getDeviceName(), BP3LDevice.getDeviceMac(), BP3LDevice.getDeviceType() + "");
    }

    @Override
    public void onDeviceConnected_BP3L(String deviceName, String deviceMac) {
        getNavigator().onDeviceConnected_iHealthBP3L(deviceName, deviceMac);
    }

    @Override
    public void onConnectionError_BP3L(String messg) {

    }

    @Override
    public void onDeviceDisconnected_BP3L() {

    }


    @Override
    public void onReadingResult(String sys, String dia, String pulse) {
        Lifetrack_infobean lifetrack_infobean = new Lifetrack_infobean();
        lifetrack_infobean.setSystolic(sys);
        lifetrack_infobean.setDiastolic(dia);
        lifetrack_infobean.setPulse(pulse);
        saveReading(lifetrack_infobean);
    }

    @Override
    public void onError(String msg) {
        getNavigator().onReadingError(msg);
    }

    public boolean isAuthorizeForBP3L(Context mContext) {
        return IHealthDeviceController.isAuthorizedToAccessDevice(mContext);
    }


    //------------------------------- Transtek Device Callbacks------------------------------------------------------------------

    @Override
    public void onDeviceDetected_Transtek(LsDeviceInfo foundDevice) {

    }

    @Override
    public void onScanCompleted_Transtek() {

    }

    @Override
    public void onDeviceConnected_Transtek(LsDeviceInfo lsDeviceInfo) {

    }

    @Override
    public void onConnectionError_Transtek(String messg) {

    }

    @Override
    public void onDeviceDisconnected_Transtek() {

    }
}

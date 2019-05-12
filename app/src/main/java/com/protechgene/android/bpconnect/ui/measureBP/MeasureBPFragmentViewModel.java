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
import static com.protechgene.android.bpconnect.ui.ApplicationBPConnect.PROTOCOL_READING_ACCEPTED_TIME_WINDOW;
import static com.protechgene.android.bpconnect.ui.ApplicationBPConnect.PROTOCOL_READING__EVENING;
import static com.protechgene.android.bpconnect.ui.ApplicationBPConnect.PROTOCOL_READING__MORNING;


public class MeasureBPFragmentViewModel extends BaseViewModel<MeasureBPFragmentNavigator> {


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
    private Context mContext;


    public MeasureBPFragmentViewModel(Repository repository) {
        super(repository);
    }

   public void connectToDevice(Context context)
   {

       mContext = context;
       // 1. Enable Bluetooth in background
       if(!getBluetoothManager().getAdapter().isEnabled())
       {
           getBluetoothManager().getAdapter().enable();
           getNavigator().turningOnBluetooth();
           return;
       }

       // 2. Check if GPS is Active, If Not ask user to turn it on ,receive user action in onActivityResult
       GpsUtils gpsUtils = new GpsUtils((Activity)mContext);
       gpsUtils.turnGPSOn(new GpsUtils.onGpsListener() {
           @Override
           public void gpsStatus(boolean isGPSEnable) {

               if(isGPSEnable)
               {
                   deviceList = new ArrayList<BluetoothDevice>();

                   // 3. Check Device Paired Status
                   boolean isBpDevicePaired =  isDevicePaired();
                   if(isBpDevicePaired) {
                       mContext.registerReceiver(mMeasudataUpdateReceiver, MeasuDataUpdateIntentFilter());
                       getNavigator().bpDevicePairedStatus(true);
                       doStartService();
                   }else
                   {
                       getNavigator().bpDevicePairedStatus(false);
                   }
               }
           }
       });

   }

    protected void onResume(boolean measureReadingForProtocol) {

        this.measureReadingForProtocol = measureReadingForProtocol;
        isReadingTaken = false;

        isDevicePaired();

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
            if (resultCode == ((Activity)mContext).RESULT_OK) {
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


    public boolean isBluetoothEnabled()
    {
        return getBluetoothManager().getAdapter().isEnabled();
        /*if(!getBluetoothManager().getAdapter().isEnabled())
        {
            getBluetoothManager().getAdapter().enable();
            getNavigator().turningOnBluetooth();
            return;
        }*/
    }

    public void enableBluetooth()
    {
        getBluetoothManager().getAdapter().enable();
    }

    boolean isDevicePaired() {
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        Set<BluetoothDevice> pairingDevices = null;
        pairingDevices = bluetoothManager.getAdapter().getBondedDevices();
        BluetoothDevice deviceName;
        pairedDeviceList.clear();//Clear the list , then initialize the list
        if (pairingDevices == null) {
            pairedDeviceList.clear();
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

            if(pairedDeviceList.contains("bpDevice"))
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
        if (mIsBleReceiver) {
            mContext.unregisterReceiver(bleServiceReceiver);
            mIsBleReceiver = false;
        }

        Intent intent1 = new Intent(mContext, BleReceivedService.class);
        mContext.stopService(intent1);
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
            ((Activity)mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  // For UW-302BLE
            deviceList.clear();
            ((Activity)mContext).runOnUiThread(new Runnable() {
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
            ((Activity)mContext).runOnUiThread(new Runnable() {
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
                        ((Activity)mContext).runOnUiThread(new Runnable() {
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
            Log.d("saveReading","ADGattUUID.BloodPressureMeasurement.toString().equals(characteristicUuidString)");
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
        getNavigator().dismissIndicator();
        doStopService();
        try {
            //Register or UnRegister your broadcast receiver here
            mContext.unregisterReceiver(mMeasudataUpdateReceiver);
        } catch(IllegalArgumentException e) {

            e.printStackTrace();
        }

    }


        public void saveReading(final Lifetrack_infobean lifetrackInfobean)
        {
            //getNavigator().showIndicator("Processing data...");
            Log.d("saveReading","saveReading");
            lifetrackInfobean.setDateTimeStamp((System.currentTimeMillis())+"");

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    final HealthReading healthReading = new HealthReading();

                    //case 1: Check for Aberrant reading
                    long logTime = Long.parseLong(lifetrackInfobean.getDateTimeStamp());
                    String s = DateUtils.convertMillisecToDateTime(logTime, "HH");
                    int HH = Integer.parseInt(s);
                    //int HH = Integer.parseInt(hh);
                    if(HH<PROTOCOL_MORNING_MINIMUM_TIME || (HH>=PROTOCOL_MORNING_MAXIMUM_TIME && HH<PROTOCOL_EVENING_MINIMUM_TIME) || HH>=PROTOCOL_EVENING_MAXIMUM_TIME )
                    {
                        //yes It is aberrant reading
                        healthReading.setIs_abberant("1");
                    }else
                    {
                        // During recommanded time zone
                        healthReading.setIs_abberant("0");
                    }

                    //case 2: Check for Protocol bound reading
                    List<ProtocolModel> allProtocol1 = getRespository().getAllProtocol();
                    if(allProtocol1!=null && allProtocol1.size()>0)
                    {
                        //protocol exists
                        ProtocolModel protocolModel = allProtocol1.get(0);

                        String readingLogTime = DateUtils.convertMillisecToDateTime(logTime, "HH:mm");

                        String morningReadingTime = protocolModel.getMorningReadingTime();
                        long compareResult = DateUtils.compareTimeString(readingLogTime, morningReadingTime, "HH:mm");
                        if(compareResult<0)
                        {
                            //Reading Done before morning protocol time
                            healthReading.setProtocol_id("");
                        }else if(compareResult<PROTOCOL_READING_ACCEPTED_TIME_WINDOW && measureReadingForProtocol)
                        {
                            //Reading Done in 10 min of Morning Protocol Time, so it is valid protocol reading
                            healthReading.setProtocol_id(protocolModel.getProtocolCode()+"");
                        }else
                        {
                            //Reading done in evening
                            String eveningReadingTime = protocolModel.getEveningReadingTime();
                            compareResult = DateUtils.compareTimeString(readingLogTime, eveningReadingTime, "HH:mm");
                            if(compareResult<0)
                            {
                                //Reading Done before evening protocol time
                                healthReading.setProtocol_id("");
                            }else if(compareResult<PROTOCOL_READING_ACCEPTED_TIME_WINDOW && measureReadingForProtocol)
                            {
                                //Reading Done in 10 min of evening Protocol Time, so it is valid protocol reading
                                healthReading.setProtocol_id(protocolModel.getProtocolCode()+"");
                            }else
                            {
                                //Reading Done after evening protocol expire window time
                                healthReading.setProtocol_id("");
                            }
                        }

                    }else
                    {
                        //No Protocol Exists
                        healthReading.setProtocol_id("");
                    }

                    //Set Other Details
                    healthReading.setSync(false);
                    healthReading.setLogTime(lifetrackInfobean.getDateTimeStamp());
                    healthReading.setReading_time(Long.parseLong(lifetrackInfobean.getDateTimeStamp()));
                    healthReading.setPulse(lifetrackInfobean.getPulse());
                    healthReading.setDiastolic(lifetrackInfobean.getDiastolic());
                    healthReading.setSystolic(lifetrackInfobean.getSystolic());
                    healthReading.setReadingID(0);


                    if(isReadingTaken)
                        return;

                    //Save This reading IN DB
                    Log.d("saveReading","addNewHealthRecord");
                    getRespository().addNewHealthRecord(healthReading);
                    //Deliver Reading to UI
                    getNavigator().result(healthReading);

                    uploadReadingToServer(healthReading);
                    isReadingTaken = true;

                }
            });
        }

        private void uploadReadingToServer( final HealthReading healthReading )
        {
            String accessToken = getRespository().getAccessToken();
            String patientId = getRespository().getPatientId();
            String userId = getRespository().getCurrentUserId();

            //Send Reading Data to server
            JsonArray jsonArray = new JsonArray();
            for(int i =0;i<1;i++)
            {
                JsonObject jsonObject = new JsonObject();
                try {

                    jsonObject.addProperty("patientId",userId);
                    jsonObject.addProperty("systolic",healthReading.getSystolic());
                    jsonObject.addProperty("diastolic",healthReading.getDiastolic());
                    jsonObject.addProperty("pulse_data",healthReading.getPulse());
                    jsonObject.addProperty("reading_time",(Long.parseLong(healthReading.getLogTime())/1000) +"");
                    jsonObject.addProperty("device_id","12345");
                    jsonObject.addProperty("device_name","AND");
                    jsonObject.addProperty("device_mac_address","12:34:56:78:90");
                    jsonObject.addProperty("is_abberant",healthReading.getIs_abberant());
                    jsonObject.addProperty("protocol_id",healthReading.getProtocol_id());
                    jsonArray.add(jsonObject);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(jsonArray.size()==0)
                return;

            String json  = jsonArray.toString();
            Log.d("createDummyReadings","createDummyReadings:"+json);

            disposables.add(getRespository().addBpReadings(accessToken,jsonArray)
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
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                            //getNavigator().handleError(throwable);
                        }
                    }));
        }


        public void isReadingForProtocol()
        {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {

                    List<ProtocolModel> allProtocol = getRespository().getAllProtocol();

                    if(allProtocol !=null && allProtocol.size()>0)
                    {
                        ProtocolModel protocolModel = allProtocol.get(0);
                        String startDay = protocolModel.getStartDay();
                        String morningReadingTime = protocolModel.getMorningReadingTime();
                        String eveningReadingTime = protocolModel.getEveningReadingTime();

                        String todayDate = DateUtils.getDateString(0, "MMM dd,yyyy");
                        long l = DateUtils.compareTimeString(startDay, todayDate, "MMM dd,yyyy");
                        if(l<=0)
                        {
                            //Protocol has Started
                            String currentTime = DateUtils.getTimeString(0, "HH:mm");

                            long l1 = DateUtils.compareTimeString(currentTime, eveningReadingTime,"HH:mm");
                            //Check For Evening Alarm Readings
                            if(l1>=0)
                            {
                                //After Evening Alarm Time

                                String s = DateUtils.convertDateStringToMillisec(todayDate+" "+eveningReadingTime, "MMM dd,yyyy HH:mm");
                                long l2 = Long.parseLong(s) * 1000;
                                List<HealthReading> lastAlarmRecords = getRespository().getLastAlarmRecords(l2, l2 + ApplicationBPConnect.PROTOCOL_READING_ACCEPTED_TIME_WINDOW);

                                if(lastAlarmRecords!=null && lastAlarmRecords.size()>0)
                                {
                                    //Evening Alarm Protocol Reading has already been taken
                                    getNavigator().isProtocolTypeReading(false);
                                }
                                else
                                {
                                    //Evening Alarm Protocol Reading not taken yet
                                    long l3 = System.currentTimeMillis();
                                    if(l3>=l2 && l3<=(l2+ApplicationBPConnect.PROTOCOL_READING_ACCEPTED_TIME_WINDOW))
                                    {
                                        getNavigator().isProtocolTypeReading(true);
                                    }else
                                    {
                                        getNavigator().isProtocolTypeReading(false);
                                    }
                                }
                            }else
                            {
                                //Check For Morning Alarm Readings

                                l1 = DateUtils.compareTimeString(currentTime, morningReadingTime,"HH:mm");
                                if(l1>=0)
                                {
                                    //After Morning Alarm Time

                                    String s = DateUtils.convertDateStringToMillisec(todayDate+" "+morningReadingTime, "MMM dd,yyyy HH:mm");
                                    long l2 = Long.parseLong(s) *1000 ;
                                    List<HealthReading> lastAlarmRecords = getRespository().getLastAlarmRecords(l2, l2 + ApplicationBPConnect.PROTOCOL_READING_ACCEPTED_TIME_WINDOW);

                                    if(lastAlarmRecords!=null && lastAlarmRecords.size()>0)
                                    {
                                        //Morning Alarm Protocol Reading has already been taken
                                        getNavigator().isProtocolTypeReading(false);
                                    }
                                    else
                                    {
                                        //Morning Alarm Protocol Reading not taken yet
                                        long l3 = System.currentTimeMillis();
                                        if(l3>=l2 && l3<=(l2+ApplicationBPConnect.PROTOCOL_READING_ACCEPTED_TIME_WINDOW))
                                        {
                                            getNavigator().isProtocolTypeReading(true);
                                        }else
                                        {
                                            getNavigator().isProtocolTypeReading(false);
                                        }
                                    }
                                }else
                                {
                                    getNavigator().isProtocolTypeReading(false);
                                }
                            }

                        }else
                        {
                            getNavigator().isProtocolTypeReading(false);
                        }
                    }else
                    {
                        getNavigator().isProtocolTypeReading(false);
                    }
                }
            });
        }
}

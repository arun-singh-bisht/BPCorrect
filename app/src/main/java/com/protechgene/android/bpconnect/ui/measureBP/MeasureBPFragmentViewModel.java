package com.protechgene.android.bpconnect.ui.measureBP;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.view.WindowManager;

import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.ble.ADGattUUID;
import com.protechgene.android.bpconnect.data.ble.BleReceivedService;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

import java.util.ArrayList;
import java.util.Set;


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


    private static final int REQUEST_ENABLE_BLUETOOTH = 1000;
    private boolean mIsBindBleReceivedServivce = false;
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

    private Context mContext;

    public MeasureBPFragmentViewModel(Repository repository) {
        super(repository);
    }

   public void connectToDevice(Context context)
   {
       mContext = context;

       int val = ((0x07 & 0xff) << 8) | (0xE1 & 0xff);
       int sys = ((0x00 & 0xff) << 8) | (0x65 & 0xff);

       context.registerReceiver(mMeasudataUpdateReceiver, MeasuDataUpdateIntentFilter());

       //Call function to get paired device
       isDevicePaired();
       doStartService();
       initializeUI_new();
       setListiner();
   }

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
                //showIndicator(getResources().getString(R.string.indicator_start_receive));
                getNavigator().connectedToDevice();

                BleReceivedService.getGatt().discoverServices();
                setDateTimeDelay = Long.MIN_VALUE;
                indicationDelay = Long.MIN_VALUE;
            } else if (BleReceivedService.TYPE_GATT_DISCONNECTED.equals(type)) {
                //dismissIndicator();
                getNavigator().disconnectedFromDevice();
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
                            //dismissIndicator();
                            getNavigator().disconnectedFromDevice();
                            doStartLeScan();
                        } else {
                            BluetoothGatt gatt = BleReceivedService.getGatt();
                            if (gatt != null) {
                                gatt.connect();
                            }
                        }
                    }
                } else {
                    //dismissIndicator();
                    getNavigator().disconnectedFromDevice();
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
                        String[] firmRevisionArray = getResources().getStringArray(R.array.firm_revision_group1);
                        boolean isGroup1 = false;
                        for (String revision : firmRevisionArray) {
                            if (revision.contains(firmRevision)) {
                                isGroup1 = true;
                                break;
                            }
                        }

                        String[] cesFirmRevisionCesArray = getResources().getStringArray(R.array.firm_revision_ces);
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
                                        dismissIndicator();
                                    }
                                } else {
                                    dismissIndicator();
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
                                        dismissIndicator();
                                    }
                                }
                            }, indicationDelay);
                        }
                    }
                } else if (BleReceivedService.TYPE_INDICATION_VALUE.equals(type)) {
                    setIndicatorMessage(getResources().getString(R.string.indicator_during_receive));
                    Bundle bundle = intent.getBundleExtra(BleReceivedService.EXTRA_VALUE);
                    String uuidString = intent.getExtras().getString(BleReceivedService.EXTRA_CHARACTERISTIC_UUID);
                    receivedData(uuidString, bundle);
                    uiThreadHandler.removeCallbacks(disableIndicationRunnable);
                    uiThreadHandler.postDelayed(disableIndicationRunnable, 4000L);
                } else if (BleReceivedService.TYPE_DESCRIPTOR_WRITE.equals(type)) {
                    //For now do this only for the UW-302
                    String device_name = intent.getExtras().getString(BleReceivedService.EXTRA_DEVICE_NAME);
                    if (device_name.contains("UW-302BLE")) {

                    }

                }
            }
        }
    };

    void isDevicePaired() {
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        Set<BluetoothDevice> pairingDevices = null;
        pairingDevices = bluetoothManager.getAdapter().getBondedDevices();
        BluetoothDevice deviceName;
        pairedDeviceList.clear();//Clear the list , then initialize the list
        if (pairingDevices == null) {
            pairedDeviceList.clear();
            return;
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
        }
    }

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
}

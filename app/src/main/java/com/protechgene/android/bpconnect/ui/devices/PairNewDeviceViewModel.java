package com.protechgene.android.bpconnect.ui.devices;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.protechgene.android.bpconnect.data.Repository;
import com.protechgene.android.bpconnect.data.ble.ADGattUUID;
import com.protechgene.android.bpconnect.data.ble.BleConnectService;
import com.protechgene.android.bpconnect.deviceManager.iHealthbp3l.DeviceCharacteristic;
import com.protechgene.android.bpconnect.ui.base.BaseViewModel;

import java.lang.reflect.Method;


public class PairNewDeviceViewModel extends BaseViewModel<PairNewDeviceNavigator> implements PairDeviceViewModelInterface {


    private Context context;
    protected BleConnectService mBleService;
    private boolean mIsServiceBind = false;
    public static final String DEVICE_SCAN_MODE_KEY = "DeviceScanModeKey";
    public static final String DEVICE_SCAN_ADDRESS_KEY = "DeviceScanAddressKey";

    public static final int DEVICE_SCAN_MODE_NONE = 0;
    public static final int DEVICE_SCAN_MODE_BP = 1;
    public static final int DEVICE_SCAN_MODE_WS = 2;
    public static final int DEVICE_SCAN_MODE_AM = 3;
    public static final int DEVICE_SCAN_MODE_TM = 4;
    public static final int DEVICE_SCAN_MODE_AM_UW = 9;

    private int mDeviceScanMode = DEVICE_SCAN_MODE_NONE;
    private Handler mMainThreadHandler;
    private BroadcastReceiver mDevicePairingReceiver ;

    // One-Time Flag
    private boolean isShowPairing = false;

    public PairNewDeviceViewModel(Repository repository) {
        super(repository);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {

            mBleService = ((BleConnectService.BleConnectionBinder)service).getService();

            PairNewDeviceViewModel.this.onServiceConnected();
        }

        public void onServiceDisconnected(ComponentName className) {

            mBleService = null;

            PairNewDeviceViewModel.this.onServiceDisconnected();
        }
    };

    private BroadcastReceiver mBleConnectionReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Bundle bundle = intent.getExtras();


            if(BleConnectService.ACTION_DEVICE_SCAN.equals(action)) {
                String address = bundle.getString(BleConnectService.KEY_DEVICE_ADDRES);
                Log.d("mBleConnectionReceiver","action: ACTION_DEVICE_SCAN "+address);
                if(address != null) {
                    onFindConnectDevice(address);
                }
            }
            else if(BleConnectService.ACTION_DEVICE_REQUEST_PAIRING.equals(action)) {
                Log.d("mBleConnectionReceiver","action: ACTION_DEVICE_REQUEST_PAIRING ");
                registDeviceBondStateChanged();
            }
            else if(BleConnectService.ACTION_DISCOVERED_SERVICES.equals(action)) {
                //Need to start the separate flow here ACGS-10
                String device_name = bundle.getString(BleConnectService.KEY_DEVICE_NAME);
                Log.d("mBleConnectionReceiver","action: ACTION_DISCOVERED_SERVICES "+device_name);
                if(mBleService != null) {
                    if (device_name.contains("UW-302BLE")) {
                        mBleService.setupUW302Pairing();
                    } else {
                        mBleService.readBufferSizeWrtie();
                    }

                }
            }
            else if(BleConnectService.ACTION_READ_CHARACTER.equals(action)) {
                Log.d("mBleConnectionReceiver","action: ACTION_READ_CHARACTER ");
                mBleService.writeBufferSize();
                // to do Nothing
            }
            else if(BleConnectService.ACTION_WRITE_CHARACTER.equals(action)) {
                boolean isSuccess = bundle.getBoolean(BleConnectService.KEY_RESULT);
                String uuidString = bundle.getString(BleConnectService.KEY_UUID_STRING);
                String device_name = bundle.getString(BleConnectService.KEY_DEVICE_NAME);
                Log.d("mBleConnectionReceiver","action: ACTION_WRITE_CHARACTER "+isSuccess+" "+uuidString+" "+device_name);
                if(ADGattUUID.DateTime.toString().compareTo(uuidString) == 0) {
                    if (device_name.contains("UW-302BLE")) {
                        //Need to send disconnect
                        mBleService.disconnectTracker();
                    } else {
                        if(isSuccess) {
                            // Success
                            if(mBleService != null) {
                                mBleService.setupSuccess();
                            }
                        }
                        else {
                        }
                    } //End of ACGS-10
                } else {
                    mBleService.readBufferSizeR();
                }
            } else if(BleConnectService.ACTION_DEVICE_SETUP.equals(action)) {

                boolean isSuccess = bundle.getBoolean(BleConnectService.KEY_RESULT);
                Log.d("mBleConnectionReceiver","action: ACTION_DEVICE_SETUP "+isSuccess);
                if(mBleService != null) {
                    mBleService.disConnectDevice();
                }
                onDevicePairingResult(isSuccess);
            }
        }
    };


    public void initScan(Context context)
    {
        this.context = context;

        // Checks if BlueTooth is unSupported on the device.
        if (!BleConnectService.isEnableBluetoothFunction(context)) {
            //finish();
            Throwable throwable = new IllegalArgumentException("Bluetooth not supported");
            getNavigator().handleError(throwable);
            return;
        }

        context.registerReceiver(mBleConnectionReceiver, BleConnectService.BleConnectionFilter);

        mDeviceScanMode = DEVICE_SCAN_MODE_BP;
        mMainThreadHandler = new Handler();
        doBindService();
    }

    private void doBindService() {

        context.bindService(new Intent(context,
                BleConnectService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsServiceBind = true;
    }

    private void doUnbindService() {
        if (mIsServiceBind) {
            context.unbindService(mConnection);
            mIsServiceBind = false;
        }
    }

    public void onDestroy() {

        if(mBleService != null) {
            mBleService.disConnectDevice();
        }

        context.unregisterReceiver(mBleConnectionReceiver);

        mMainThreadHandler.removeCallbacksAndMessages(null);

        doUnbindService();

        unRegistDeviceBondStateChanged();
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
        }
    }

    private void registDeviceBondStateChanged() {
        if(mDevicePairingReceiver != null) {
            return ;
        }

        mDevicePairingReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                if(!intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                    return;
                }

                Bundle bundle = intent.getExtras();
                if(bundle != null) {
                    int bond_state = intent.getExtras().getInt(BluetoothDevice.EXTRA_BOND_STATE);
                    if(bond_state == BluetoothDevice.BOND_BONDED) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if(mBleService != null) {
                            String device_name = device.getName();
                            if (device_name.contains("UW-302BLE")) {
                                mBleService.reTrackerServiceDiscovery(device.getAddress());
                            } else {
                                mBleService.reConnectDevice(device.getAddress());
                            }



                        }
                    }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        context.registerReceiver(mDevicePairingReceiver, intentFilter);
    }

    private void unRegistDeviceBondStateChanged() {
        if(mDevicePairingReceiver == null) {
            return;
        }

        context.unregisterReceiver(mDevicePairingReceiver);
        mDevicePairingReceiver = null;
    }

    private void onServiceConnected() {
        // OverRide Method
        if(mBleService != null) {
            mBleService.startScanDevices();
        }
    }

    private void onFindConnectDevice(String address) {
        // OverRide Method
        if(mBleService != null) {
            mBleService.stopScanDevice();
        }

        if(!isShowPairing) {
            isShowPairing = true;

            BluetoothDevice device = null;
            device = BleConnectService.getBluetoothDevice(context,address);

            if (device != null) {
                if (mDeviceScanMode == DEVICE_SCAN_MODE_WS) {
                   /* SharedPreferences.Editor editor = getSharedPreferences(
                            "ANDMEDICAL", MODE_PRIVATE).edit();
                    editor.putString("weightdeviceid",
                            "" + device.getUuids());
                    editor.commit();*/
                    Log.d("onFindDevice","DEVICE_SCAN_MODE_WS "+device.getName()+" "+device.getUuids()+" "+device.getAddress());
                }
                else if (mDeviceScanMode == DEVICE_SCAN_MODE_BP) {
                   /* SharedPreferences.Editor editor = getSharedPreferences(
                            "ANDMEDICAL", MODE_PRIVATE).edit();
                    editor.putString("bpdeviceid", "" + device.getUuids());
                    editor.commit();*/

                    Log.d("onFindDevice","DEVICE_SCAN_MODE_BP "+device.getName()+" "+device.getUuids()+" "+device.getAddress());
                    DeviceCharacteristic deviceCharacteristic = new DeviceCharacteristic();
                    deviceCharacteristic.setDeviceName(device.getName());
                    deviceCharacteristic.setDeviceMac(device.getAddress());
                    getNavigator().onDeviceFound(deviceCharacteristic);
                }

                if(mBleService != null) {
                   // mBleService.connectDevice(device.getAddress());
                }
            }
        }
    }

    public void connectToDevice(String deviceName,String mac,String username)
    {
        if(mBleService != null) {
            mBleService.connectDevice(mac);
            BluetoothDevice bluetoothDevice = BleConnectService.getBluetoothDevice(context, mac);
            getRespository().setBPDeviceName(bluetoothDevice.getName());
            getRespository().setBPDeviceAddress(bluetoothDevice.getAddress());
        }
    }


    public void onPairAndConnect(String address)
    {
        BluetoothDevice bluetoothDevice = BleConnectService.getBluetoothDevice(context, address);
        bluetoothDevice.createBond();
    }
    private void onDevicePairingResult(boolean result) {
        // OverRide Method
        if(result ) {
            //ViewGroup dialogLayout = (ViewGroup)findViewById(R.id.dialog_layout);
            //dialogLayout.setVisibility(View.INVISIBLE);

            /*Intent intent = new Intent(InstructionActivity.this,
                    DialogActivity.class);
            intent.putExtra(DialogActivity.INTENT_KEY_TITLE, getResources().getString(R.string.dialog_title_paring_complete));*/

            /*if (mDeviceScanMode == DEVICE_SCAN_MODE_AM_UW) {
                intent.putExtra(DialogActivity.INTENT_KEY_MESSAGE, getResources().getString(R.string.dialog_message_pairing_complete_uw302));
            } else {
                intent.putExtra(DialogActivity.INTENT_KEY_MESSAGE, getResources().getString(R.string.dialog_message_paring_complete));
            }

            startActivityForResult(intent, DialogActivity.REQUEST_CODE);*/
            getNavigator().onDevicePaired();
        }
        else {
            // to do Nothing
        }
    }

    private void onServiceDisconnected() {
        // OverRide Method
    }

}

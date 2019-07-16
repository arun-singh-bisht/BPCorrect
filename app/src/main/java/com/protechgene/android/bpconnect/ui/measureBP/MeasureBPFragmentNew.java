package com.protechgene.android.bpconnect.ui.measureBP;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.Utils.GpsUtils;
import com.protechgene.android.bpconnect.Utils.MathUtil;
import com.protechgene.android.bpconnect.data.ble.Lifetrack_infobean;
import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.ui.ApplicationBPConnect;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.custom.CustomAlertDialog;
import com.skyfishjy.library.RippleBackground;

import butterknife.BindView;
import butterknife.OnClick;
import io.feeeei.circleseekbar.CircleSeekBar;

import static com.protechgene.android.bpconnect.ui.measureBP.MeasureBPFragmentViewModel.BP_DEVICE_MODEL_AND_UA_651BLE;
import static com.protechgene.android.bpconnect.ui.measureBP.MeasureBPFragmentViewModel.BP_DEVICE_MODEL_IHEALTH_BP3L;
import static com.protechgene.android.bpconnect.ui.measureBP.MeasureBPFragmentViewModel.BP_DEVICE_MODEL_TRANSTREK_1491B;


public class MeasureBPFragmentNew extends BaseFragment implements MeasureBPFragmentNavigator {

    public static final String FRAGMENT_TAG = "MeasureBPFragment";

    private MeasureBPFragmentViewModel measureBPFragmentViewModel;
    private  CountDownTimer countDownTimer;

    @BindView(R.id.btn_done)
    TextView doneButton;
    @BindView(R.id.text_bp_reading)
    TextView text_bp_reading;
    @BindView(R.id.blood_pressure_tv)
    TextView blood_pressure_tv;
    @BindView(R.id.text_wait_tv)
    TextView text_wait_tv;
    @BindView(R.id.text_upper)
    TextView text_upper;

    @BindView(R.id.text_heart_rate_reading)
    TextView text_heart_rate_reading;
    @BindView(R.id.text_wait)
    TextView text_wait;

    @BindView(R.id.text_transfer_status)
    TextView text_transfer_status;

    @BindView(R.id.view_wait)
    View view_wait;

    @BindView(R.id.text_counter)
    TextView text_counter;

    @BindView(R.id.image_scanned_device)
    View image_scanned_device;

    @BindView(R.id.text_ihealth_device_name)
    TextView text_ihealth_device_name;

    @BindView(R.id.text_measurement_completed_message)
    TextView text_measurement_completed_message;

    @BindView(R.id.seekbar)
    CircleSeekBar seekbar;
    //@BindView(R.id.view_timer)
    //View view_timer;
    @BindView(R.id.view_reading)
    View view_reading;
    @BindView(R.id.content)
    RippleBackground rippleBackground;


    private boolean isReadingDone = false;
    private boolean isTypeProtocol = false;
    private boolean isCounterRunning = false;
    private boolean shouldInstructionDialogShow = true;
    private int count_protocolReadingAlreadyTaken = 0;
    private int total_protocolReadingTaken = 0;
    private String protocolId;
    private String SELECTED_BP_MODEL;
    @Override
    protected int layoutRes() {
        return R.layout.fragment_measure_bp;
    }

    @Override
    protected void initialize() {
        measureBPFragmentViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(MeasureBPFragmentViewModel.class);
        measureBPFragmentViewModel.setNavigator(this);
        measureBPFragmentViewModel.isReadingForProtocol();
    }

    @Override
    public void isReadingForProtocol_Result(final boolean b,final String protocolCode,final int protocolReadingTaken) {
        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isTypeProtocol = b;
                protocolId = protocolCode;
                total_protocolReadingTaken = protocolReadingTaken;
                showToast(b?"Protocol Reading":"Normal Reading");
                //measureBPFragmentViewModel.connectToDevice(getBaseActivity());
                if(SELECTED_BP_MODEL!=null)
                {
                    if(SELECTED_BP_MODEL.equalsIgnoreCase(BP_DEVICE_MODEL_AND_UA_651BLE))
                    {
                        measureBPFragmentViewModel.connectToDevice(getBaseActivity(),SELECTED_BP_MODEL,isTypeProtocol,protocolId,total_protocolReadingTaken);

                    }else if(SELECTED_BP_MODEL.equalsIgnoreCase(BP_DEVICE_MODEL_IHEALTH_BP3L))
                    {
                        onDeviceConnected_iHealthBP3L(deviceName_BP3L,deviceMac__BP3L);

                    }else if(SELECTED_BP_MODEL.equalsIgnoreCase(BP_DEVICE_MODEL_TRANSTREK_1491B))
                    {
                        measureBPFragmentViewModel.connectToDevice(getBaseActivity(),SELECTED_BP_MODEL,isTypeProtocol,protocolId,total_protocolReadingTaken);
                    }

                }else
                {
                    showBPDevicesMenu();
                }

            }
        });
    }

    private void showBPDevicesMenu()
    {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        builder.setTitle("Select Device");

        // add a list
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.devices_list_dialog_new, null);
       // final String[] gender = {"A&D UA-651BLE", "iHealth BP3L","Transtrek"};
        builder.setView(alertLayout);
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        alertLayout.findViewById(R.id.device_a_d_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SELECTED_BP_MODEL = BP_DEVICE_MODEL_AND_UA_651BLE;
                measureBPFragmentViewModel.connectToDevice(getBaseActivity(),SELECTED_BP_MODEL,isTypeProtocol,protocolId,total_protocolReadingTaken);
                dialog.dismiss();
            }
        });

        alertLayout.findViewById(R.id.device_ihealth_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SELECTED_BP_MODEL = BP_DEVICE_MODEL_IHEALTH_BP3L;
                measureBPFragmentViewModel.connectToDevice(getBaseActivity(),SELECTED_BP_MODEL,isTypeProtocol,protocolId,total_protocolReadingTaken);
                dialog.dismiss();
            }
        });
        alertLayout.findViewById(R.id.device_omron_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        alertLayout.findViewById(R.id.device_trans_prak_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SELECTED_BP_MODEL = BP_DEVICE_MODEL_TRANSTREK_1491B;
                measureBPFragmentViewModel.connectToDevice(getBaseActivity(),SELECTED_BP_MODEL,isTypeProtocol,protocolId,total_protocolReadingTaken);
                dialog.dismiss();
            }
        });
    }


    @Override
    public void turningOnBluetooth() {
        showProgress("Turning On bluetooth...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgress();
                measureBPFragmentViewModel.connectToDevice(getBaseActivity(),SELECTED_BP_MODEL,isTypeProtocol,protocolId,total_protocolReadingTaken);
            }
        },1500);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GpsUtils.GPS_REQUEST)
        {
            if(resultCode == getBaseActivity().RESULT_OK)
                measureBPFragmentViewModel.connectToDevice(getBaseActivity(),SELECTED_BP_MODEL,isTypeProtocol,protocolId,total_protocolReadingTaken);
            else
                FragmentUtil.removeFragment(getBaseActivity());
        }
        else
            measureBPFragmentViewModel.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void AND_DevicePairedStatus(boolean status) {

        if(!ApplicationBPConnect.isBPDeviceRequiredForTesting)
            status = !status;

        if(status)
        {
            startScannaing();
          /*  new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    measureBPFragmentViewModel.saveReading(getRandomReading());
                }
            },1000*30);*/

        }else
        {
            measureBPFragmentViewModel.onDestroy();

            //Show message to user to pair device first
            showAlert("Error", "No device found. Pair to a BP measuring device first.", "OK", new AlertDialogCallback() {
                @Override
                public void onPositiveClick() {
                    //Close this screen.
                    FragmentUtil.removeFragment(getBaseActivity());
                }
            });

            if(rippleBackground!=null)
                rippleBackground.stopRippleAnimation();
            text_transfer_status.setVisibility(View.GONE);

        }
    }

    @Override
    public void onScanningStarted_iHealthBP3L(boolean status) {

        if(status) {
            startScannaing();
        }else
        {
            measureBPFragmentViewModel.onDestroy();
            //Show message to user to pair device first
            showAlert("Error", "Not authorized to access this device.", "OK", new AlertDialogCallback() {
                @Override
                public void onPositiveClick() {
                    //Close this screen.
                    FragmentUtil.removeFragment(getBaseActivity());
                }
            });

            if(rippleBackground!=null)
                rippleBackground.stopRippleAnimation();
            text_transfer_status.setVisibility(View.GONE);
        }
    }

    private void startScannaing()
    {
        if(rippleBackground!=null)
            rippleBackground.startRippleAnimation();

        if(SELECTED_BP_MODEL.equalsIgnoreCase(BP_DEVICE_MODEL_AND_UA_651BLE))
        {
            text_transfer_status.setVisibility(View.VISIBLE);
            text_transfer_status.setText("Searching for data...");

            measureBPFragmentViewModel.onResume(isTypeProtocol,protocolId);

            if(shouldInstructionDialogShow)
                CustomAlertDialog.showInstructionDialog(getBaseActivity());

        }else if(SELECTED_BP_MODEL.equalsIgnoreCase(BP_DEVICE_MODEL_IHEALTH_BP3L))
        {
            text_upper.setVisibility(View.VISIBLE);
            text_upper.setText("Scanning for Device...");
        }else if(SELECTED_BP_MODEL.equalsIgnoreCase(BP_DEVICE_MODEL_TRANSTREK_1491B))
        {
            text_transfer_status.setVisibility(View.VISIBLE);
            text_transfer_status.setText("Searching for data...");
        }

    }

    String deviceName_BP3L;
    String deviceMac__BP3L;
    String deviceType__BP3L;
    @Override
    public void onDeviceFound_iHealthBP3L(String deviceName, String deviceMac, String deviceType) {

        if(deviceName==null || deviceName.isEmpty() || deviceName.equalsIgnoreCase("null"))
        {
            getBaseActivity().showSnakeBar("No Device Found");
            text_upper.setVisibility(View.GONE);
        }else
        {
            this.deviceName_BP3L = deviceName;
            this.deviceMac__BP3L = deviceMac;
            this.deviceType__BP3L = deviceType;

            text_upper.setVisibility(View.VISIBLE);
            text_upper.setText("Tap Devcie to connect");
            image_scanned_device.setVisibility(View.VISIBLE);
            text_ihealth_device_name.setText(deviceName);
        }
    }

    @OnClick(R.id.image_scanned_device)
    public void connectRequest_iHealthBP3L()
    {
        measureBPFragmentViewModel.connectBP3LDevice(deviceName_BP3L,deviceMac__BP3L,deviceType__BP3L);
        text_upper.setVisibility(View.VISIBLE);
        //text_upper.setText("Connecting...");
        text_ihealth_device_name.setText("Connecting...");
    }

    @Override
    public void onDeviceConnected_iHealthBP3L(String deviceName, String deviceMac) {
        text_upper.setVisibility(View.VISIBLE);
        text_upper.setText("Connected to "+deviceName+"\n"+"Press the start button below to measure blood pressure");
        image_scanned_device.setVisibility(View.GONE);
        if(rippleBackground!=null)
            rippleBackground.stopRippleAnimation();

        doneButton.setText("Start");
        doneButton.setVisibility(View.VISIBLE);
        doneButton.setTag("StartBp3LDevice");
    }

    public void startMeasuringBPFromBP3LDevice()
    {
        if(rippleBackground!=null)
            rippleBackground.startRippleAnimation();
        measureBPFragmentViewModel.startMeasuringBPFromBP3LDevice(deviceName_BP3L,deviceMac__BP3L,isTypeProtocol,protocolId,total_protocolReadingTaken);

        doneButton.setText("Stop");
        doneButton.setVisibility(View.VISIBLE);
        doneButton.setTag("StopBp3LDevice");
    }

    public void stopMeasuringBPFromBP3LDevice()
    {
        if(rippleBackground!=null)
            rippleBackground.stopRippleAnimation();
        measureBPFragmentViewModel.stopMeaseureReading();

        doneButton.setText("Done");
        doneButton.setVisibility(View.VISIBLE);
        doneButton.setTag("Done");
    }


    //------------- receive Result from BLE device -----------------------------------------
    @Override
    public void result(final HealthReading healthReading) {
        measureBPFragmentViewModel.onDestroy();

        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress();

                blood_pressure_tv.setVisibility(View.VISIBLE);
                text_bp_reading.setVisibility(View.VISIBLE);
                text_bp_reading.setText(healthReading.getSystolic()+"/"+healthReading.getDiastolic());
                text_heart_rate_reading.setText(healthReading.getPulse());

                count_protocolReadingAlreadyTaken++;

                if(rippleBackground!=null)
                    rippleBackground.stopRippleAnimation();

                if(count_protocolReadingAlreadyTaken<2) {
                    shouldInstructionDialogShow = false;
                    activateCountDown();
                    doneButton.setVisibility(View.GONE);
                }
                else
                {
                    isReadingDone = true;
                    text_transfer_status.setVisibility(View.GONE);
                    text_wait_tv.setVisibility(View.GONE);
                    view_wait.setVisibility(View.GONE);
                    text_upper.setVisibility(View.GONE);
                    doneButton.setText("Done");
                    doneButton.setVisibility(View.VISIBLE);
                    doneButton.setTag("Done");
                    text_measurement_completed_message.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onReadingError(final String msg) {
        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress();

                blood_pressure_tv.setVisibility(View.VISIBLE);
                text_bp_reading.setVisibility(View.VISIBLE);
                text_bp_reading.setText(msg);
                text_heart_rate_reading.setText("-");

                if(rippleBackground!=null)
                    rippleBackground.stopRippleAnimation();

                isReadingDone = true;
                text_transfer_status.setVisibility(View.GONE);
                text_wait_tv.setVisibility(View.GONE);
                view_wait.setVisibility(View.GONE);
                text_upper.setVisibility(View.GONE);
                doneButton.setText("Done");
                doneButton.setVisibility(View.VISIBLE);
                doneButton.setTag("Done");

            }
        });
    }


    private void activateCountDown()
    {
        isCounterRunning = true;
        view_wait.setVisibility(View.VISIBLE);


        countDownTimer = new CountDownTimer(60 * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                text_transfer_status.setVisibility(View.GONE);
                text_counter.setText(millisUntilFinished / 1000 + "");
                seekbar.setCurProcess((int)(millisUntilFinished / 1000));
            }

            public void onFinish() {
                isCounterRunning = false;
                clearReadingData();

                view_wait.setVisibility(View.GONE);
                //startScannaing();
                measureBPFragmentViewModel.isReadingForProtocol();
            }
        }.start();
    }


    //----------------------------------------------------------------------------------------------


    @OnClick(R.id.img_left)
    public void onBackIconClick()
    {
        FragmentUtil.removeFragment(getBaseActivity());
        //measureBPFragmentViewModel.sendDummyReading();
    }

    @OnClick(R.id.btn_done)
    public void onStartButtonClick()
    {
        Object tag = doneButton.getTag();
        if(tag!=null)
        {
            String t = (String)tag;
            if(t.equalsIgnoreCase("StartBp3LDevice"))
            {
                startMeasuringBPFromBP3LDevice();
            }else if(t.equalsIgnoreCase("StopBp3LDevice"))
            {
                stopMeasuringBPFromBP3LDevice();
            }else if(t.equalsIgnoreCase("Done"))
            {
                //Close Screen
                measureBPFragmentViewModel.disconnectFromBP3LDevice();
                FragmentUtil.removeFragment(getBaseActivity());
            }
        }else
        {
            //Close Screen
            FragmentUtil.removeFragment(getBaseActivity());
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        measureBPFragmentViewModel.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        measureBPFragmentViewModel.disconnectFromBP3LDevice();
        measureBPFragmentViewModel.onDestroy();
        if(rippleBackground!=null)
            rippleBackground.stopRippleAnimation();
        if(countDownTimer!=null)
            countDownTimer.cancel();
        countDownTimer = null;
    }

    //-------------- Show Progress Message ---------------------------------------
    @Override
    public void setIndicatorMessage(String message) {
        if(message==null)
            message = "Test";

        showIndicator(message);
    }

    @Override
    public void showIndicator(String message) {
        //hideProgress();
        //showProgress(message);
        text_transfer_status.setVisibility(View.VISIBLE);
        text_transfer_status.setText(message);
    }

    @Override
    public void dismissIndicator() {
        hideProgress();
    }



    @Override
    public void handleError(Throwable throwable) {
        hideProgress();
        getBaseActivity().showSnakeBar(throwable.getMessage());
    }

    private void clearReadingData()
    {
        text_bp_reading.setText("0/0");
        text_heart_rate_reading.setText("0");

        blood_pressure_tv.setVisibility(View.GONE);
        text_bp_reading.setVisibility(View.GONE);
    }



    private Lifetrack_infobean getRandomReading()
    {
        Lifetrack_infobean lifetrack_infobean = new Lifetrack_infobean();
        lifetrack_infobean.setSystolic(MathUtil.getRandomNumber(110,150)+"");
        lifetrack_infobean.setDiastolic(MathUtil.getRandomNumber(90,130)+"");
        lifetrack_infobean.setPulse(MathUtil.getRandomNumber(60,99)+"");
        return lifetrack_infobean;
    }

}

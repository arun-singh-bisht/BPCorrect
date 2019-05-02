package com.protechgene.android.bpconnect.ui.measureBP;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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

import butterknife.BindView;
import butterknife.OnClick;
import io.feeeei.circleseekbar.CircleSeekBar;

import static com.protechgene.android.bpconnect.ui.ApplicationBPConnect.isReadingTakenFromActualDevice;

public class MeasureBPFragment extends BaseFragment implements MeasureBPFragmentNavigator {

    public static final String FRAGMENT_TAG = "MeasureBPFragment";

    private MeasureBPFragmentViewModel measureBPFragmentViewModel;
    private  CountDownTimer countDownTimer;

    @BindView(R.id.btn_start)
    TextView startButton;
    @BindView(R.id.text_bp_reading)
    TextView text_bp_reading;
    @BindView(R.id.text_heart_rate_reading)
    TextView text_heart_rate_reading;
    @BindView(R.id.text_wait)
    TextView text_wait;
    //@BindView(R.id.seekbar)
    //CircleSeekBar seekbar;
    //@BindView(R.id.view_timer)
    //View view_timer;
    @BindView(R.id.view_reading)
    View view_reading;


    private boolean isReadingDone = false;
    private boolean isTypeProtocol = false;
    private boolean isCounterRunning = false;
    private int numberOfReadings = 0;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_measure_bp;
    }

    @Override
    protected void initialize() {
        measureBPFragmentViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(MeasureBPFragmentViewModel.class);
        measureBPFragmentViewModel.setNavigator(this);
        measureBPFragmentViewModel.connectToDevice(getBaseActivity());

        Bundle args = getArguments();
        if(args!=null)
            isTypeProtocol = args.getBoolean("isTypeProtocol");

        //isTypeProtocol = true;

    }

    @OnClick(R.id.img_left)
    public void onBackIconClick()
    {
        FragmentUtil.removeFragment(getBaseActivity());
    }

    @OnClick(R.id.btn_start)
    public void onStartButtonClick()
    {
        if(isCounterRunning)
            return;

        if(!isReadingDone) {

            if(isReadingTakenFromActualDevice)
            {
                measureBPFragmentViewModel.onResume();
                showProgress("Wait...");
            }else
            {
                Lifetrack_infobean lifetrackInfobean = new Lifetrack_infobean();
                lifetrackInfobean.setSystolic(MathUtil.getRandomNumber(80,130)+"");
                lifetrackInfobean.setDiastolic(MathUtil.getRandomNumber(60,90)+"");
                lifetrackInfobean.setPulse(MathUtil.getRandomNumber(70,100)+"");
                lifetrackInfobean.setDateTimeStamp((System.currentTimeMillis()/1000)+"");
                measureBPFragmentViewModel.saveReading(lifetrackInfobean);
            }
        }else {
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
        measureBPFragmentViewModel.onDestroy();
    }


    @Override
    public void turningOnBluetooth() {
        showProgress("Turning On bluetooth...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgress();
                measureBPFragmentViewModel.connectToDevice(getBaseActivity());
            }
        },1500);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GpsUtils.GPS_REQUEST)
        {
            if(resultCode == getBaseActivity().RESULT_OK)
                measureBPFragmentViewModel.connectToDevice(getBaseActivity());
            else
                FragmentUtil.removeFragment(getBaseActivity());
        }
        else
            measureBPFragmentViewModel.onActivityResult(requestCode,resultCode,data);

        Log.d("onActivityResult",""+requestCode+" "+resultCode);
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
        hideProgress();
        showProgress(message);
    }

    @Override
    public void dismissIndicator() {
        hideProgress();
    }

    //------------- receive Result from BLE device -----------------------------------------
    @Override
    public void result(final HealthReading healthReading) {

        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress();
                text_bp_reading.setText(healthReading.getSystolic()+"/"+healthReading.getDiastolic());
                text_heart_rate_reading.setText(healthReading.getPulse());
                numberOfReadings = numberOfReadings+1;

                if(isTypeProtocol && numberOfReadings<2)
                    activateCountDown();
                else
                {
                    isReadingDone = true;
                    startButton.setText("DONE");
                }
            }
        });

    }

    @Override
    public void handleError(Throwable throwable) {
        hideProgress();
        getBaseActivity().showSnakeBar(throwable.getMessage());
    }

    @Override
    public void bpDevicePairedStatus(boolean status) {

        if(!ApplicationBPConnect.isBPDeviceRequiredForTesting)
            status = !status;

        if(status)
        {
            getBaseActivity().showSnakeBar("No BP Device Found");
            //activate 'Start' button
            startButton.setVisibility(View.VISIBLE);
        }else
        {
            startButton.setVisibility(View.GONE);
            //Show message to user to pair device first
            showAlert("Error", "No device found. Pair to a BP measuring device first.", "OK", new AlertDialogCallback() {
                @Override
                public void onPositiveClick() {
                    //Close this screen.
                    FragmentUtil.removeFragment(getBaseActivity());
                }
            });
        }
    }

    private void clearReadingData()
    {
        text_bp_reading.setText("0/0");
        text_heart_rate_reading.setText("0");
    }

    private void activateCountDown()
    {
        isCounterRunning = true;
        text_wait.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(60 * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                startButton.setText(millisUntilFinished / 1000 + "\nseconds");
            }

            public void onFinish() {
                isCounterRunning = false;
                startButton.setText("START");
                text_wait.setVisibility(View.GONE);
                clearReadingData();
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(countDownTimer!=null)
            countDownTimer.cancel();
        countDownTimer = null;

    }
}

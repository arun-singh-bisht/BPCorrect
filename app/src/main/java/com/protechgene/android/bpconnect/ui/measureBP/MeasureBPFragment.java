package com.protechgene.android.bpconnect.ui.measureBP;

import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.data.ble.Lifetrack_infobean;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;

import butterknife.BindView;
import butterknife.OnClick;

public class MeasureBPFragment extends BaseFragment implements MeasureBPFragmentNavigator {

    public static final String FRAGMENT_TAG = "MeasureBPFragment";

    private MeasureBPFragmentViewModel measureBPFragmentViewModel;

    @BindView(R.id.btn_start)
    View startButton;

    @BindView(R.id.text_bp_reading)
    TextView text_bp_reading;
    @BindView(R.id.text_heart_rate_reading)
    TextView text_heart_rate_reading;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_measure_bp;
    }

    @Override
    protected void initialize() {
        measureBPFragmentViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(MeasureBPFragmentViewModel.class);
        measureBPFragmentViewModel.setNavigator(this);
        measureBPFragmentViewModel.connectToDevice(getBaseActivity());
    }

    @OnClick(R.id.btn_start)
    public void onStartButtonClick()
    {
        measureBPFragmentViewModel.onResume();
    }

    @Override
    public void onResume() {
        super.onResume();
        //measureBPFragmentViewModel.onResume();
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
    public void tunrOnBluetooth() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, measureBPFragmentViewModel.REQUEST_ENABLE_BLUETOOTH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        measureBPFragmentViewModel.onActivityResult(requestCode,resultCode,data);
    }

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

    @Override
    public void result(Lifetrack_infobean lifetrackInfobean) {
        text_bp_reading.setText(lifetrackInfobean.getSystolic()+"/"+lifetrackInfobean.getDiastolic());
        text_heart_rate_reading.setText(lifetrackInfobean.getPulse());
    }

    @Override
    public void handleError(Throwable throwable) {
        getBaseActivity().showSnakeBar(throwable.getMessage());
    }

    @Override
    public void bpDevicePairedStatus(boolean status) {
        if(status)
        {
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
}

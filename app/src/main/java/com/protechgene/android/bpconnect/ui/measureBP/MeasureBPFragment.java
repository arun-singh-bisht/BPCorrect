package com.protechgene.android.bpconnect.ui.measureBP;

import android.arch.lifecycle.ViewModelProviders;
import android.view.View;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.profile.ProfileFragmentViewModel;

public class MeasureBPFragment extends BaseFragment implements MeasureBPFragmentNavigator {

    public static final String FRAGMENT_TAG = "MeasureBPFragment";

    private MeasureBPFragmentViewModel measureBPFragmentViewModel;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_measure_bp;
    }

    @Override
    protected void initialize() {
        measureBPFragmentViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(MeasureBPFragmentViewModel.class);
        measureBPFragmentViewModel.setNavigator(this);
    }

    @Override
    public void handleError(Throwable throwable) {

    }

    @Override
    public void connectedToDevice() {
        getBaseActivity().showSnakeBar("Connected To Device");
    }

    @Override
    public void disconnectedFromDevice() {
        getBaseActivity().showSnakeBar("Disconnected From Device");
    }
}

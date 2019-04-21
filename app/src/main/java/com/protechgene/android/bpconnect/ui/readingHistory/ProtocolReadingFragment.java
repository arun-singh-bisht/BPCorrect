package com.protechgene.android.bpconnect.ui.readingHistory;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;
import com.protechgene.android.bpconnect.data.local.models.BPReadingModel;
import com.protechgene.android.bpconnect.ui.adapters.BPReadingAdapter;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ProtocolReadingFragment extends BaseFragment implements ProtocolReadingsFragmentNavigator{


    public static final String FRAGMENT_TAG = "ProtocolReadingFragment";

    @BindView(R.id.recycler_view)
    public RecyclerView recyclerView;

    private ProtocolReadingsViewModel protocolReadingsViewModel;
    private BPReadingAdapter bpReadingAdapter;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_bp_protocol_readings;
    }

    @Override
    protected void initialize() {
        protocolReadingsViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(ProtocolReadingsViewModel.class);
        protocolReadingsViewModel.setNavigator(this);
        initView();
    }

    private void initView()
    {

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        bpReadingAdapter = new BPReadingAdapter(getActivity(),new ArrayList<BPReadingModel>());
        bpReadingAdapter.setData(BPReadingModel.getData());
        recyclerView.setAdapter(bpReadingAdapter);

        protocolReadingsViewModel.checkActiveProtocol();

    }

    @Override
    public void handleError(Throwable throwable) {

    }

    @Override
    public void isProtocolExists(boolean status, ProtocolModel protocolModel) {

        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BPReadingFragment bpReadingFragment = (BPReadingFragment)getParentFragment();
                if(status)
                {
                    bpReadingFragment.setRightIcon(R.drawable.ic_action_alarm);
                }else
                {
                    bpReadingFragment.setRightIcon(R.drawable.ic_action_add_simple);
                }
            }
        });

    }

    @Override
    public void showReadingData(List<HealthReading> valueList) {

    }
}

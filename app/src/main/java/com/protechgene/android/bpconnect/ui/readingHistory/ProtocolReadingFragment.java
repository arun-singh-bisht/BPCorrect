package com.protechgene.android.bpconnect.ui.readingHistory;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.data.local.models.BPReadingModel;
import com.protechgene.android.bpconnect.ui.adapters.BPReadingAdapter;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;

import java.util.ArrayList;

import butterknife.BindView;

public class ProtocolReadingFragment extends BaseFragment {


    public static final String FRAGMENT_TAG = "ProtocolReadingFragment";

    @BindView(R.id.recycler_view)
    public RecyclerView recyclerView;


    private BPReadingAdapter bpReadingAdapter;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_bp_protocol_readings;
    }

    @Override
    protected void initialize() {
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

    }

}

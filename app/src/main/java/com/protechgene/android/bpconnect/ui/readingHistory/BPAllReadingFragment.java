package com.protechgene.android.bpconnect.ui.readingHistory;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.ui.adapters.ReadingAdapter;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

public class BPAllReadingFragment extends BaseFragment implements BPAllReadingsFragmentNavigator {

    public static final String FRAGMENT_TAG = "BPAllReadingFragment";
    private ReadingAdapter bpReadingAdapter;
    private BpReadingsViewModel bpReadingsViewModel;

    @BindView(R.id.recycler_view)
    public RecyclerView recyclerView;
    @BindView(R.id.text_empty_msg)
    public TextView text_empty_msg;



    @Override
    protected int layoutRes() {
        return R.layout.fragment_bp_all_readings;
    }

    @Override
    protected void initialize() {
        bpReadingsViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(BpReadingsViewModel.class);
        bpReadingsViewModel.setNavigator(this);
        initView();
    }

    private void initView()
    {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        bpReadingAdapter = new ReadingAdapter(getActivity(), new ArrayList<>());
        recyclerView.setAdapter(bpReadingAdapter);

        showProgress("Loading...");
        bpReadingsViewModel.getBpReadings();

    }

    @Override
    public void showReadingData(List<HealthReading> actualValues) {

        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress();

                if(actualValues.size()>0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    text_empty_msg.setVisibility(View.GONE);

                    Collections.reverse(actualValues);
                    bpReadingAdapter.setData(actualValues,true);
                }else
                {
                    recyclerView.setVisibility(View.GONE);
                    text_empty_msg.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void handleError(Throwable throwable) {
        hideProgress();
        getBaseActivity().showSnakeBar(throwable.getMessage());
    }
}

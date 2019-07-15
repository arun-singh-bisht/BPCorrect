package com.protechgene.android.bpconnect.ui.reminder;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.ui.adapters.ProtocolsListAdapter;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.data.remote.responseModels.protocol.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

public class HistoryProtocolsFragment extends BaseFragment implements HistoryProtocolsFragmentNavigator {

    public static final String FRAGMENT_TAG = "HistoryProtocolsFragment";
    private ProtocolsListAdapter protocolsListAdapter;
    private HistoryProtocolsViewModel historyProtocolsViewModel;

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
        historyProtocolsViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(HistoryProtocolsViewModel.class);
        historyProtocolsViewModel.setNavigator(this);
        initView();
    }

    private void initView()
    {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        protocolsListAdapter = new ProtocolsListAdapter(getActivity(), new ArrayList<>());
        recyclerView.setAdapter(protocolsListAdapter);

        showProgress("Loading...");
        historyProtocolsViewModel.getHistoryProtocols();

    }


    @Override
    public void handleError(Throwable throwable) {
        hideProgress();
        getBaseActivity().showSnakeBar(throwable.getMessage());
    }

    @Override
    public void showData(List<Data> valueList) {
        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress();

                if(valueList.size()>0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    text_empty_msg.setVisibility(View.GONE);

                    Collections.reverse(valueList);
                    protocolsListAdapter.setData(valueList);
                }else
                {
                    recyclerView.setVisibility(View.GONE);
                    text_empty_msg.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}

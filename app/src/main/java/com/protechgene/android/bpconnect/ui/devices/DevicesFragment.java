package com.protechgene.android.bpconnect.ui.devices;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.data.local.models.TutorialModel;
import com.protechgene.android.bpconnect.ui.adapters.DevicesAdapter;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class DevicesFragment extends BaseFragment {

    public static final String FRAGMENT_TAG = "DevicesFragment";
    private DevicesAdapter bpReadingAdapter;

    @BindView(R.id.txt_title)
    TextView title;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_tutorial;
    }

    @Override
    protected void initialize() {
        initView();
    }

    @OnClick(R.id.img_right)
    public void onAddIconClick()
    {
        FragmentUtil.loadFragment(getActivity(),R.id.container_fragment,new PairNewDevicesFragment(),PairNewDevicesFragment.FRAGMENT_TAG,"PairNewDevicesFragmentTransition");
    }

    private void initView()
    {
        title.setText("Devices");

        getView().findViewById(R.id.img_right).setVisibility(View.VISIBLE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        bpReadingAdapter = new DevicesAdapter(new ArrayList<TutorialModel>());
        bpReadingAdapter.setData(TutorialModel.getData());
        recyclerView.setAdapter(bpReadingAdapter);

    }

}

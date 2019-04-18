package com.protechgene.android.bpconnect.ui.tutorial;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.data.local.models.TutorialModel;
import com.protechgene.android.bpconnect.ui.adapters.TutorialAdapter;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class TutorialFragment extends BaseFragment {

    public static final String FRAGMENT_TAG = "TutorialFragment";

    @BindView(R.id.txt_title)
    TextView txt_title;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    private TutorialAdapter bpReadingAdapter;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_tutorial;
    }

    @Override
    protected void initialize() {
        initView();
    }

    @OnClick(R.id.img_left)
    public void onBackIconClick()
    {
        FragmentUtil.removeFragment(getBaseActivity());
    }

    private void initView()
    {
        txt_title.setText("Learn");

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        bpReadingAdapter = new TutorialAdapter(new ArrayList<TutorialModel>());
        bpReadingAdapter.setData(TutorialModel.getData());
        recyclerView.setAdapter(bpReadingAdapter);
    }

}

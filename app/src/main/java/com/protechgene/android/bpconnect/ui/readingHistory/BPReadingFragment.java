package com.protechgene.android.bpconnect.ui.readingHistory;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.adapters.ViewPagerAdapter;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class BPReadingFragment extends BaseFragment {

    public static final String FRAGMENT_TAG = "BPReadingFragment";

    @BindView(R.id.tabs)
    public TabLayout tabLayout;

    @BindView(R.id.viewpager)
    public ViewPager viewPager;

    @BindView(R.id.txt_title)
    public TextView txt_title;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_bp_readings;
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
        txt_title.setText("Blood Pressure Readings");
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new BPAllReadingFragment(), "ALL");
        adapter.addFragment(new ProtocolReadingFragment(), "Scheduled");
        viewPager.setAdapter(adapter);
    }
}

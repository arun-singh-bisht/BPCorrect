package com.protechgene.android.bpconnect.ui.reminder;

import android.arch.lifecycle.ViewModelProviders;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.DateUtils;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.adapters.ViewPagerAdapter;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.readingHistory.BPAllReadingFragment;
import com.protechgene.android.bpconnect.ui.reminder.ActiveProtocolFragment;
import com.protechgene.android.bpconnect.ui.settings.SettingFragmentViewModel;

import butterknife.BindView;
import butterknife.OnClick;

public class ReminderFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    public static final String FRAGMENT_TAG = "ReminderFragment";
    private  ActiveProtocolFragment activeProtocolFragment;

    @BindView(R.id.tabs)
    public TabLayout tabLayout;

    @BindView(R.id.viewpager)
    public ViewPager viewPager;

    @BindView(R.id.txt_title)
    public TextView txt_title;

    @BindView(R.id.img_right)
    public ImageView img_right;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_bp_readings;
    }

    @Override
    protected void initialize() {
        //activeProtocolFragment = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(ActiveProtocolFragment.class);
        initView();
    }

    private void initView()
    {
        txt_title.setText("Manage your BPCorrect Reminders");

        setupViewPager(viewPager);
        //  tabLayout.setupWithViewPager(viewPager);
        img_right.setVisibility(View.GONE);
    }

    @OnClick(R.id.img_left)
    public void onBackIconClick()
    {
        FragmentUtil.removeFragment(getBaseActivity());
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new ActiveProtocolFragment(), "Active Protocol");
        //  adapter.addFragment(new HistoryProtocolsFragment(), "History Protocols");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("onPageSelected","position: "+position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void setRightIcon(int res_id)
    {
        img_right.setImageResource(res_id);
        img_right.setTag(res_id);
    }
}

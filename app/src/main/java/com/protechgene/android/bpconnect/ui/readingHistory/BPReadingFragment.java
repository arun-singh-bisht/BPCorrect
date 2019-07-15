package com.protechgene.android.bpconnect.ui.readingHistory;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.adapters.ViewPagerAdapter;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.reminder.ReminderFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class BPReadingFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    public static final String FRAGMENT_TAG = "BPReadingFragment";

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
        initView();
    }

    private void initView()
    {
        txt_title.setText("My Blood Pressure Readings");
        img_right.setImageResource(R.drawable.ic_action_add_simple);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        img_right.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.img_left)
    public void onBackIconClick()
    {
        FragmentUtil.removeFragment(getBaseActivity());
    }

    @OnClick(R.id.img_right)
    public void onAddIconClick()
    {
        FragmentUtil.loadFragment(getBaseActivity(),R.id.container_fragment,new ReminderFragment(),ReminderFragment.FRAGMENT_TAG,"ReminderFragmentTransition");
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new ProtocolReadingFragment(), "BPCorrect Readings");
        adapter.addFragment(new BPAllReadingFragment(), "ALL");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("onPageSelected","position: "+position);
        if(position==0)
            img_right.setVisibility(View.VISIBLE);
        else if(position==1)
            img_right.setVisibility(View.GONE);
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

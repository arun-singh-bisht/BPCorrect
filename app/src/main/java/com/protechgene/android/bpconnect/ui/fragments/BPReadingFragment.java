package com.protechgene.android.bpconnect.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.data.local.models.BPReadingModel;
import com.protechgene.android.bpconnect.ui.adapters.BPReadingAdapter;
import com.protechgene.android.bpconnect.ui.adapters.ViewPagerAdapter;

import java.util.ArrayList;

public class BPReadingFragment extends Fragment {

    public static final String FRAGMENT_TAG = "BPReadingFragment";
    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BPReadingAdapter bpReadingAdapter;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(view==null)
        {
            view = inflater.inflate(R.layout.fragment_bp_readings,container,false);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView()
    {
        TextView txt_title =  view.findViewById(R.id.txt_title);
        txt_title.setText("Blood Pressure Readings");

        viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new BPAllReadingFragment(), "ALL");
        adapter.addFragment(new ProtocolReadingFragment(), "Scheduled");
        viewPager.setAdapter(adapter);
    }
}

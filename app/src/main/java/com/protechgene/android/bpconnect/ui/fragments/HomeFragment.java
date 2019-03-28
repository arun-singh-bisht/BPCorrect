package com.protechgene.android.bpconnect.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.ui.activites.MainActivity;

public class HomeFragment extends Fragment implements View.OnClickListener {

    public static final String FRAGMENT_TAG = "HomeFragment";
    private View view;
    private MainActivity mainActivity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
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
            view = inflater.inflate(R.layout.fragment_home,container,false);
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
        view.findViewById(R.id.card_measure_bp).setOnClickListener(this);
        view.findViewById(R.id.card_readings).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.card_measure_bp : {
                FragmentUtil.loadFragment(mainActivity,R.id.container_fragment,new MeasureBPFragment(),MeasureBPFragment.FRAGMENT_TAG,"MeasureBPFragmentTransition");
            }
            break;
            case R.id.card_readings : {
                FragmentUtil.loadFragment(mainActivity,R.id.container_fragment,new BPReadingFragment(),BPReadingFragment.FRAGMENT_TAG,"BPReadingFragmentTransition");
            }
            break;
        }
    }
}

package com.protechgene.android.bpconnect.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.data.local.models.TutorialModel;
import com.protechgene.android.bpconnect.ui.adapters.DevicesAdapter;
import com.protechgene.android.bpconnect.ui.adapters.TutorialAdapter;

import java.util.ArrayList;

public class DevicesFragment extends Fragment implements View.OnClickListener {

    public static final String FRAGMENT_TAG = "DevicesFragment";
    private View view;
    private DevicesAdapter bpReadingAdapter;
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
            view = inflater.inflate(R.layout.fragment_tutorial,container,false);
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
        txt_title.setText("Devices");

        view.findViewById(R.id.img_right).setVisibility(View.VISIBLE);
        view.findViewById(R.id.img_right).setOnClickListener(this);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        bpReadingAdapter = new DevicesAdapter(new ArrayList<TutorialModel>());
        bpReadingAdapter.setData(TutorialModel.getData());
        recyclerView.setAdapter(bpReadingAdapter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.img_right:{
                FragmentUtil.loadFragment(getActivity(),R.id.container_fragment,new PairNewDevicesFragment(),PairNewDevicesFragment.FRAGMENT_TAG,"PairNewDevicesFragmentTransition");
            }
            break;
        }
    }
}

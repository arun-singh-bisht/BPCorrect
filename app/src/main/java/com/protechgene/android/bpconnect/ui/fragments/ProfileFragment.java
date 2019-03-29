package com.protechgene.android.bpconnect.ui.fragments;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.FragmentUtil;
import com.protechgene.android.bpconnect.data.local.models.TutorialModel;
import com.protechgene.android.bpconnect.ui.adapters.DevicesAdapter;

import java.util.ArrayList;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    public static final String FRAGMENT_TAG = "ProfileFragment";
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
            view = inflater.inflate(R.layout.fragment_profile,container,false);
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
        view.findViewById(R.id.root_header).setBackgroundColor(Color.parseColor("#00000000"));
        ((ImageView)view.findViewById(R.id.img_left)).setColorFilter(Color.argb(255, 255, 255, 255));
        ((ImageView)view.findViewById(R.id.img_right)).setColorFilter(Color.argb(255, 255, 255, 255));
        ((ImageView)view.findViewById(R.id.img_right)).setImageResource(R.drawable.ic_action_edit);

        TextView txt_title =  view.findViewById(R.id.txt_title);
        txt_title.setText("Profile");
        txt_title.setTextColor(Color.parseColor("#ffffff"));

        view.findViewById(R.id.img_right).setVisibility(View.VISIBLE);
        view.findViewById(R.id.img_right).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.img_right:{
                FragmentUtil.loadFragment(getActivity(),R.id.container_fragment,new ProfileEditFragment(),ProfileEditFragment.FRAGMENT_TAG,"ProfileEditFragmentTransition");
            }
            break;
        }
    }
}

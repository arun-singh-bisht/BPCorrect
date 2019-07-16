package com.protechgene.android.bpconnect.ui.tutorial;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

    @BindView(R.id.text_instruction_msg)
    TextView text_instruction_msg;



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
        text_instruction_msg.setVisibility(View.GONE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        TutorialAdapter.OnItemClickListener listener = (position) -> {
            Toast.makeText(getContext(), "Position " + position, Toast.LENGTH_SHORT).show();
        };
        bpReadingAdapter = new TutorialAdapter(new ArrayList<TutorialModel>(),listener,getActivity());
        bpReadingAdapter.setData(TutorialModel.getData());
        recyclerView.setAdapter(bpReadingAdapter);


       /* recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), bpReadingAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }



}

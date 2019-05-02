package com.protechgene.android.bpconnect.ui.readingHistory;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;
import com.protechgene.android.bpconnect.ui.adapters.ReadingAdapter;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.feeeei.circleseekbar.CircleSeekBar;

public class ProtocolReadingFragment extends BaseFragment implements ProtocolReadingsFragmentNavigator{


    public static final String FRAGMENT_TAG = "ProtocolReadingFragment";

    @BindView(R.id.recycler_view)
    public RecyclerView recyclerView;
    @BindView(R.id.view_protocol_readings)
    public View view_protocol_readings;

    @BindView(R.id.text_sys_dia)
    public TextView text_sys_dia;
    @BindView(R.id.text_pulse)
    public TextView text_pulse;
    @BindView(R.id.text_total_readings_value)
    public TextView text_total_readings_value;
    @BindView(R.id.text_reading_taken_value)
    public TextView text_reading_taken;
    @BindView(R.id.text_reading_missed_value)
    public TextView text_reading_missed;
    @BindView(R.id.text_protocol_date)
    public TextView text_protocol_date;

    @BindView(R.id.seekbar)
    public CircleSeekBar seekbar;
    @BindView(R.id.text_progress_percentage)
    public TextView text_progress_percentage;




    private ProtocolReadingsViewModel protocolReadingsViewModel;
    private ReadingAdapter bpReadingAdapter;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_bp_protocol_readings;
    }

    @Override
    protected void initialize() {
        protocolReadingsViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getBaseActivity().getApplication())).get(ProtocolReadingsViewModel.class);
        protocolReadingsViewModel.setNavigator(this);
        initView();
    }

    private void initView()
    {

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        bpReadingAdapter = new ReadingAdapter(getActivity(), new ArrayList<>());
        recyclerView.setAdapter(bpReadingAdapter);

        protocolReadingsViewModel.checkActiveProtocol();
    }

    @Override
    public void handleError(final Throwable throwable) {
        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getBaseActivity().showSnakeBar(throwable.getMessage());
            }
        });
    }

    @Override
    public void isProtocolExists(boolean status, final ProtocolModel protocolModel) {

        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BPReadingFragment bpReadingFragment = (BPReadingFragment)getParentFragment();
                if(status)
                {
                    bpReadingFragment.setRightIcon(R.drawable.ic_action_alarm);
                    view_protocol_readings.setVisibility(View.VISIBLE);
                    text_protocol_date.setText(protocolModel.getStartDay()+" - "+protocolModel.getEndDay());
                    protocolReadingsViewModel.getBpReadings();
                }else
                {
                    bpReadingFragment.setRightIcon(R.drawable.ic_action_add_simple);
                    view_protocol_readings.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void showReadingData(final List<HealthReading> valueList) {
        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(valueList!=null && valueList.size()>0)
                    bpReadingAdapter.setData(valueList,false);
            }
        });
    }

    @Override
    public void showSummeyData(int avgSys, int avgDia, int avgPulse, int totalReadings, int taken, int missed) {
        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                text_sys_dia.setText(avgSys+"/"+avgDia);
                text_pulse.setText(avgPulse+"");
                text_total_readings_value.setText(totalReadings+"");
                text_reading_taken.setText(taken+"");
                text_reading_missed.setText(missed+"");

                seekbar.setCurProcess(taken);
                text_progress_percentage.setText((taken*100)/totalReadings +"%");
            }
        });
    }

}

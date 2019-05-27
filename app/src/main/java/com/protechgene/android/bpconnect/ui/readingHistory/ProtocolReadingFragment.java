package com.protechgene.android.bpconnect.ui.readingHistory;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import com.protechgene.android.bpconnect.data.local.db.models.ProtocolModel;
import com.protechgene.android.bpconnect.ui.adapters.ReadingAdapter;
import com.protechgene.android.bpconnect.ui.base.BaseFragment;
import com.protechgene.android.bpconnect.ui.base.ViewModelFactory;
import com.protechgene.android.bpconnect.ui.custom.CustomAlertDialog;

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

    @BindView(R.id.text_empty_msg)
    public TextView text_empty_msg;

    @BindView(R.id.text_blood_pressure)
    public TextView text_blood_pressure;
    @BindView(R.id.text_sys_dia_title)
    public TextView text_sys_dia_title;
    @BindView(R.id.text_bpm)
    public TextView text_bpm;

    @BindView(R.id.card_reading_stats)
    public CardView card_reading_stats;

    @BindView(R.id.text_sys_dia)
    public TextView text_sys_dia;
    @BindView(R.id.text_pulse)
    public TextView text_pulse;
//    @BindView(R.id.text_total_readings_value)
//    public TextView text_total_readings_value;
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

    @BindView(R.id.image_info)
    public ImageView image_info;

    @BindView(R.id.view_average_statics)
    public View view_average_statics;

    @BindView(R.id.view_no_average_message)
    public View view_no_average_message;


    private ProtocolReadingsViewModel protocolReadingsViewModel;
    private ReadingAdapter bpReadingAdapter;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_bp_protocol_readings_new;
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
                    text_empty_msg.setVisibility(View.GONE);

                    text_protocol_date.setText(protocolModel.getStartDay()+" - "+protocolModel.getEndDay());
                    protocolReadingsViewModel.getBpReadings();
                }else
                {
                    bpReadingFragment.setRightIcon(R.drawable.ic_action_add_simple);
                    view_protocol_readings.setVisibility(View.GONE);
                    text_empty_msg.setVisibility(View.VISIBLE);
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
            //    text_total_readings_value.setText(totalReadings+"");
                text_reading_taken.setText(taken+"  of  28");
                text_reading_missed.setText(missed+"  of 28");

                seekbar.setCurProcess(taken);
                text_progress_percentage.setText((taken*100)/totalReadings +"%");

                int sys = avgSys;
                int dia = avgDia;
                int color = 0;

                int stage =0;
                String bp_stage_name ="";

                if(sys==0 && dia==0) {
                    view_average_statics.setVisibility(View.GONE);
                    view_no_average_message.setVisibility(View.VISIBLE);
                    return;
                }

                view_average_statics.setVisibility(View.VISIBLE);
                view_no_average_message.setVisibility(View.GONE);

                if(sys<120 && dia<80)
                {
                    color = R.color.reading_normal_green;
                    stage = R.string.bp_normal;
                    bp_stage_name = "Normal";
                }else if(sys<130 && dia<80)
                {
                    color = R.color.reading_elevated;
                    stage = R.string.bp_elevated;
                    bp_stage_name = "Elevated";
                }else if(sys<140 || dia<90)
                {
                    color = R.color.reading_hyper_stage_first;
                    stage = R.string.bp_hypertension_stage_1;
                    bp_stage_name = "Hypertension Stage 1";
                }else if(sys<180 || dia<120)
                {
                    color = R.color.reading_hyper_stage_second;
                    stage = R.string.bp_hypertension_stage_2;
                    bp_stage_name = "Hypertension Stage 2";
                }else
                {
                    color = R.color.reading_hyper_stage_crisis;
                    stage = R.string.bp_hypertension_crisis;
                    bp_stage_name = "Hypertensive Crisis";
                }
                setCardBgColor(color);
                setInfoMessage(stage,bp_stage_name);
            }
        });
    }

    private void setCardBgColor(int color)
    {
        card_reading_stats.setCardBackgroundColor(getResources().getColor(color));

        int label_color;
        if(color == R.color.reading_normal_green || color == R.color.reading_hyper_stage_second || color==R.color.reading_hyper_stage_crisis)
            label_color = Color.WHITE;
        else
            label_color = Color.BLACK;


        text_blood_pressure.setTextColor(label_color);
        text_sys_dia.setTextColor(label_color);
        text_sys_dia_title.setTextColor(label_color);
        text_pulse.setTextColor(label_color);
        text_bpm.setTextColor(label_color);
    }

    private void setInfoMessage(int stage_res,String title)
    {
        image_info.setVisibility(View.VISIBLE);
        image_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(stage_res==0)
                    return;

                String mystring = getBaseActivity().getResources().getString(stage_res);
                CustomAlertDialog.showDefaultDialog(getBaseActivity(),title,mystring);
            }
        });
    }

}

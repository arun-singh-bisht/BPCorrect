package com.protechgene.android.bpconnect.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.Utils.DateUtils;
import com.protechgene.android.bpconnect.data.remote.responseModels.protocol.Data;

import java.util.List;

public class ProtocolsListAdapter extends RecyclerView.Adapter<ProtocolsListAdapter.MyViewHolder>   {


    private List<Data> dataList;
    private Context context;


    public ProtocolsListAdapter(Context context, List<Data> dataList) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_history_protocol, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Data data = dataList.get(position);

        String startDate = DateUtils.convertMillisecToDateTime(Long.parseLong(data.getStartDate())*1000, "MMM dd,yyyy");
        String endDate = DateUtils.convertMillisecToDateTime(Long.parseLong(data.getEndDate())*1000, "MMM dd,yyyy");
        holder.text_protocol_start_date.setText(startDate +" - "+ endDate);
        holder.text_protocol_end_date.setVisibility(View.GONE);
        holder.text_protocol_morning_time.setText( String.valueOf( (int)data.getSystolic()) +" mmHg");
        holder.text_protocol_evening_time.setText(String.valueOf( ( int)data.getDiastolic()) +" mmHg");
        holder.text_pulse_value.setText(String.valueOf( (int)data.getPulse()) +" bpm");
        holder.header_color_bar.setBackgroundColor(context.getResources().getColor( get_color((int)data.getSystolic(),(int)data.getDiastolic())));
    }

    public int get_color(int sys, int dia) {
      //  int color;
        if(sys < 50 && dia < 40){
            Log.e("stage","1");
            return R.color.color_search_bg_light;

        }
        else if(sys<120 && dia<80)
        {
            Log.e("stage","2");
            return R.color.reading_normal_green;

        }else if(sys<130 && dia<80)
        {
            Log.e("stage","3");
            return R.color.reading_elevated;
        }else if(sys<140 || dia<90)
        {
            Log.e("stage","4");
            return R.color.reading_hyper_stage_first;

        }else if(sys<180 || dia<120)
        {
            Log.e("stage","5");
            return R.color.reading_hyper_stage_second;
        }
        else
        {
            Log.e("stage","6");
            return R.color.reading_hyper_stage_crisis;

        }
    }

    @Override
    public int getItemCount() {
        if(dataList==null)
            return 0;
        return dataList.size();
    }


    public void setData(List<Data> dataList)
    {
        this.dataList = dataList;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout header_color_bar;
        public TextView text_protocol_start_date,text_protocol_end_date,text_protocol_morning_time,text_protocol_evening_time,text_pulse_value;

        public MyViewHolder(View view) {
            super(view);
            header_color_bar = view.findViewById(R.id.layout_header);
            text_protocol_start_date = view.findViewById(R.id.text_protocol_start_date);
            text_protocol_end_date = view.findViewById(R.id.text_protocol_end_date);
            text_protocol_morning_time = view.findViewById(R.id.text_protocol_morning_time);
            text_protocol_evening_time =  view.findViewById(R.id.text_protocol_evening_time);
            text_pulse_value = view.findViewById(R.id.text_pulse_value);
        }
    }

}

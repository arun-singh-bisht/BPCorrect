package com.protechgene.android.bpconnect.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        holder.text_protocol_morning_time.setText(data.getDatasource().getSBP() +" mmHg");
        holder.text_protocol_evening_time.setText(data.getDatasource().getDBP() +" mmHg");
        holder.text_pulse_value.setText(data.getDatasource().getPULSE() +" bpm");
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
        public TextView text_protocol_start_date,text_protocol_end_date,text_protocol_morning_time,text_protocol_evening_time,text_pulse_value;

        public MyViewHolder(View view) {
            super(view);
            text_protocol_start_date = view.findViewById(R.id.text_protocol_start_date);
            text_protocol_end_date = view.findViewById(R.id.text_protocol_end_date);
            text_protocol_morning_time = view.findViewById(R.id.text_protocol_morning_time);
            text_protocol_evening_time =  view.findViewById(R.id.text_protocol_evening_time);
            text_pulse_value = view.findViewById(R.id.text_pulse_value);
        }
    }

}

package com.protechgene.android.bpconnect.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
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

        holder.text_protocol_start_date.setText(data.getStartDate());
        holder.text_protocol_end_date.setText(data.getEndDate());
        holder.text_protocol_morning_time.setText(data.getDatasource().get(0).getSBP());
        //holder.text_protocol_evening_time.setText(data.getDatasource().getDBP());
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
        public TextView text_protocol_start_date,text_protocol_end_date,text_protocol_morning_time,text_protocol_evening_time;

        public MyViewHolder(View view) {
            super(view);
            text_protocol_start_date = view.findViewById(R.id.text_protocol_start_date);
            text_protocol_end_date = view.findViewById(R.id.text_protocol_end_date);
            text_protocol_morning_time = view.findViewById(R.id.text_protocol_morning_time);
            text_protocol_evening_time =  view.findViewById(R.id.text_protocol_evening_time);
        }
    }

}

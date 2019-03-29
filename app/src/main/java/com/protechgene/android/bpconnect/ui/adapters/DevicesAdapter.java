package com.protechgene.android.bpconnect.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.data.local.models.TutorialModel;

import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.MyViewHolder>   {


    private List<TutorialModel> readingModelList;

    public DevicesAdapter(List<TutorialModel> readingModelList) {
        this.readingModelList = readingModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_devices, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        /*BPReadingModel readingModel = readingModelList.get(position);
        holder.text_sys_value.setText(readingModel.getSys());
        holder.text_dia_value.setText(readingModel.getDia());
        holder.text_pulse_value.setText(readingModel.getPulse());
        String color = readingModel.getColor();
        holder.layout_header.setBackgroundColor(Color.parseColor(color));*/
    }

    @Override
    public int getItemCount() {
        return readingModelList.size();
    }


    public void setData(List<TutorialModel> readingModelList)
    {
        this.readingModelList = readingModelList;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text_sys_value,sys,text_dia_value,text_pulse_value;
        public View layout_header;

        public MyViewHolder(View view) {
            super(view);
          /*  text_sys_value = (TextView) view.findViewById(R.id.text_sys_value);
            text_dia_value = (TextView) view.findViewById(R.id.text_dia_value);
            text_pulse_value = (TextView) view.findViewById(R.id.text_pulse_value);
            layout_header =  view.findViewById(R.id.layout_header);*/
        }
    }


}

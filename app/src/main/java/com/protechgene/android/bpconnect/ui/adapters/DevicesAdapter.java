package com.protechgene.android.bpconnect.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.data.local.models.BPDeviceModel;

import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.MyViewHolder>   {


    private List<BPDeviceModel> bpDeviceModels;

    public DevicesAdapter(List<BPDeviceModel> bpDeviceModels) {
        this.bpDeviceModels = bpDeviceModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_devices, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BPDeviceModel bpDeviceModel = bpDeviceModels.get(position);
        holder.text_name.setText(bpDeviceModel.getDeviceName());
        holder.text_address.setText(bpDeviceModel.getDeviceAddress());
    }

    @Override
    public int getItemCount() {
        return bpDeviceModels.size();
    }


    public void setData(List<BPDeviceModel> bpDeviceModels)
    {
        this.bpDeviceModels = bpDeviceModels;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text_name,text_address;
        public View root;

        public MyViewHolder(View view) {
            super(view);
            text_name = view.findViewById(R.id.text_name);
            text_address = view.findViewById(R.id.text_address);
            root = view.findViewById(R.id.root);
        }
    }


}

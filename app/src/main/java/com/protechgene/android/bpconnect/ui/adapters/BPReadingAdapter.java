package com.protechgene.android.bpconnect.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.data.local.models.BPReadingModel;

import java.util.List;

public class BPReadingAdapter extends RecyclerView.Adapter<BPReadingAdapter.MyViewHolder>   {


    private List<BPReadingModel> readingModelList;
    private Context context;
    public BPReadingAdapter(Context context,List<BPReadingModel> readingModelList) {
        this.readingModelList = readingModelList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_bp_reading, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BPReadingModel readingModel = readingModelList.get(position);
        holder.text_sys_value.setText(readingModel.getSys());
        holder.text_dia_value.setText(readingModel.getDia());
        holder.text_pulse_value.setText(readingModel.getPulse());
        String color = readingModel.getColor();
        //holder.layout_header.setBackgroundColor(Color.parseColor(color));
        Drawable background = holder.colorPlate.getBackground();
        LayerDrawable layerDrawable = (LayerDrawable)background;
        RotateDrawable gradientDrawable = (RotateDrawable) layerDrawable
                .findDrawableByLayerId(R.id.color_rect);

        if(position%2==0)
        {
            gradientDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }else
        {
            gradientDrawable.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        }

    }

    @Override
    public int getItemCount() {
        return readingModelList.size();
    }


    public void setData(List<BPReadingModel> readingModelList)
    {
        this.readingModelList = readingModelList;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text_sys_value,sys,text_dia_value,text_pulse_value;
        public View layout_header;
        public View colorPlate;

        public MyViewHolder(View view) {
            super(view);
            text_sys_value = (TextView) view.findViewById(R.id.text_sys_value);
            text_dia_value = (TextView) view.findViewById(R.id.text_dia_value);
            text_pulse_value = (TextView) view.findViewById(R.id.text_pulse_value);
            layout_header =  view.findViewById(R.id.layout_header);
            colorPlate = view.findViewById(R.id.color_plate);
        }
    }


}

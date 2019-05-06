package com.protechgene.android.bpconnect.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.data.local.db.models.HealthReading;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReadingAdapter extends RecyclerView.Adapter<ReadingAdapter.MyViewHolder>   {


    private List<HealthReading> readingModelList;
    private Context context;
    private boolean isColorPlateShow;

    public ReadingAdapter(Context context, List<HealthReading> readingModelList) {
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
        HealthReading actualValue = readingModelList.get(position);
        holder.text_sys_value.setText(actualValue.getSystolic() +" mmHg");
        holder.text_dia_value.setText(actualValue.getDiastolic()+" mmHg");
        holder.text_pulse_value.setText(actualValue.getPulse()+" bpm");

        if(actualValue.getIs_abberant().equalsIgnoreCase("1")) {
            holder.text_aberrant.setVisibility(View.VISIBLE);
        }
        else {
            holder.text_aberrant.setVisibility(View.GONE);
        }

        Date date = new Date(Long.parseLong(actualValue.getLogTime()));
        DateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy");
        String strDate = dateFormat.format(date);
        holder.text_date.setText(strDate);
        dateFormat = new SimpleDateFormat("hh:mm a");
        String strTime = dateFormat.format(date);
        holder.text_time.setText(strTime);

    }

    @Override
    public int getItemCount() {
        if(readingModelList==null)
            return 0;
        return readingModelList.size();
    }


    public void setData(List<HealthReading> readingModelList,boolean isColorPlateShow)
    {
        this.isColorPlateShow = isColorPlateShow;
        this.readingModelList = readingModelList;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView text_sys_value,text_dia_value,text_pulse_value,text_time,text_date,text_aberrant;
        public View layout_header;

        public MyViewHolder(View view) {
            super(view);
            text_sys_value = view.findViewById(R.id.text_sys_value);
            text_dia_value = view.findViewById(R.id.text_dia_value);
            text_pulse_value = view.findViewById(R.id.text_pulse_value);
            text_date =  view.findViewById(R.id.text_date);
            text_time =  view.findViewById(R.id.text_time);
            text_aberrant = view.findViewById(R.id.text_aberrant);
            layout_header =  view.findViewById(R.id.layout_header);
        }
    }


}

package com.protechgene.android.bpconnect.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.protechgene.android.bpconnect.R;
import com.protechgene.android.bpconnect.data.local.models.BPReadingModel;
import com.protechgene.android.bpconnect.data.local.models.TutorialModel;

import java.util.List;

public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.MyViewHolder>   {

    public interface OnItemClickListener {
        void onItemClick(TutorialModel item);
    }
    private Activity activity;
    private final OnItemClickListener listener;
    private List<TutorialModel> readingModelList;

    public TutorialAdapter(List<TutorialModel> readingModelList, OnItemClickListener listener , Activity activity) {
        this.readingModelList = readingModelList;
        this.listener =listener;
        this.activity=activity;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_tutorial, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TutorialModel readingModel = readingModelList.get(position);
        holder.learn_title.setText(readingModel.getTitle());
        holder.learn_description.setText(readingModel.getDescription());
       // holder.text_pulse_value.setText(readingModel.getP);
       // String color = readingModel.getColor();
        holder.learn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(readingModel.getVideoUrl()); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivity(intent);
            }
        });
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
        public TextView learn_title,learn_description,learn_button,text_pulse_value;
        public View layout_header;

        public MyViewHolder(View view) {
            super(view);
            learn_title = (TextView) view.findViewById(R.id.learn_title);
            learn_description = (TextView) view.findViewById(R.id.learn_description);
            //text_pulse_value = (TextView) view.findViewById(R.id.text_pulse_value);
            learn_button =  view.findViewById(R.id.read_more);
        }
    }


}

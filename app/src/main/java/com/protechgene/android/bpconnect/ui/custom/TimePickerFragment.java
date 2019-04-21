package com.protechgene.android.bpconnect.ui.custom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener {

    private int mId;
    private TimePickedListener mListener;
    private String title;
    private int hour_of_day;
    private int min;
    public TimePickerFragment (TimePickedListener mListener,int id,String title,int hour_of_day,int min) {
        this.mListener = mListener;
       mId = id;
       this.title = title;
       this.hour_of_day = hour_of_day;
       this.min = min;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // use the current time as the default values for the picker

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK
                ,this, hour_of_day, min, DateFormat.is24HourFormat(getActivity()));

        TextView tvTitle = new TextView(getActivity());
        tvTitle.setText(title);
        tvTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tvTitle.setPadding(5, 12, 5, 12);
        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        timePickerDialog.setCustomTitle(tvTitle);

        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Log.d("onTimeSet","onClick:"+i);
            }
        });

        return timePickerDialog;
    }


    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // when the time is selected, send it to the activity via its callback
        // interface method
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        Log.d("onTimeSet","hourOfDay:"+hourOfDay+" minute:"+minute);

        if(mListener != null)
            mListener.onTimePicked(c, mId);

    }


    public static interface TimePickedListener {
        public void onTimePicked(Calendar time, int id);
    }
}
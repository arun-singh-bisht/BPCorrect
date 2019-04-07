package com.protechgene.android.bpconnect.ui.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener {

    private int mId;
    private TimePickedListener mListener;
    private String title;
    public TimePickerFragment (TimePickedListener mListener,int id,String title) {
        this.mListener = mListener;
       mId = id;
       this.title = title;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK
                ,this, hour, minute, DateFormat.is24HourFormat(getActivity()));

        /*.........Set a custom title for picker........*/
        TextView tvTitle = new TextView(getActivity());
        tvTitle.setText(title);
        tvTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tvTitle.setPadding(5, 12, 5, 12);
        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        timePickerDialog.setCustomTitle(tvTitle);

        return timePickerDialog;
    }


    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // when the time is selected, send it to the activity via its callback
        // interface method
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        if(mListener != null)
            mListener.onTimePicked(c, mId);
    }


    public static interface TimePickedListener {
        public void onTimePicked(Calendar time, int id);
    }
}
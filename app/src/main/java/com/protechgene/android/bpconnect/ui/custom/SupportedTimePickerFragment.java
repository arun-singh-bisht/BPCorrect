package com.protechgene.android.bpconnect.ui.custom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ibotta.android.support.pickerdialogs.SupportedTimePickerDialog;
import com.protechgene.android.bpconnect.R;

import java.util.Calendar;

public class SupportedTimePickerFragment extends DialogFragment implements
        SupportedTimePickerDialog.OnTimeSetListener {

    private int mId;
    private TimePickedListener mListener;
    private String title;
    private int hour_of_day;
    private int min;
    public SupportedTimePickerFragment(TimePickedListener mListener, int id, String title, int hour_of_day, int min) {
        this.mListener = mListener;
       mId = id;
       this.title = title;
       this.hour_of_day = hour_of_day;
       this.min = min;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // use the current time as the default values for the picker

        SupportedTimePickerDialog timePickerDialog = new SupportedTimePickerDialog(getActivity(), R.style.SpinnerTimePickerDialogTheme
                ,this, hour_of_day, min, false);


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

        Log.d("onTimeSet","hourOfDay:"+hourOfDay+" minute:"+minute);

        if(mListener != null)
            mListener.onTimePicked(c, mId);

    }


    public static interface TimePickedListener {
        public void onTimePicked(Calendar time, int id);
    }
}
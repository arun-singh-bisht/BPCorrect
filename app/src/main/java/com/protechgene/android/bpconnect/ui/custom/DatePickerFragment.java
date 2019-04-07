package com.protechgene.android.bpconnect.ui.custom;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    private DatePickedListener mListener;
    private String title;


    public DatePickerFragment(DatePickedListener mListener, String title) {
        this.mListener = mListener;
       this.title = title;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),this,mYear,mMonth,mDay);

        /*.........Set a custom title for picker........*/
        TextView tvTitle = new TextView(getActivity());
        tvTitle.setText(title);
        tvTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tvTitle.setPadding(5, 12, 5, 12);
        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        datePickerDialog.setCustomTitle(tvTitle);

        return datePickerDialog;
    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        /*if(mListener != null)
            mListener.onTimePicked(c, mId);*/
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, i);
        c.set(Calendar.MONTH, i1);
        c.set(Calendar.DAY_OF_MONTH, i2);

        if(mListener != null)
            mListener.onDatePicked(c);
    }


    public static interface DatePickedListener {
       public void onDatePicked(Calendar time);
    }
}
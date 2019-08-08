package com.protechgene.android.bpconnect.ui.custom;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.widget.DatePicker;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.protechgene.android.bpconnect.R;

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

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Light,this ,mYear,mMonth,mDay);

        /*.........Set a custom title for picker........*/
        TextView tvTitle = new TextView(getActivity());
        tvTitle.setText(title);
        tvTitle.setBackgroundColor(Color.parseColor("#ADADFF"));
        tvTitle.setTextColor(Color.parseColor("#FFFFFF"));
        tvTitle.setPadding(5, 12, 5, 12);
        tvTitle.setGravity(Gravity.CENTER);
        datePickerDialog.setCustomTitle(tvTitle);
        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
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
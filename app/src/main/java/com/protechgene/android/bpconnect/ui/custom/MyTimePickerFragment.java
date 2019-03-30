package com.protechgene.android.bpconnect.ui.custom;

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
import android.widget.Toast;

import java.util.Calendar;

public class MyTimePickerFragment  extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener onTimeSetListener;
    private String title;
    public MyTimePickerFragment(TimePickerDialog.OnTimeSetListener onTimeSetListener,String title) {
        this.onTimeSetListener = onTimeSetListener;
        this.title = title;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK
                ,onTimeSetListener, hour, minute, DateFormat.is24HourFormat(getActivity()));
       /* TimePickerDialog timePickerDialog =new TimePickerDialog(getActivity(), timeSetListener, hour, minute,
                DateFormat.is24HourFormat(getActivity()));*/
        /*.........Set a custom title for picker........*/
        TextView tvTitle = new TextView(getActivity());
        tvTitle.setText(title);
        tvTitle.setBackgroundColor(Color.parseColor("#EEE8AA"));
        tvTitle.setPadding(5, 8, 5, 8);
        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        timePickerDialog.setCustomTitle(tvTitle);

        return timePickerDialog;
    }

    private TimePickerDialog.OnTimeSetListener timeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Toast.makeText(getActivity(), "selected time is "
                                    + view.getHour() +
                                    " / " + view.getMinute()
                            , Toast.LENGTH_SHORT).show();
                }
            };
}

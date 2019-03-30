package com.protechgene.android.bpconnect.ui.custom;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.protechgene.android.bpconnect.R;

/**
 * Created by Arun.Singh on 9/6/2018.
 */

public class CustomAlertDialog {

    public interface I_CustomAlertDialog
    {
        void onPositiveClick(Dialog dialog);
        void onNegativeClick(Dialog dialog);
    }

    public static void showDialog(Activity activity, String msg,int res_id,final I_CustomAlertDialog i_customAlertDialog){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(res_id);

       TextView text = (TextView) dialog.findViewById(R.id.txt_title);
        text.setText(msg);

        TextView txt_negative = (TextView) dialog.findViewById(R.id.txt_negative);
        txt_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                i_customAlertDialog.onNegativeClick(dialog);
            }
        });
        TextView txt_positive = (TextView) dialog.findViewById(R.id.txt_positive);
        txt_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                i_customAlertDialog.onPositiveClick(dialog);
            }
        });
        dialog.show();
    }


    public static void showDialog(Activity activity, String msg,String positiveBtnText,String negativeBtnText,int res_id,final I_CustomAlertDialog i_customAlertDialog){

        final Dialog dialog = new Dialog(activity,R.style.Theme_AppCompat_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(res_id);
        dialog.setCancelable(false);

        //Set Title
        TextView text = (TextView) dialog.findViewById(R.id.txt_title);
        text.setText(msg);

        //Set Negative button Text and click listener
        TextView txt_negative = (TextView) dialog.findViewById(R.id.txt_negative);
        if(negativeBtnText!=null)
            txt_negative.setText(negativeBtnText);
        txt_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                i_customAlertDialog.onNegativeClick(dialog);
            }
        });
        //Set Positive button Text and click listener
        TextView txt_positive = (TextView) dialog.findViewById(R.id.txt_positive);
        if(positiveBtnText!=null)
            txt_positive.setText(positiveBtnText);
        txt_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                i_customAlertDialog.onPositiveClick(dialog);
            }
        });
        dialog.show();
    }


    public static void showDialogSingleButton(Activity activity,String msg,final I_CustomAlertDialog i_customAlertDialog){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialo_single_button);

        TextView text = (TextView) dialog.findViewById(R.id.txt_title);
        text.setText(msg);

        TextView txt_ok = (TextView) dialog.findViewById(R.id.txt_ok);
        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                i_customAlertDialog.onPositiveClick(dialog);
            }
        });
        dialog.show();
    }
}
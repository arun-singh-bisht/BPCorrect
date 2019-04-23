package com.protechgene.android.bpconnect.ui.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
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
        void onPositiveClick(Dialog dialog,int request_code);
        void onNegativeClick(Dialog dialog,int request_code);
    }

    public interface I_CustomAlertDialogThreeButton
    {
        void onPositiveClick(int request_code);
        void onNegativeClick(int request_code);
        void onNeuralClick(int request_code);
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
                i_customAlertDialog.onNegativeClick(dialog,0);
            }
        });
        TextView txt_positive = (TextView) dialog.findViewById(R.id.txt_positive);
        txt_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                i_customAlertDialog.onPositiveClick(dialog,0);
            }
        });
        dialog.show();
    }


    //Two Button
    public static void showDialog(Activity activity,final int request_code, String msg,String positiveBtnText,String negativeBtnText,int res_id,final I_CustomAlertDialog i_customAlertDialog){

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
                i_customAlertDialog.onNegativeClick(dialog,request_code);
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
                i_customAlertDialog.onPositiveClick(dialog,request_code);
            }
        });
        dialog.show();
    }

    //Three Button
    public static void showThreeButtonDialog(Activity activity,final int request_code, String msg,String positiveBtnText,String negativeBtnText,String neutralBtnText,final I_CustomAlertDialogThreeButton i_customAlertDialogThreeButton){

        AlertDialog.Builder  builder = new AlertDialog.Builder(activity);
        //builder.setTitle("Test");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(positiveBtnText,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        i_customAlertDialogThreeButton.onPositiveClick(request_code);
                    }
                });

        builder.setNeutralButton(neutralBtnText,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        i_customAlertDialogThreeButton.onNeuralClick(request_code);
                    }
                });

        builder.setNegativeButton(negativeBtnText,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        i_customAlertDialogThreeButton.onNegativeClick(request_code);
                    }
                });
        builder.create().show();
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
                i_customAlertDialog.onPositiveClick(dialog,0);
            }
        });
        dialog.show();
    }
}

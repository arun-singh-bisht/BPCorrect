package com.protechgene.android.bpconnect.ui.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.Window;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

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

    public static void showDefaultDialog(Context context,String title,String mesg)
    {
        // setup the alert builder
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);

        builder.setTitle(title);

        // Linkify the message
        final SpannableString s = new SpannableString(mesg);
        Linkify.addLinks(s, Linkify.ALL);

        // add a list
        builder.setMessage(s);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        // create and show the alert dialog
        android.app.AlertDialog dialog = builder.create();

        int titleId = context.getResources().getIdentifier( "alertTitle", "id", "android" );
        if (titleId > 0) {
            TextView dialogTitle = (TextView) dialog.findViewById(titleId);
            if (dialogTitle != null) {
                Linkify.addLinks(dialogTitle, Linkify.WEB_URLS);
            }
        }

        dialog.show();

        // Make the textview clickable. Must be called after show()
        ((TextView)dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }


    public static void showVideoDialog(final Context context)
    {
        final Dialog dialog = new Dialog(context,R.style.Theme_AppCompat_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_video);
        dialog.setCancelable(false);
        dialog.findViewById(R.id.text_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        final VideoView simpleVideoView = dialog.findViewById(R.id.video_view);
        dialog.findViewById(R.id.image_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hide Play Button
                dialog.findViewById(R.id.image_play).setVisibility(View.GONE);
                //Load Video
                String link="http://67.211.223.164:8080/video/bp_video.mp4";
                Uri video = Uri.parse(link);
                MediaController mediaController = new MediaController(context);
                mediaController.setAnchorView(simpleVideoView);
                simpleVideoView.setMediaController(mediaController);
                simpleVideoView.setVideoURI(video);
                simpleVideoView.start();
            }
        });

        dialog.show();
    }

    public static void showInstructionDialog(Context context)
    {
        // setup the alert builder
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("Proper blood pressure measuring instructions");

        builder.setMessage(R.string.bulleted_list);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        // create and show the alert dialog
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
}

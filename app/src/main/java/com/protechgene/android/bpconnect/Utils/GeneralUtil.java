package com.protechgene.android.bpconnect.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.widget.EditText;
import android.widget.Toast;


public class GeneralUtil {

    private static  AlertDialog progressDialog;
    public static void showToast(final Context context,final String msg)
    {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void setValueInEditText(EditText editText,String value)
    {
        if(value!=null && !value.isEmpty() && !value.equalsIgnoreCase("null"))
            editText.setText(value);
    }

    public static String getTextFromEditText(Activity activity, int resId)
    {
        return ((EditText)activity.findViewById(resId)).getText().toString();
    }

    public static boolean validateEditText(Activity activity,int resId)
    {
        String text = ((EditText)activity.findViewById(resId)).getText().toString();
        if(text==null || text.isEmpty())
            return false;
        return true;
    }

    public static boolean validateEmailEditText(Activity activity,int resId)
    {
        String text = ((EditText)activity.findViewById(resId)).getText().toString();
        if(text==null || text.isEmpty())
            return false;
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches())
            return false;

        return true;
    }

    public static boolean validatePAsswordEditText(Activity activity,int resId)
    {
        String text = ((EditText)activity.findViewById(resId)).getText().toString();
        if(text==null || text.isEmpty())
            return false;
        if(text.length()<8)
            return false;
        return true;
    }
    public static boolean validatePhoneNumberEditText(Activity activity,int resId)
    {
        String text = ((EditText)activity.findViewById(resId)).getText().toString();
        if(text==null || text.isEmpty() || text.length()!=10)
            return false;

        return true;
    }


    public static void  dismissProgressDialog()
    {
        if(progressDialog!=null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


}

package com.protechgene.android.bpconnect.Utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class FragmentUtil {

    public static void loadFragment(Context context,int containerViewId,
                                   Fragment fragment,
                                   String fragmentTag,
                                   String backStackStateName) {

        if(backStackStateName!=null) {
            ((AppCompatActivity)context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(containerViewId, fragment, fragmentTag)
                    .addToBackStack(backStackStateName)
                    .commit();
        }else
        {
            ((AppCompatActivity)context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(containerViewId, fragment, fragmentTag)
                    .commit();
        }
    }
}

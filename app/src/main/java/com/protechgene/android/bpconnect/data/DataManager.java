package com.protechgene.android.bpconnect.data;

import android.content.Context;

import com.protechgene.android.bpconnect.data.local.sp.SPInterface;
import com.protechgene.android.bpconnect.data.local.sp.SharedPreferenceHelper;

public class DataManager implements SPInterface {


    private static DataManager dataManager;
    private Context context;
    private SharedPreferenceHelper preferenceHelper;

    private DataManager(Context context)
    {
        this.context = context;
    }

    public static DataManager getInstance(Context context)
    {
        if (dataManager ==null)
        {
            dataManager = new DataManager(context);
        }
        return dataManager;
    }


    @Override
    public void setSharedPreferenceString(String key, String value) {
        preferenceHelper.setSharedPreferenceString(context,key,value);
    }

    @Override
    public void setSharedPreferenceInt(String key, int value) {
        preferenceHelper.setSharedPreferenceInt(context,key,value);
    }

    @Override
    public void setSharedPreferenceBoolean(String key, boolean value) {
        preferenceHelper.setSharedPreferenceBoolean(context,key,value);
    }

    @Override
    public String getSharedPreferenceString(String key, String defValue) {
        return preferenceHelper.getSharedPreferenceString(context,key,defValue);
    }

    @Override
    public int getSharedPreferenceInt(String key, int defValue) {
        return preferenceHelper.getSharedPreferenceInt(context,key,defValue);
    }

    @Override
    public boolean getSharedPreferenceBoolean(String key, boolean defValue) {
        return preferenceHelper.getSharedPreferenceBoolean(context,key,defValue);
    }
}

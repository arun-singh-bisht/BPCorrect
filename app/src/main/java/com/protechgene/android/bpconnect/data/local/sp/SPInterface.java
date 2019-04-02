package com.protechgene.android.bpconnect.data.local.sp;

import android.content.Context;
import android.content.SharedPreferences;

public interface SPInterface {

    void setSharedPreferenceString(String key, String value);
    void setSharedPreferenceInt(String key, int value);
    void setSharedPreferenceBoolean(String key, boolean value);
    String getSharedPreferenceString(String key, String defValue);
    int getSharedPreferenceInt(String key, int defValue);
    boolean getSharedPreferenceBoolean(String key, boolean defValue);
}

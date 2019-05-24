package com.protechgene.android.bpconnect.Utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StringUtil {

    public static String getValue(String s)
    {
        if(s==null || s.equalsIgnoreCase("null"))
            return "";
        return s;
    }

}

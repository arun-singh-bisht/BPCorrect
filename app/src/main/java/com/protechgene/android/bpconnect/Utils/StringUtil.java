package com.protechgene.android.bpconnect.Utils;

public class StringUtil {

    public static String getValue(String s)
    {
        if(s==null || s.equalsIgnoreCase("null"))
            return "";
        return s;
    }
}

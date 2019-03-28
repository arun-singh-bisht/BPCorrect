package com.protechgene.android.bpconnect.Utils;

public class MathUtil {

    public static int getRandomNumber(int rangeMin,int rangeMax)
    {
        return rangeMin + (int)(Math.random() * ((rangeMax - rangeMin) + 1));
    }
}

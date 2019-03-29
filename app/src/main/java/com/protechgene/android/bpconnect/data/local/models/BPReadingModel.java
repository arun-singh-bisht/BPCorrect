package com.protechgene.android.bpconnect.data.local.models;

import com.protechgene.android.bpconnect.Utils.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class BPReadingModel {

    String date;
    String sys;
    String dia;
    String pulse;
    String color;

    public BPReadingModel(String date, String sys, String dia, String pulse,String color) {
        this.date = date;
        this.sys = sys;
        this.dia = dia;
        this.pulse = pulse;
        this.color = color;
    }

    public String getDate() {
        return date;
    }

    public String getSys() {
        return sys;
    }

    public String getDia() {
        return dia;
    }

    public String getPulse() {
        return pulse;
    }

    public String getColor() {
        return color;
    }

    public static List<BPReadingModel> getData()
    {
        List<BPReadingModel> readingModels = new ArrayList<>();
        for(int i=1;i<13;i++)
        {
            int sys = MathUtil.getRandomNumber(120,140);
            int dia = MathUtil.getRandomNumber(70,90);
            int pulse = MathUtil.getRandomNumber(70,80);
            int color = MathUtil.getRandomNumber(1,4);
            String colorCode ="";
            if (color==1)
                colorCode ="#444549";
            else if (color==2)
                colorCode ="#444549";
            else if (color==3)
                colorCode ="#444549";
            else if (color==4)
                colorCode ="#444549";


            readingModels.add(new BPReadingModel("March 29, 2019",sys+" mmHg",dia+" mmHg",pulse+" bpm",colorCode));
        }
        return readingModels;
    }
}

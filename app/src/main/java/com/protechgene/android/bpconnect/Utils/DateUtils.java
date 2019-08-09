package com.protechgene.android.bpconnect.Utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static int getAge(Date dateOfBirth) {

        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();

        int age = 0;

        birthDate.setTime(dateOfBirth);
        if (birthDate.after(today)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }

        age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
        if ((birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
                (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
            age--;

            // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
        } else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) &&
                (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
    }

    public static String getDateString(int offset, String requiredFormatt) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, offset);
        DateFormat dateFormat = new SimpleDateFormat(requiredFormatt);
        return dateFormat.format(cal.getTime());
    }

    public static String getTimeString(int offsetOnMin, String requiredFormatt) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, offsetOnMin);
        DateFormat dateFormat = new SimpleDateFormat(requiredFormatt);
        return dateFormat.format(cal.getTime());
    }

    public static String convertMillisecToDateTime(long milliseconds, String resultFormat) {
        DateFormat simple = new SimpleDateFormat(resultFormat);
        Date result = new Date(milliseconds);
        return simple.format(result);
    }

    public static String convertDateStringToMillisec(String dateString, String format) {
        DateFormat simple = new SimpleDateFormat(format);
        try {
            Date parse = simple.parse(dateString);
            return parse.getTime() / 1000 + "";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int convertMillisecToHourOfDay(long seconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(seconds * 1000L);
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));

        return Calendar.HOUR_OF_DAY;

    }

    //timeString - 18:30
    public static String conver24hourformatTo12hour(String timeString) {
        String[] split = timeString.split(":");
        String h = split[0];
        String m = split[1];

        int hh = Integer.parseInt(h);
        hh = hh % 12;

        int mm = Integer.parseInt(m);
        if (mm == 0)
            m = "00";
        else if (mm < 10)
            m = "0" + mm;

        return hh + ":" + m;
    }

    //time1 - 15:22
    public static long compareTimeString(String time1, String time2, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date d1 = sdf.parse(time1);
            Date d2 = sdf.parse(time2);
            long elapsed = d1.getTime() - d2.getTime();
            // current time is lesser than previous time
            if (elapsed < 0)
                return -1;
            // current time = previous time
            else if (elapsed == 0)
                return 0;
            // current time is greater than previous time
            else if (elapsed > 0)
                return elapsed;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long daysDifferenceBetweenDates(String dateString1, String dateString2, String format) {
        SimpleDateFormat myFormat = new SimpleDateFormat(format);

        try {
            Date date1 = myFormat.parse(dateString1);
            Date date2 = myFormat.parse(dateString2);

            long diff = date2.getTime() - date1.getTime();
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            // System.out.println ("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String addTime(String timeString, String format, int add_hour, int add_min) {

        SimpleDateFormat myFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = myFormat.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, add_hour);
        calendar.add(Calendar.MINUTE, add_min);
        return calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
    }
}
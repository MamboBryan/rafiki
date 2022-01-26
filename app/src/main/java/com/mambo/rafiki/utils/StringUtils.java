package com.mambo.rafiki.utils;

import android.text.TextUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StringUtils {

    public static String intToString(int number) {
        return String.valueOf(number);
    }

    public static String getStringFrom(String s) {
        return s == null ? "" : s;
    }

    public static String getDescription(String s) {
        return s == null || TextUtils.isEmpty(s) ? "No description" : s;
    }

    public static String getDateDisplayText(Date date) {

        String month = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date.getTime());
        String day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
        String actualDate = new SimpleDateFormat("d", Locale.ENGLISH).format(date.getTime());
        String year = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date.getTime());

        return day + " , " + month + " " + actualDate + " " + year;

    }

    public static String getTimeFromDate(Date date) {

        PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
        String ago = prettyTime.format(date);

        return ago;

    }
}

package com.mambo.rafiki.utils;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String getTime(Date date) {

        PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
        return prettyTime.format(date);

    }

    public static String getMonth(Date date) {

        return new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date.getTime());

    }

    public static String getDateDisplayText(Date date) {

        String month = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date.getTime());
        String day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
        String actualDate = new SimpleDateFormat("d", Locale.ENGLISH).format(date.getTime());
        String year = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date.getTime());

        return day + " , " + month + " " + actualDate + " " + year;

    }

}

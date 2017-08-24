package com.kumarangarden.billingsystem.m_Utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kanna_000 on 24-08-2017.
 */

public class DateTimeUtil {

    public static String GetFormatChanged(String from, String to, String value)
    {
        DateFormat originalFormat = new SimpleDateFormat(from, Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat(to, Locale.ENGLISH);
        Date t_date = null;
        try {
            t_date = originalFormat.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  targetFormat.format(t_date);
    }
}

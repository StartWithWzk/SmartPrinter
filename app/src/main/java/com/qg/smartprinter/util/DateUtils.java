package com.qg.smartprinter.util;

import android.support.annotation.Nullable;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;

/**
 * @author TZH
 * @version 1.0
 */
public class DateUtils {
    public static String getDateString() {
        return getDateString(new Date());
    }

    public static String getDateString(@Nullable Date date) {
        if (date == null) {
            return "";
        }
        return DateFormat.format("yyyy-MM-dd kk:mm:ss", date).toString();
    }

    public static Calendar getCalendar(@Nullable Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        return calendar;
    }
}

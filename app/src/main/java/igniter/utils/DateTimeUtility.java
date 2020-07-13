package igniter.utils;
/**
 * @package com.trioangle.igniter
 * @subpackage utils
 * @category DateTimeUtility
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import igniter.configs.AppController;
import igniter.configs.Constants;

/*****************************************************************
 DateTimeUtility
 ****************************************************************/
public class DateTimeUtility {

    @Inject
    Context context;

    public DateTimeUtility() {
        AppController.getAppComponent().inject(this);
    }

    public long toLocalTime(long time, TimeZone to) {
        return convertTime(time, Constants.utcTZ, to);
    }

    public long toUTC(long time, TimeZone from) {
        return convertTime(time, from, Constants.utcTZ);
    }

    public long convertTime(long time, TimeZone from, TimeZone to) {
        return time + getTimeZoneOffset(time, from, to);
    }

    private long getTimeZoneOffset(long time, TimeZone from, TimeZone to) {
        int fromOffset = from.getOffset(time);
        int toOffset = to.getOffset(time);
        int diff;

        if (fromOffset >= 0) {
            if (toOffset > 0) {
                toOffset = -1 * toOffset;
            } else {
                toOffset = Math.abs(toOffset);
            }
            diff = (fromOffset + toOffset) * -1;
        } else {
            if (toOffset <= 0) {
                toOffset = -1 * Math.abs(toOffset);
            }
            diff = (Math.abs(fromOffset) + toOffset);
        }
        return diff;
    }

    public Date convertStringToDate(String stringDate) {
        Date date = null;
        try {
            date = Constants.utcFormat.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Date toLocalDate(String utcDateString) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(convertStringToDate(utcDateString));
        long sLocalTime = toLocalTime(calendar.getTimeInMillis(), calendar.getTimeZone());
        calendar.setTimeInMillis(sLocalTime);
        return calendar.getTime();
    }

    public String toUtcString(Date localDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(localDate);
        long utcTime = toUTC(calendar.getTimeInMillis(), calendar.getTimeZone());
        calendar.setTimeInMillis(utcTime);
        return getDateTimeString(calendar.getTime());
    }

    public String getUTCTime(Date time) {
        try {
            String format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
            simpleDateFormat.setTimeZone(timeZone);

            return simpleDateFormat.format(time);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getDateTimeString(Date date) {
        return Constants.dateTimeFormat.format(date);
    }

    public String getDateString(Date date) {
        return Constants.dateFormat.format(date);
    }

    public String getTimeString(Date date) {
        return Constants.timeFormat.format(date);
    }

    public String getChatFormatDate(String strUtcDate) {
        SimpleDateFormat chatFormat = new SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault());
        String localTime = "";
        try {
            Date date = null;
            try {
                date = Constants.chatUtcFormat.parse(strUtcDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            localTime = chatFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localTime;
    }
}

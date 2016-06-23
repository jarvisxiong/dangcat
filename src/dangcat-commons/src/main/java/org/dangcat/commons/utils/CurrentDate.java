package org.dangcat.commons.utils;

import java.util.Date;

public class CurrentDate
{
    public static void clear()
    {
        DateUtils.setCurrentDate(null);
    }

    public static Date get()
    {
        return DateUtils.clear(DateUtils.MILLISECOND, DateUtils.now());
    }

    public static Date next(int field, int value)
    {
        Date date = get();
        set(DateUtils.add(field, date, value));
        return get();
    }

    public static Date nextDay(int value)
    {
        return next(DateUtils.DAY, value);
    }

    public static Date nextHour(int value)
    {
        return next(DateUtils.HOUR, value);
    }

    public static Date nextMinute(int value)
    {
        return next(DateUtils.MINUTE, value);
    }

    public static Date nextMonth(int value)
    {
        return next(DateUtils.MONTH, value);
    }

    public static Date nextSecond(int value)
    {
        return next(DateUtils.SECOND, value);
    }

    public static Date nextWeek(int value)
    {
        return next(DateUtils.WEEK, value);
    }

    public static Date nextYear(int value)
    {
        return next(DateUtils.YEAR, value);
    }

    public static void set(Date date)
    {
        if (date == null)
            date = new Date();
        date = DateUtils.clear(DateUtils.MILLISECOND, date);
        DateUtils.setCurrentDate(date);
    }
}

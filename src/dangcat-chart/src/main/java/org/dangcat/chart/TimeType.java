package org.dangcat.chart;

import java.util.Calendar;

public enum TimeType
{
    Day(Calendar.DAY_OF_YEAR), Hour(Calendar.HOUR_OF_DAY), Minute(Calendar.MINUTE), Month(Calendar.MONTH), Week(Calendar.WEEK_OF_MONTH), Year(Calendar.YEAR);

    private final int value;

    /**
     * 构造时间类型。
     * @param value 过滤类型值。
     */
    TimeType(int value)
    {
        this.value = value;
    }

    /**
     * 取得时间类型。
     * @return 过滤类型值。
     */
    public int getValue()
    {
        return this.value;
    }
}

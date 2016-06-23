package org.dangcat.chart.highcharts;

import java.util.Date;

public class DateTimeValue extends DataValue
{
    private Date dateTime;

    public DateTimeValue(String name, Date dateTime, long value)
    {
        super(name, value);
        this.dateTime = dateTime;
    }

    public Date getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(Date dateTime)
    {
        this.dateTime = dateTime;
    }
}

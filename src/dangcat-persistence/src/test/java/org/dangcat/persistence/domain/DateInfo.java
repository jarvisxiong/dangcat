package org.dangcat.persistence.domain;

import java.util.Date;

import org.dangcat.commons.formator.DateType;
import org.dangcat.commons.formator.annotation.DateStyle;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;

@Table
public class DateInfo
{
    public static final String Day = "Day";
    public static final String Full = "Full";
    public static final String Id = "Id";
    public static final String Minute = "Minute";
    public static final String Second = "Second";

    @Column(index = 4)
    @DateStyle(DateType.Day)
    private Date day;

    @Column(fieldName = "FullDate", index = 1)
    private Date full;

    @Column(isPrimaryKey = true, isAutoIncrement = true, index = 0)
    private Integer id;

    @Column(index = 3)
    @DateStyle(DateType.Minute)
    private Date minute;

    @Column(index = 2)
    @DateStyle(DateType.Second)
    private Date second;

    public Date getDay()
    {
        return day;
    }

    public Date getFull()
    {
        return full;
    }

    public Integer getId()
    {
        return id;
    }

    public Date getMinute()
    {
        return minute;
    }

    public Date getSecond()
    {
        return second;
    }

    public void setDay(Date day)
    {
        this.day = day;
    }

    public void setFull(Date full)
    {
        this.full = full;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setMinute(Date minute)
    {
        this.minute = minute;
    }

    public void setSecond(Date second)
    {
        this.second = second;
    }
}

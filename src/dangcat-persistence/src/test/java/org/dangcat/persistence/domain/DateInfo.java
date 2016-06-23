package org.dangcat.persistence.domain;

import org.dangcat.commons.formator.DateType;
import org.dangcat.commons.formator.annotation.DateStyle;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;

import java.util.Date;

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

    public void setDay(Date day)
    {
        this.day = day;
    }

    public Date getFull()
    {
        return full;
    }

    public void setFull(Date full)
    {
        this.full = full;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Date getMinute()
    {
        return minute;
    }

    public void setMinute(Date minute)
    {
        this.minute = minute;
    }

    public Date getSecond()
    {
        return second;
    }

    public void setSecond(Date second)
    {
        this.second = second;
    }
}

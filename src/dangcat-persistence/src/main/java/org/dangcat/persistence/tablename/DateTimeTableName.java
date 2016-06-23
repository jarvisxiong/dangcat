package org.dangcat.persistence.tablename;

import org.dangcat.commons.utils.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 按时间分表名对象。
 * @author dangcat
 * 
 */
public class DateTimeTableName extends FieldValueTableName implements DynamicTable
{
    private static final String DEFAULT_FIELDNAME = "DateTime";
    private DateFormat dateFormat = null;
    private Date dateTime = null;
    private int dateType = DateUtils.MONTH;

    public DateTimeTableName(String name)
    {
        this(name, null, DEFAULT_FIELDNAME);
    }

    public DateTimeTableName(String name, String fieldName)
    {
        this(name, null, fieldName);
    }

    public DateTimeTableName(String name, String alias, String fieldName)
    {
        super(name, alias, fieldName);
    }

    private DateFormat getDateFormat()
    {
        if (this.dateFormat == null)
        {
            String format = "'" + super.getName() + "'";
            if (this.getDateType() == DateUtils.DAY)
                format += "_yyyy_MM_dd";
            else if (this.getDateType() == DateUtils.WEEK)
                format += "_yyyy_MM_'W'W";
            else if (this.getDateType() == DateUtils.MONTH)
                format += "_yyyy_MM";
            else if (this.getDateType() == DateUtils.YEAR)
                format += "_yyyy";
            this.dateFormat = new SimpleDateFormat(format);
        }
        return this.dateFormat;
    }

    public Date getDateTime()
    {
        return this.dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    protected Date getDateTime(Object data)
    {
        return (Date) super.getValue(data);
    }

    public int getDateType()
    {
        return this.dateType;
    }

    public void setDateType(int dateType) {
        this.dateType = dateType;
        this.dateFormat = null;
    }

    @Override
    public String getName()
    {
        if (this.getDateTime() != null)
            return this.getName(this.getDateTime());
        return this.getName(DateUtils.now());
    }

    @Override
    public String getName(Object value)
    {
        Date dateTime = null;
        if (value instanceof Date)
            dateTime = (Date) value;
        else
            dateTime = this.getDateTime(value);
        if (dateTime != null)
        {
            DateFormat dateFormat = this.getDateFormat();
            synchronized (dateFormat)
            {
                return dateFormat.format(dateTime);
            }
        }
        return DynamicTableUtils.getActualTableName(this);
    }

    public Date parse(String text)
    {
        Date date = null;
        try
        {
            DateFormat dateFormat = this.getDateFormat();
            synchronized (dateFormat)
            {
                date = dateFormat.parse(text);
            }
        }
        catch (ParseException e)
        {
        }
        return date;
    }
}

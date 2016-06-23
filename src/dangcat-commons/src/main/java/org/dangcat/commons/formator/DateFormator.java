package org.dangcat.commons.formator;

import org.dangcat.commons.utils.ValueUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间格式化工具。
 * 
 */
public class DateFormator implements DataFormator
{
    /** yyyy-MM-dd HH:mm:ss.SSS. */
    public static final String DATESTYLE_FULL_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    /** Tue May 6 09:27:39 GMT+08:00 2014. */
    public static final String DATESTYLE_GMT_PATTERN = "EEE MMM d HH:mm:ss z yyyy";
    /** yyyy-MM-dd HH. */
    public static final String DATESTYLE_HOUR_PATTERN = "yyyy-MM-dd HH";
    /** yyyy-MM-dd HH:mm:ss */
    public static final String DATESTYLE_LONG_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /** yyyy-MM-dd HH:mm. */
    public static final String DATESTYLE_MEDIUM_PATTERN = "yyyy-MM-dd HH:mm";
    /** yyyy-MM-dd. */
    public static final String DATESTYLE_SHORT_PATTERN = "yyyy-MM-dd";
    private SimpleDateFormat dateFormat = null;
    private DateType dateType = DateType.Full;

    public DateFormator(SimpleDateFormat dateFormat, DateType dateType) {
        this.dateFormat = dateFormat;
        this.dateType = dateType;
    }

    public DateFormator(String dateFormat) {
        this.dateFormat = new SimpleDateFormat(dateFormat);
    }

    public static SimpleDateFormat getDateFormat(DateType dateType)
    {
        String datePattern = getDatePattern(dateType);
        return new SimpleDateFormat(datePattern);
    }

    public static SimpleDateFormat getDateFormat(DateType dateType, Locale locale)
    {
        String datePattern = getDatePattern(dateType);
        if (locale != null)
            return new SimpleDateFormat(datePattern, locale);
        return new SimpleDateFormat(datePattern);
    }

    public static DateFormator getDateFormator(DateType dateType)
    {
        return new DateFormator(getDateFormat(dateType), dateType);
    }

    /**
     * 读取最适合的解析工具。
     * @param text 字串文字。
     * @return 格式化工具。
     */
    public static DateFormator getDateFormator(String text)
    {
        DateFormator dateFormator = null;
        if (!ValueUtils.isEmpty(text))
        {
            for (DateType dateType : DateType.values())
            {
                try
                {
                    SimpleDateFormat dateFormat = getDateFormat(dateType);
                    if (dateFormat.parse(text) != null)
                    {
                        dateFormator = new DateFormator(dateFormat, dateType);
                        break;
                    }
                }
                catch (Exception e)
                {
                }
            }
        }
        return dateFormator;
    }

    public static String getDatePattern(DateType dateType)
    {
        String datePattern = null;
        if (DateType.Day.equals(dateType))
            datePattern = DATESTYLE_SHORT_PATTERN;
        else if (DateType.Hour.equals(dateType))
            datePattern = DATESTYLE_HOUR_PATTERN;
        else if (DateType.Minute.equals(dateType))
            datePattern = DATESTYLE_MEDIUM_PATTERN;
        else if (DateType.Second.equals(dateType))
            datePattern = DATESTYLE_LONG_PATTERN;
        else if (DateType.GMT.equals(dateType))
            datePattern = DATESTYLE_GMT_PATTERN;
        else
            datePattern = DATESTYLE_FULL_PATTERN;
        return datePattern;
    }

    /**
     * 格式化日期。
     * @param date 日期对象。
     * @return
     */
    @Override
    public String format(Object date)
    {
        return this.getDateFormat().format((Date) date);
    }

    public DateFormat getDateFormat()
    {
        return this.dateFormat;
    }

    public String getDatePattern()
    {
        return this.dateFormat.toPattern();
    }

    public DateType getDateType()
    {
        return this.dateType;
    }

    /**
     * 由字串解析日期。
     * @param text 字串文字。
     * @param defaultValue 默认值。
     * @return 返回值。
     */
    public Date parse(String text, Date defaultValue)
    {
        Date date = defaultValue;
        DateFormat dateFormat = this.getDateFormat();
        if (dateFormat != null)
        {
            try
            {
                date = dateFormat.parse(text);
            }
            catch (ParseException e)
            {
            }
        }
        return date;
    }
}

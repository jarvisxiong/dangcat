package org.dangcat.commons.utils;

import junit.framework.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class TestDateUtils {
    @Test
    public void testDiff() {
        Date beginDate = ValueUtils.parseDate("2012-05-20 20:59:59.999");
        Date endDate = DateUtils.add(DateUtils.DAY, beginDate, 1);
        Assert.assertEquals(0, DateUtils.diff(DateUtils.YEAR, beginDate, endDate));
        Assert.assertEquals(0, DateUtils.diff(DateUtils.MONTH, beginDate, endDate));
        Assert.assertEquals(1, DateUtils.diff(DateUtils.DAY, beginDate, endDate));
        endDate = DateUtils.add(DateUtils.HOUR, beginDate, 1);
        Assert.assertEquals(0, DateUtils.diff(DateUtils.DAY, beginDate, endDate));
        Assert.assertEquals(1, DateUtils.diff(DateUtils.HOUR, beginDate, endDate));
        Assert.assertEquals(60, DateUtils.diff(DateUtils.MINUTE, beginDate, endDate));
        Assert.assertEquals(60 * 60, DateUtils.diff(DateUtils.SECOND, beginDate, endDate));
        Assert.assertEquals(60 * 60 * 1000, DateUtils.diff(DateUtils.MILLISECOND, beginDate, endDate));

        // 测试取一个月的最后一天
        Date srcDateTime = DateUtils.getLastDayOfMonth(ValueUtils.parseDate("2012-02-15 10:20:23.999"));
        Date dstDateTime = ValueUtils.parseDate("2012-02-29 23:59:59.999");
        Assert.assertEquals(0, DateUtils.diff(DateUtils.SECOND, srcDateTime, dstDateTime));

        // 测试闰年
        Date leapYearDate = ValueUtils.parseDate("2012-02-29 23:59:59");
        Assert.assertTrue(DateUtils.isLeapYear(leapYearDate));
        Date notLeapYearDate = ValueUtils.parseDate("2013-02-29 23:59:59");
        Assert.assertFalse(DateUtils.isLeapYear(notLeapYearDate));
    }

    @Test
    public void testGMTDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2014);
        calendar.set(Calendar.MONTH, 5 - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 27);
        calendar.set(Calendar.SECOND, 39);
        calendar.set(Calendar.MILLISECOND, 0);

        String text = "Tue May 6 09:27:39 GMT+08:00 2014";
        Assert.assertEquals(calendar.getTime(), DateUtils.parse(text, null));
    }

    @Test
    public void testRange() {
        Date dateTime = ValueUtils.parseDate("2014-03-28 20:59:59.999");
        Date expected = ValueUtils.parseDate("2014-03-28 00:00:00.000");
        Date actual = DateUtils.getFirstTimeOfDay(dateTime);
        Assert.assertEquals(expected, actual);
        expected = ValueUtils.parseDate("2014-03-28 23:59:59.999");
        actual = DateUtils.getLastTimeOfDay(dateTime);
        Assert.assertEquals(expected, actual);

        expected = ValueUtils.parseDate("2014-03-23 00:00:00.000");
        actual = DateUtils.getFirstDayOfWeek(dateTime);
        Assert.assertEquals(expected, actual);
        expected = ValueUtils.parseDate("2014-03-29 23:59:59.999");
        actual = DateUtils.getLastDayOfWeek(dateTime);
        Assert.assertEquals(expected, actual);

        expected = ValueUtils.parseDate("2014-03-01 00:00:00.000");
        actual = DateUtils.getFirstDayOfMonth(dateTime);
        Assert.assertEquals(expected, actual);
        expected = ValueUtils.parseDate("2014-03-31 23:59:59.999");
        actual = DateUtils.getLastDayOfMonth(dateTime);
        Assert.assertEquals(expected, actual);

        expected = ValueUtils.parseDate("2014-01-01 00:00:00.000");
        actual = DateUtils.getFirstDayOfYear(dateTime);
        Assert.assertEquals(expected, actual);
        expected = ValueUtils.parseDate("2014-12-31 23:59:59.999");
        actual = DateUtils.getLastDayOfYear(dateTime);
        Assert.assertEquals(expected, actual);
    }
}

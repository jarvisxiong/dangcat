package org.dangcat.chart.highcharts;

import org.dangcat.chart.TimeType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class ChartDemoUtils
{
    private static String[][] areas = { { "中南海1", "中南海2" }, { "白下区", "建邺区", "秦淮区", "雨花区" }, { "黄埔区", "卢湾区", "闵行区", "徐汇区" }, { "嘴皮子区" } };
    private static String[] categories = { "Firefox", "IE", "Chrome", "Safari", "Opera", "Tom", "Jerry", "Mike", "MH370", "Others" };
    private static String[] categoryTitles = { "火狐", "IE", "谷歌", "Safari", "Opera", "小猫", "老鼠", "Mike", "MH370", "其它" };
    private static String[] cities = { "北京", "南京", "上海", "天津" };
    private static String[] octetsValueColumns = { "userOnline", "downPackets", "upPackets", "downOctets", "upOctets" };
    private static String[] octetsValueTitles = { "在线人数", "下行包数", "上行包数", "下行流量", "上行流量" };

    protected static void createCategoryColumnData(ChartOption chartOption)
    {
        Map<String, Long> data = new HashMap<String, Long>();
        for (int i = 0; i < categories.length; i++)
        {
            long value = getRandomValue((i + 1) * 100000000);
            if (value != 0)
                data.put(categories[i], value);
        }
        chartOption.setData(data);
    }

    protected static void createCategoryColumns(BarChartOption barChartOption)
    {
        barChartOption.addCategoryColumn(new Column("name"));
    }

    protected static void createCategoryRowData(ChartOption chartOption)
    {
        Collection<DataValue> data = new ArrayList<DataValue>();
        for (int i = 0; i < categories.length; i++)
        {
            long value = getRandomValue((i + 1) * 100000000);
            if (value != 0)
                data.add(new DataValue(categoryTitles[i], value));
        }
        chartOption.setData(data);
    }

    protected static void createCityPrimaryKeyColumns(ChartOption chartOption)
    {
        chartOption.addPrimaryKeyColumn(new Column("city"));
        chartOption.addPrimaryKeyColumn(new Column("area"));
    }

    protected static void createOctetsValueColumns(ChartOption chartOption)
    {
        for (Column column : getOctetsValueColumns())
            chartOption.addValueColumn(column);
    }

    protected static void createPacketsValueColumns(ChartOption chartOption)
    {
        for (int i = 0; i < octetsValueColumns.length; i++)
            chartOption.addValueColumn(new Column(octetsValueColumns[i], octetsValueTitles[i], "octets"));
    }

    protected static void createPrimaryKeyColumns(ChartOption chartOption)
    {
        chartOption.addPrimaryKeyColumn(new Column("name"));
    }

    protected static void createStackedCategoryRowData(BarChartOption barChartOption)
    {
        for (Column column : getOctetsValueColumns())
            barChartOption.addCategoryColumn(column);
    }

    protected static void createStackedColumnData(ChartOption chartOption)
    {
        Collection<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < cities.length; i++)
        {
            String city = cities[i];
            for (int j = 0; j < areas[i].length; j++)
            {
                String area = areas[i][j];
                Map<String, Object> columnData = new HashMap<String, Object>();
                for (int k = 0; k < octetsValueColumns.length; k++)
                {
                    columnData.put("city", city);
                    columnData.put("area", area);
                    long value = getRandomValue((k + 1) * 100000000);
                    if (value != 0)
                        columnData.put(octetsValueColumns[k], value);
                }
                data.add(columnData);
            }
        }
        chartOption.setData(data);
    }

    protected static void createStackedRowData(ChartOption chartOption)
    {
        Collection<StackedValue> data = new ArrayList<StackedValue>();
        for (int i = 0; i < cities.length; i++)
        {
            String city = cities[i];
            for (int j = 0; j < areas[i].length; j++)
            {
                String area = areas[i][j];
                for (int k = 0; k < octetsValueColumns.length; k++)
                {
                    long value = getRandomValue((k + 1) * 100000000);
                    if (value != 0)
                        data.add(new StackedValue(city, area, octetsValueTitles[k], value));
                }
            }
        }
        chartOption.setData(data);
    }

    private static Object createTimeColumnData(Date date)
    {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("dateTime", date);
        for (int i = 0; i < octetsValueColumns.length; i++)
        {
            long value = getRandomValue((i + 1) * 100000000);
            if (value != 0)
                data.put(octetsValueColumns[i], value);
        }
        return data;
    }

    protected static void createTimeColumnData(TimeChartOption timeChartOption)
    {
        Collection<Object> data = new ArrayList<Object>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeChartOption.getBeginTime());
        while (calendar.getTime().compareTo(timeChartOption.getEndTime()) <= 0)
        {
            data.add(createTimeColumnData(calendar.getTime()));
            calendar.add(Calendar.MILLISECOND, timeChartOption.getTimeStep().intValue());
        }
        timeChartOption.setData(data);
    }

    private static void createTimeRowData(Collection<DateTimeValue> data, Date date)
    {
        for (int i = 0; i < octetsValueColumns.length; i++)
        {
            long value = getRandomValue((i + 1) * 100000000);
            if (value != 0)
                data.add(new DateTimeValue(octetsValueTitles[i], date, value));
        }
    }

    protected static void createTimeRowData(TimeChartOption timeChartOption)
    {
        Collection<DateTimeValue> data = new ArrayList<DateTimeValue>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeChartOption.getBeginTime());
        while (calendar.getTime().compareTo(timeChartOption.getEndTime()) <= 0)
        {
            createTimeRowData(data, calendar.getTime());
            calendar.add(Calendar.MILLISECOND, timeChartOption.getTimeStep().intValue());
        }
        timeChartOption.setData(data);
    }

    protected static void createValueColumn(ChartOption chartOption)
    {
        chartOption.addValueColumn(new Column("value", "弱智值", "octets"));
    }

    protected static void createValueColumns(ChartOption chartOption)
    {
        for (int i = 0; i < categories.length; i++)
            chartOption.addValueColumn(new Column(categories[i], categoryTitles[i]));
    }

    protected static void createYAxisProperties(AxisChartOption axisChartOption)
    {
        axisChartOption.addYAxis(new YAxis("涨薪率"));
    }

    protected static void createYAxisPropertiesArray(AxisChartOption axisChartOption)
    {
        axisChartOption.addYAxis(new YAxis("流量"));
        axisChartOption.addYAxis(new YAxis("包数"));
        axisChartOption.addYAxis(new YAxis("在线数"));
    }

    private static Collection<Column> getOctetsValueColumns()
    {
        Collection<Column> columns = new ArrayList<Column>();
        Column userOnline = new Column("userOnline", "在线人数");
        userOnline.setYAxis(2);
        userOnline.setStack("userOnline");
        columns.add(userOnline);

        Column downPackets = new Column("downPackets", "下行包数");
        downPackets.setYAxis(1);
        downPackets.setStack("packets");
        columns.add(downPackets);

        Column upPackets = new Column("upPackets", "上行包数");
        upPackets.setYAxis(1);
        upPackets.setStack("packets");
        columns.add(upPackets);

        Column downOctets = new Column("downOctets", "下行流量", "octets");
        downOctets.setStack("octets");
        columns.add(downOctets);

        Column upOctets = new Column("upOctets", "上行流量", "octets");
        upOctets.setStack("octets");
        columns.add(upOctets);
        return columns;
    }

    private static long getRandomValue(long baseValue)
    {
        return Math.round(Math.random() * baseValue);
    }

    protected static void setTimeChartOption(TimeChartOption timeChartOption)
    {
        try
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date beginTime = dateFormat.parse("2013-12-01");
            timeChartOption.setBeginTime(beginTime);
            Date endTime = dateFormat.parse("2013-12-31");
            timeChartOption.setEndTime(endTime);
        }
        catch (ParseException e)
        {
        }
        timeChartOption.setTimeType(TimeType.Month);
        timeChartOption.setTimeStep(24 * 3600000l);
    }
}

package org.dangcat.chart.highcharts;

import java.util.Date;

import org.dangcat.chart.TimeType;

public class TimeChartOption extends AxisChartOption
{
    private Date beginTime = null;
    private String dateTimeFieldName = null;
    private Date endTime = null;
    private Long timeStep = null;
    private TimeType timeType = null;
    private boolean useSpline = false;

    public Date getBeginTime()
    {
        return beginTime;
    }

    public String getDateTimeFieldName()
    {
        return dateTimeFieldName;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public Long getTimeStep()
    {
        return timeStep;
    }

    public TimeType getTimeType()
    {
        return timeType;
    }

    public boolean isUseSpline()
    {
        return useSpline;
    }

    public void setBeginTime(Date beginTime)
    {
        this.beginTime = beginTime;
    }

    public void setDateTimeFieldName(String dateTimeFieldName)
    {
        this.dateTimeFieldName = dateTimeFieldName;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public void setTimeStep(Long timeStep)
    {
        this.timeStep = timeStep;
    }

    public void setTimeType(TimeType timeType)
    {
        this.timeType = timeType;
    }

    public void setUseSpline(boolean useSpline)
    {
        this.useSpline = useSpline;
    }
}

package org.dangcat.chart.highcharts;

import org.dangcat.chart.TimeType;

import java.util.Date;

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

    public void setBeginTime(Date beginTime)
    {
        this.beginTime = beginTime;
    }

    public String getDateTimeFieldName()
    {
        return dateTimeFieldName;
    }

    public void setDateTimeFieldName(String dateTimeFieldName)
    {
        this.dateTimeFieldName = dateTimeFieldName;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Long getTimeStep()
    {
        return timeStep;
    }

    public void setTimeStep(Long timeStep)
    {
        this.timeStep = timeStep;
    }

    public TimeType getTimeType()
    {
        return timeType;
    }

    public void setTimeType(TimeType timeType)
    {
        this.timeType = timeType;
    }

    public boolean isUseSpline()
    {
        return useSpline;
    }

    public void setUseSpline(boolean useSpline)
    {
        this.useSpline = useSpline;
    }
}

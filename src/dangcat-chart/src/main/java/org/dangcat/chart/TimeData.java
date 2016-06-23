package org.dangcat.chart;

import java.util.Collection;
import java.util.Date;

public class TimeData<T>
{
    private Date beginTime = null;
    private Collection<T> data = null;
    private Date endTime = null;
    private Long timeStep = null;

    public Date getBeginTime()
    {
        return beginTime;
    }

    public void setBeginTime(Date beginTime)
    {
        this.beginTime = beginTime;
    }

    public Collection<T> getData()
    {
        return data;
    }

    public void setData(Collection<T> data)
    {
        this.data = data;
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
}

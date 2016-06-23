package org.dangcat.chart;

import java.util.Date;

import org.dangcat.commons.utils.DateUtils;

/**
 * 时间范围。
 * @author Administrator
 * 
 */
public class TimeRange
{
    private static final int TIMESTEP_COUNT = 120;
    private static final int TIMESTEP_DAY = 12 * 60 * 1000;
    private static final int TIMESTEP_HOUR = 30 * 1000;
    private static final int TIMESTEP_MONTH = 8 * 60 * 60 * 1000;
    private static final int TIMESTEP_WEEK = 3 * 24 * 60 * 1000;
    private static final int TIMESTEP_YEAR = 3 * 24 * 60 * 60 * 1000;
    /** 时间步长。 */
    private Date baseTime = null;
    /** 起始时间。 */
    private Date beginTime = null;
    /** 截止时间。 */
    private Date endTime = null;
    /** 时长。 */
    private long timeLength = 0;
    /** 时间周期。 */
    private Integer timePeriod = null;
    /** 时间步长。 */
    private long timeStep = 0;
    /** 时间类型。 */
    private TimeType timeType = null;

    public TimeRange()
    {
    }

    public TimeRange(TimeType timeType)
    {
        this(timeType, null);
    }

    public TimeRange(TimeType timeType, Date baseTime)
    {
        this.timeType = timeType;
        this.baseTime = baseTime;
        this.calculate();
    }

    public void calculate()
    {
        if (this.timeType == null)
            return;
        Date baseTime = this.getBaseTime();
        if (TimeType.Year.equals(this.timeType))
            this.calculateYear(baseTime);
        else if (TimeType.Month.equals(this.timeType))
            this.calculateMonth(baseTime);
        else if (TimeType.Week.equals(this.timeType))
            this.calculateWeek(baseTime);
        else if (TimeType.Day.equals(this.timeType))
            this.calculateDay(baseTime);
        else if (TimeType.Hour.equals(this.timeType))
            this.calculateHour(baseTime);
        else if (TimeType.Minute.equals(this.timeType))
            this.calculateMinute(baseTime);
        this.timeLength = this.endTime.getTime() - this.beginTime.getTime();
    }

    protected void calculateDay(Date baseTime)
    {
        if (this.timePeriod != null)
        {
            this.beginTime = DateUtils.add(DateUtils.DAY, baseTime, this.timePeriod);
            this.endTime = baseTime;
            this.timeStep = this.getDayTimeStep() * Math.abs(this.timePeriod);
        }
        else
        {
            this.beginTime = DateUtils.getFirstTimeOfDay(baseTime);
            this.endTime = DateUtils.getLastTimeOfDay(baseTime);
            this.timeStep = this.getDayTimeStep();
        }
    }

    protected void calculateHour(Date baseTime)
    {
        if (this.timePeriod != null)
        {
            this.beginTime = DateUtils.add(DateUtils.HOUR, baseTime, this.timePeriod);
            this.endTime = baseTime;
            this.timeStep = this.getHourTimeStep() * Math.abs(this.timePeriod);
        }
        else
        {
            this.beginTime = DateUtils.add(DateUtils.HOUR, baseTime, -1);
            this.endTime = baseTime;
            this.timeStep = this.getHourTimeStep();
        }
    }

    protected void calculateMinute(Date baseTime)
    {
        if (this.timePeriod != null)
        {
            this.beginTime = DateUtils.add(DateUtils.MINUTE, baseTime, this.timePeriod);
            this.endTime = baseTime;
        }
        else
        {
            this.beginTime = DateUtils.add(DateUtils.MINUTE, baseTime, -1);
            this.endTime = baseTime;
        }
        this.timeStep = (this.endTime.getTime() - this.beginTime.getTime()) / this.getTimeStepCount();
    }

    protected void calculateMonth(Date baseTime)
    {
        if (this.timePeriod != null)
        {
            this.beginTime = DateUtils.add(DateUtils.MONTH, baseTime, this.timePeriod);
            this.endTime = baseTime;
            this.timeStep = this.getMonthTimeStep() * Math.abs(this.timePeriod);
        }
        else
        {
            this.beginTime = DateUtils.getFirstDayOfMonth(baseTime);
            this.endTime = DateUtils.getLastDayOfMonth(baseTime);
            this.timeStep = this.getMonthTimeStep();
        }
    }

    protected void calculateWeek(Date baseTime)
    {
        if (this.timePeriod != null)
        {
            this.beginTime = DateUtils.add(DateUtils.WEEK, baseTime, this.timePeriod);
            this.endTime = baseTime;
            this.timeStep = this.getWeekTimeStep() * Math.abs(this.timePeriod);
        }
        else
        {
            this.beginTime = DateUtils.getFirstDayOfWeek(baseTime);
            this.endTime = DateUtils.getLastDayOfWeek(baseTime);
            this.timeStep = this.getWeekTimeStep();
        }
    }

    protected void calculateYear(Date baseTime)
    {
        if (this.timePeriod != null)
        {
            this.beginTime = DateUtils.add(DateUtils.YEAR, baseTime, this.timePeriod);
            this.endTime = baseTime;
            this.timeStep = this.getYearTimeStep() * Math.abs(this.timePeriod);
        }
        else
        {
            this.beginTime = DateUtils.getFirstDayOfYear(baseTime);
            this.endTime = DateUtils.getLastDayOfYear(baseTime);
            this.timeStep = this.getYearTimeStep();
        }
    }

    public Date getBaseTime()
    {
        if (this.baseTime == null)
            return DateUtils.now();
        return this.baseTime;
    }

    public Date getBeginTime()
    {
        return this.beginTime;
    }

    protected int getDayTimeStep()
    {
        return TIMESTEP_DAY;
    }

    public Date getEndTime()
    {
        return this.endTime;
    }

    protected int getHourTimeStep()
    {
        return TIMESTEP_HOUR;
    }

    protected int getMonthTimeStep()
    {
        return TIMESTEP_MONTH;
    }

    public long getTimeLength()
    {
        return this.timeLength;
    }

    public Integer getTimePeriod()
    {
        return this.timePeriod;
    }

    public long getTimeStep()
    {
        return this.timeStep;
    }

    protected int getTimeStepCount()
    {
        return TIMESTEP_COUNT;
    }

    public TimeType getTimeType()
    {
        return this.timeType;
    }

    protected int getWeekTimeStep()
    {
        return TIMESTEP_WEEK;
    }

    protected int getYearTimeStep()
    {
        return TIMESTEP_YEAR;
    }

    public void setBaseTime(Date baseTime)
    {
        this.baseTime = baseTime;
        this.calculate();
    }

    public void setBeginTime(Date beginTime)
    {
        this.beginTime = beginTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public void setTimeLength(long timeLength)
    {
        this.timeLength = timeLength;
    }

    public void setTimePeriod(Integer timePeriod)
    {
        this.timePeriod = timePeriod;
        this.calculate();
    }

    public void setTimeStep(long timeStep)
    {
        this.timeStep = timeStep;
    }

    public void setTimeType(TimeType timeType)
    {
        this.timeType = timeType;
        this.calculate();
    }
}

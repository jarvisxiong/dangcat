package org.dangcat.commons.timer;

import java.util.Calendar;
import java.util.Date;

/**
 * 定时小闹钟。
 * @author dangcat
 * 
 */
public abstract class AlarmClock implements Comparable<AlarmClock>
{
    public static final int LOW_PRIORITY = 200;
    public static final int MAX_PRIORITY = 0;
    public static final int NORMAL_PRIORITY = 100;
    private static final long MIN_ALARM_TIMELENGTH = 100;
    protected boolean isSingleRunnable = true;
    private boolean enabled = true;
    private Date lastAlramTime = new Date();
    private long minAlramTimeLength = MIN_ALARM_TIMELENGTH;
    private Date nextAlramTime = null;
    private int priority = 100;
    private SingleRunnable singleRunnable = null;
    private Object target;

    public AlarmClock()
    {
    }

    public AlarmClock(Object target)
    {
        this.target = target;
    }

    public int compareTo(AlarmClock alarmClock)
    {
        return this.getPriority() - alarmClock.getPriority();
    }

    public boolean couldRun()
    {
        return this.singleRunnable == null || !this.singleRunnable.isRunning();
    }

    protected Date getLastAlramTime()
    {
        return this.lastAlramTime;
    }

    protected void setLastAlramTime(Date lastAlramTime) {
        this.lastAlramTime = lastAlramTime;
    }

    public long getMinAlramTimeLength()
    {
        return this.minAlramTimeLength;
    }

    public void setMinAlramTimeLength(long minAlramTimeLength) {
        this.minAlramTimeLength = minAlramTimeLength;
    }

    public Date getNextAlramTime()
    {
        return this.nextAlramTime;
    }

    protected void setNextAlramTime(Date nextAlramTime) {
        this.nextAlramTime = nextAlramTime;
    }

    public int getPriority()
    {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Runnable getRunnable()
    {
        Runnable runnable = null;
        if (this.target instanceof Runnable)
            runnable = (Runnable) this.target;

        if (this.isSingleRunnable())
        {
            if (this.singleRunnable == null)
                this.singleRunnable = new SingleRunnable(runnable);
            runnable = this.singleRunnable;
        }
        return runnable;
    }

    public Object getTarget()
    {
        return this.target;
    }

    /**
     * 触发目标。
     */
    public void setTarget(Object target) {
        this.target = target;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected boolean isInholdTime(Date alramTime)
    {
        if (this.lastAlramTime == null)
            return false;
        return Math.abs(this.lastAlramTime.getTime() - alramTime.getTime()) > this.getMinAlramTimeLength();
    }

    public boolean isSingleRunnable()
    {
        return this.isSingleRunnable;
    }

    public void setSingleRunnable(boolean isSingleRunnable)
    {
        this.isSingleRunnable = isSingleRunnable;
    }

    /**
     * 是否到达时间。
     */
    public abstract boolean isTimeout(Calendar calendar);

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        info.append("Target = " + this.getTarget().getClass().getSimpleName());
        return info.toString();
    }
}
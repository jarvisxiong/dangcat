package org.dangcat.commons.timer;

import org.dangcat.commons.utils.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * 周期定时器。
 *
 * @author dangcat
 */
public class IntervalAlarmClock extends AlarmClock {
    private long delay = 0l;
    private long interval = 0l;

    public IntervalAlarmClock() {
        super();
    }

    public IntervalAlarmClock(long delay, long interval, Object target) {
        super(target);
        this.delay = delay;
        this.interval = interval;
    }

    public IntervalAlarmClock(long interval, Object target) {
        this(0l, interval, target);
    }

    public IntervalAlarmClock(Object target) {
        this(0l, 0l, target);
    }

    private int diffSecond(Calendar calendar) {
        return (int) (Math.abs(calendar.getTimeInMillis() - this.getLastAlramTime().getTime()) / 1000l);
    }

    public long getDelay() {
        return this.delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getInterval() {
        return this.interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    @Override
    public Date getNextAlramTime() {
        Date nextAlramTime = super.getNextAlramTime();
        if (nextAlramTime == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(this.getLastAlramTime());
            calendar.add(Calendar.SECOND, (int) this.getInterval());
            nextAlramTime = calendar.getTime();
            this.setNextAlramTime(nextAlramTime);
        }
        return nextAlramTime;
    }

    @Override
    public boolean isTimeout(Calendar calendar) {
        if (this.diffSecond(calendar) > this.getDelay() + this.getInterval()) {
            this.setLastAlramTime(calendar.getTime());
            return true;
        }
        return false;
    }

    @Override
    protected void setLastAlramTime(Date lastAlramTime) {
        super.setLastAlramTime(lastAlramTime);
        this.setNextAlramTime(DateUtils.add(DateUtils.SECOND, lastAlramTime, (int) this.getInterval()));
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder(super.toString());
        if (this.getDelay() != 0l)
            info.append("\tDelay =" + this.getDelay());
        info.append("\tInterval =" + this.getInterval());
        return info.toString();
    }
}

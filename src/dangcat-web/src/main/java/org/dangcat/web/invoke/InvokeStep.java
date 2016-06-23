package org.dangcat.web.invoke;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.ValueUtils;

public class InvokeStep
{
    public static final int CANCEL = 4;
    public static final int FINISHED = 3;
    public static final int PROCESSING = 2;
    public static final int WAITING = 1;

    private long beginTime = 0;
    private boolean cancel = false;
    private long current = 0;
    private String name;
    private long timeCost = 0;
    private long total = 0;

    public InvokeStep(String name)
    {
        this.name = name;
    }

    public void cancel()
    {
        if (this.getStatus() != FINISHED)
            this.cancel = true;
    }

    public long getCurrent()
    {
        return current;
    }

    public String getName()
    {
        return name;
    }

    public double getPercent()
    {
        return ValueUtils.isZero(this.total) ? 0 : this.current * 100.0 / this.total;
    }

    public int getStatus()
    {
        if (this.isCancel())
            return CANCEL;

        double process = this.getPercent();
        if (ValueUtils.isZero(process))
            return WAITING;
        if (process >= 99.9999)
            return FINISHED;
        return PROCESSING;
    }

    public long getTimeCost()
    {
        return timeCost;
    }

    public long getTotal()
    {
        return total;
    }

    public void increase()
    {
        this.onChanged(this.current, ++this.current);
    }

    public boolean isCancel()
    {
        return this.cancel;
    }

    public boolean isFinished()
    {
        return this.getStatus() == FINISHED || this.getStatus() == CANCEL;
    }

    private void onChanged(long oldValue, long newValue)
    {
        if (oldValue == 0)
            this.beginTime = DateUtils.currentTimeMillis();
        else
            this.timeCost = DateUtils.currentTimeMillis() - this.beginTime;
    }

    public void setCurrent(long current)
    {
        this.onChanged(this.current, current);
        this.current = current;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }
}

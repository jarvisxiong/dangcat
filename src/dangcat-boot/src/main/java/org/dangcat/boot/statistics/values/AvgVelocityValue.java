package org.dangcat.boot.statistics.values;

import org.dangcat.commons.formator.ValueFormator;
import org.dangcat.commons.formator.VelocityFormator;
import org.dangcat.commons.utils.DateUtils;

public class AvgVelocityValue extends AtomicValue
{
    private long beginTime = DateUtils.currentTimeMillis();
    private AtomicValue count = null;
    private VelocityFormator velocityFormator = new VelocityFormator();

    public AvgVelocityValue(String name, AtomicValue count)
    {
        super(name);
        this.count = count;
    }

    public long caculateVelocity(long value, long timeCost)
    {
        long velocity = 0;
        if (value > 0 && timeCost > 0)
            velocity = (long) (value / (timeCost / 1000.0));
        return velocity;
    }

    public long getCount()
    {
        return this.count.getValue();
    }

    public long getTimeCost()
    {
        return DateUtils.currentTimeMillis() - this.beginTime;
    }

    public long getValue()
    {
        return this.caculateVelocity(this.getCount(), this.getTimeCost());
    }

    @Override
    public ValueFormator getValueFormator()
    {
        return this.velocityFormator;
    }
}

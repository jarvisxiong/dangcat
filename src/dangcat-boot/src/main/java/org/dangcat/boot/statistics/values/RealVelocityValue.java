package org.dangcat.boot.statistics.values;

public class RealVelocityValue extends AvgVelocityValue
{
    private AtomicValue timeCost = null;

    public RealVelocityValue(String name, AtomicValue value, AtomicValue timeCost)
    {
        super(name, value);
        this.timeCost = timeCost;
    }

    public long getTimeCost()
    {
        return this.timeCost.getValue();
    }
}

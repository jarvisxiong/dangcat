package org.dangcat.boot.statistics;

import org.dangcat.boot.statistics.values.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Í³¼Æ»ù´¡¡£
 * @author dangcat
 * 
 */
public abstract class StatisticsBase
{
    public static final String TimeCost = "TimeCost";
    public static final String Velocity = "Velocity";

    private Map<String, AtomicValue> atomicValueMap = new LinkedHashMap<String, AtomicValue>();
    private String name;

    public StatisticsBase(String name)
    {
        this.name = name;
    }

    protected void addCount(String name)
    {
        this.atomicValueMap.put(name, new CountValue(name));
    }

    protected void addRecord(String name)
    {
        this.atomicValueMap.put(name, new RecordValue(name));
    }

    protected void addTime(String name)
    {
        this.atomicValueMap.put(name, new TimeLengthValue(name));
    }

    protected void addVelocity(String name, String countName)
    {
        AtomicValue count = this.atomicValueMap.get(countName);
        this.atomicValueMap.put(name, new AvgVelocityValue(name, count));
    }

    protected void addVelocity(String name, String countName, String timeCostName)
    {
        AtomicValue count = this.atomicValueMap.get(countName);
        AtomicValue timeCost = this.atomicValueMap.get(timeCostName);
        this.atomicValueMap.put(name, new RealVelocityValue(name, count, timeCost));
    }

    public void assign(StatisticsBase statisticsBase)
    {
        for (Map.Entry<String, AtomicValue> entry : this.atomicValueMap.entrySet())
        {
            if (entry.getValue() instanceof CountValue)
            {
                CountValue detCountValue = (CountValue) entry.getValue();
                CountValue srcCountValue = (CountValue) statisticsBase.atomicValueMap.get(entry.getKey());
                detCountValue.increase(srcCountValue.getValue());
            }
            else if (entry.getValue() instanceof RecordValue)
            {
                RecordValue detRecordValue = (RecordValue) entry.getValue();
                RecordValue srcRecordValue = (RecordValue) statisticsBase.atomicValueMap.get(entry.getKey());
                detRecordValue.setValue(srcRecordValue.getValue());
            }
        }
    }

    public long begin()
    {
        return this.begin(TimeCost);
    }

    public long begin(String name)
    {
        return ((TimeLengthValue) this.getAtomicValue(name)).begin();
    }

    public void end()
    {
        this.end(TimeCost);
    }

    public void end(long beginTime)
    {
        this.end(TimeCost, beginTime);
    }

    public void end(String name)
    {
        ((TimeLengthValue) this.getAtomicValue(name)).end();
    }

    public void end(String name, long beginTime)
    {
        ((TimeLengthValue) this.getAtomicValue(name)).end(beginTime);
    }

    @SuppressWarnings("unchecked")
    public <T extends AtomicValue> T getAtomicValue(String name)
    {
        return (T) this.atomicValueMap.get(name);
    }

    public String getName()
    {
        return this.name;
    }

    public long getValue(String name)
    {
        return this.getAtomicValue(name).getValue();
    }

    public long increase(String name)
    {
        return ((CountValue) this.getAtomicValue(name)).increase();
    }

    public long increase(String name, long value)
    {
        return ((CountValue) this.getAtomicValue(name)).increase(value);
    }

    public long increaseTimeCost(long timeCost)
    {
        return this.increase(TimeCost, timeCost);
    }

    public boolean isValid()
    {
        for (AtomicValue atomicValue : this.atomicValueMap.values())
        {
            if (atomicValue.isValid())
                return true;
        }
        return false;
    }

    public long setValue(String name, long value)
    {
        return ((RecordValue) this.getAtomicValue(name)).setValue(value);
    }

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        for (AtomicValue atomicValue : this.atomicValueMap.values())
        {
            if (atomicValue.isValid())
            {
                if (info.length() == 0)
                {
                    info.append(this.getName());
                    info.append(" - ");
                }
                else
                    info.append(", ");
                info.append(atomicValue);
            }
        }
        return info.toString();
    }
}

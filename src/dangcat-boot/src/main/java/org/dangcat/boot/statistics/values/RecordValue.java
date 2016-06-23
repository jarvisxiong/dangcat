package org.dangcat.boot.statistics.values;

import java.util.concurrent.atomic.AtomicLong;

public class RecordValue extends AtomicValue
{
    private AtomicLong value = new AtomicLong(0);

    public RecordValue(String name)
    {
        super(name);
    }

    public long getValue()
    {
        return this.value.get();
    }

    public long setValue(long value)
    {
        return this.value.getAndSet(value);
    }
}

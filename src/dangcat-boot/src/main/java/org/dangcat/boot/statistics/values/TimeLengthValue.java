package org.dangcat.boot.statistics.values;

import org.dangcat.commons.formator.TimeLengthFormator;
import org.dangcat.commons.formator.ValueFormator;
import org.dangcat.commons.utils.DateUtils;

public class TimeLengthValue extends CountValue
{
    private long beginTime = DateUtils.currentTimeMillis();
    private TimeLengthFormator timeLengthFormator = new TimeLengthFormator();

    public TimeLengthValue(String name)
    {
        super(name);
    }

    public long begin()
    {
        this.beginTime = DateUtils.currentTimeMillis();
        return this.beginTime;
    }

    public void end()
    {
        this.end(this.beginTime);
    }

    public long end(long beginTime)
    {
        return this.increase(DateUtils.currentTimeMillis() - beginTime);
    }

    @Override
    public ValueFormator getValueFormator()
    {
        return this.timeLengthFormator;
    }
}

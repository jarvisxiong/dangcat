package org.dangcat.boot.statistics.values;

import org.dangcat.commons.formator.ValueFormator;

public abstract class AtomicValue
{
    private String name = null;
    private ValueFormator valueFormator = new ValueFormator();

    public AtomicValue(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public abstract long getValue();

    public ValueFormator getValueFormator()
    {
        return this.valueFormator;
    }

    public boolean isValid()
    {
        return this.getValue() > 0;
    }

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        info.append(this.getName());
        info.append(" = ");
        info.append(this.getValueFormator().format(this.getValue()));
        return info.toString();
    }
}

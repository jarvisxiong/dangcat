package org.dangcat.chart.jfreechart.data;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

class RowCollection
{
    private Map<Comparable<?>, Number> valueMap = new LinkedHashMap<Comparable<?>, Number>();

    protected Collection<Comparable<?>> getColumnKeys()
    {
        return this.valueMap.keySet();
    }

    protected Number getValue(Comparable<?> columnKey)
    {
        return this.valueMap.get(columnKey);
    }

    protected void putValue(Comparable<?> columnKey, Number value)
    {
        this.valueMap.put(columnKey, value);
    }
}

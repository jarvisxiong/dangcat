package org.dangcat.chart.jfreechart.data;

import java.util.HashMap;
import java.util.Map;

public class TransRateMap
{
    private Map<Comparable<?>, Double> columnTransRateMap = null;
    private Map<Comparable<?>, Double> rowTransRateMap = null;
    private Map<Comparable<?>, Map<Comparable<?>, Double>> valueTransRateMap = null;

    public void addColumnTransRate(Comparable<?> columnKey, Double value)
    {
        if (value != null)
        {
            if (this.columnTransRateMap == null)
                this.columnTransRateMap = new HashMap<Comparable<?>, Double>();
            this.columnTransRateMap.put(columnKey, value);
        }
    }

    public void addRowTransRate(Comparable<?> rowKey, Double value)
    {
        if (value != null)
        {
            if (this.rowTransRateMap == null)
                this.rowTransRateMap = new HashMap<Comparable<?>, Double>();
            this.rowTransRateMap.put(rowKey, value);
        }
    }

    public void addTransRate(Comparable<?> rowKey, Comparable<?> columnKey, Double value)
    {
        if (value != null)
        {
            if (this.valueTransRateMap == null)
                this.valueTransRateMap = new HashMap<Comparable<?>, Map<Comparable<?>, Double>>();
            Map<Comparable<?>, Double> columnTransRateMap = this.valueTransRateMap.get(rowKey);
            if (columnTransRateMap == null)
            {
                columnTransRateMap = new HashMap<Comparable<?>, Double>();
                this.valueTransRateMap.put(rowKey, columnTransRateMap);
            }
            columnTransRateMap.put(columnKey, value);
        }
    }

    public double getValue(Comparable<?> rowKey, Comparable<?> columnKey, double value)
    {
        if (this.valueTransRateMap != null)
        {
            Map<Comparable<?>, Double> columnTransRateMap = this.valueTransRateMap.get(rowKey);
            if (columnTransRateMap != null)
                value = this.getValue(columnTransRateMap, columnKey, value);
        }
        value = this.getValue(this.columnTransRateMap, columnKey, value);
        return this.getValue(this.rowTransRateMap, rowKey, value);
    }

    private double getValue(Map<Comparable<?>, Double> transRateMap, Comparable<?> key, double value)
    {
        if (transRateMap != null)
        {
            Double transRate = transRateMap.get(key);
            if (transRate != null)
                value *= transRate.doubleValue();
        }
        return value;
    }
}

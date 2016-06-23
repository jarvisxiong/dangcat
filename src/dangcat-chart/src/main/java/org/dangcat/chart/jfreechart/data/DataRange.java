package org.dangcat.chart.jfreechart.data;

import org.dangcat.commons.utils.MathUtils;
import org.dangcat.commons.utils.ValueUtils;

import java.util.HashMap;
import java.util.Map;

class DataRange
{
    private static final Integer ZERO = 0;
    private ValueRange rowRange = new ValueRange();
    private Map<Comparable<?>, ValueRange> rowRangeMap = new HashMap<Comparable<?>, ValueRange>();
    private ValueRange totalRange = new ValueRange();
    private Map<Comparable<?>, ValueRange> totalRangeMap = new HashMap<Comparable<?>, ValueRange>();

    protected void clear()
    {
        this.rowRange.clear();
        this.rowRangeMap.clear();
        this.totalRange.clear();
        this.totalRangeMap.clear();
    }

    protected Number getMaxValue()
    {
        Number value = this.totalRange.max;
        for (ValueRange valueRange : this.totalRangeMap.values())
        {
            if (ValueUtils.compare(valueRange.max, value) > 0)
                value = valueRange.max;
        }
        return value;
    }

    protected void setMaxValue(Number value) {
        this.totalRange.max = value;
    }

    protected Number getMinValue()
    {
        Number value = this.totalRange.min;
        for (ValueRange valueRange : this.totalRangeMap.values())
        {
            if (ValueUtils.compare(valueRange.min, value) > 0)
                value = valueRange.min;
        }
        return value;
    }

    protected void setMinValue(Number value) {
        this.totalRange.min = value;
    }

    protected Number getRowMax(Comparable<?>... rowKeys)
    {
        Number value = this.rowRange.max;
        if (rowKeys == null || rowKeys.length == 0)
            rowKeys = this.rowRangeMap.keySet().toArray(new Comparable<?>[0]);

        for (Comparable<?> rowKey : rowKeys)
        {
            Number number = this.getValueRange(this.rowRangeMap, rowKey).max;
            if (ValueUtils.compare(number, value) > 0)
                value = number;
        }
        return value;
    }

    protected Number getRowMin(Comparable<?>... rowKeys)
    {
        Number value = this.rowRange.min;
        if (rowKeys == null || rowKeys.length == 0)
            rowKeys = this.rowRangeMap.keySet().toArray(new Comparable<?>[0]);

        for (Comparable<?> rowKey : rowKeys)
        {
            Number number = this.getValueRange(this.rowRangeMap, rowKey).min;
            if (ValueUtils.compare(number, value) < 0)
                value = number;
        }
        return value;
    }

    private ValueRange getValueRange(Map<Comparable<?>, ValueRange> rangeMap, Comparable<?> key)
    {
        ValueRange valueRange = rangeMap.get(key);
        if (valueRange == null)
        {
            valueRange = new ValueRange();
            rangeMap.put(key, valueRange);
        }
        return valueRange;
    }

    protected void putValue(Comparable<?> rowKey, Comparable<?> columnKey, Number value)
    {
        this.getValueRange(this.rowRangeMap, rowKey).putValue(value);
        this.getValueRange(this.totalRangeMap, columnKey).sum(value);
    }

    protected void setRowMaxValue(Number value, Comparable<?>... rowKeys)
    {
        if (rowKeys == null || rowKeys.length == 0)
            this.rowRange.max = value;
        else
        {
            for (Comparable<?> rowKey : rowKeys)
                this.getValueRange(this.rowRangeMap, rowKey).max = value;
        }
    }

    protected void setRowMinValue(Number value, Comparable<?>... rowKeys)
    {
        if (rowKeys == null || rowKeys.length == 0)
            this.rowRange.min = value;
        else
        {
            for (Comparable<?> rowKey : rowKeys)
                this.getValueRange(this.rowRangeMap, rowKey).min = value;
        }
    }

    class ValueRange {
        private Number max = ZERO;
        private Number min = ZERO;

        protected void clear() {
            this.max = ZERO;
            this.max = ZERO;
        }

        protected void putValue(Number value) {
            if (ValueUtils.compare(value, this.max) > 0)
                this.max = value;
            if (ValueUtils.compare(value, this.min) < 0)
                this.min = value;
        }

        protected void sum(Number value) {
            if (ValueUtils.compare(value, ZERO) > 0)
                this.max = MathUtils.plus(this.max, value);
            if (ValueUtils.compare(value, ZERO) < 0)
                this.min = MathUtils.plus(this.min, value);
        }
    }
}

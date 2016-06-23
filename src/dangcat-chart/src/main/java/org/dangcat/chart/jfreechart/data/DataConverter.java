package org.dangcat.chart.jfreechart.data;

import org.dangcat.commons.formator.ValueFormator;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.Column;

public class DataConverter
{
    private DataModule dataModule = null;
    private String[] fieldNames = null;
    private Number maxNumber = null;
    private Number minNumber = null;
    private String title = null;
    private double transRate = 1.0;
    private TransRateMap transRateMap = null;
    private String unit = null;

    public DataConverter(DataModule dataModule)
    {
        this.dataModule = dataModule;
    }

    public DataConverter(String fieldName)
    {
        this.fieldNames = new String[] { fieldName };
    }

    public DataConverter(String title, String[] fieldNames)
    {
        this.title = title;
        this.fieldNames = fieldNames;
    }

    public void calculate(boolean isStacked)
    {
        ValueFormator valueFormator = this.dataModule.getValueFormator();
        if (valueFormator != null)
        {
            String[] rowKeys = null;
            if (this.fieldNames != null && this.fieldNames.length > 0)
                rowKeys = this.fieldNames;

            Number maxNumber = this.dataModule.getRowMaxValue(rowKeys);
            Number minNumber = this.dataModule.getRowMinValue(rowKeys);
            if (maxNumber != null)
            {
                long longValue = maxNumber.longValue();
                if (Math.abs(minNumber.longValue()) > longValue)
                    longValue = Math.abs(minNumber.longValue());
                this.unit = valueFormator.calculatePerfectUnit(longValue);
                this.transRate = valueFormator.calculateTransRate(this.unit);
            }

            if (isStacked)
                this.maxNumber = this.dataModule.getMaxValue();
            else
                this.maxNumber = maxNumber;

            if (isStacked)
                this.minNumber = this.dataModule.getMinValue();
            else
                this.minNumber = minNumber;
        }
    }

    public DataModule getDataModule()
    {
        return dataModule;
    }

    public void setDataModule(DataModule dataModule) {
        this.dataModule = dataModule;
    }

    public String[] getFieldNames()
    {
        return fieldNames;
    }

    public Number getMaxNumber()
    {
        return this.maxNumber;
    }

    public void setMaxNumber(Number maxNumber) {
        this.maxNumber = maxNumber;
    }

    public double getMaxValue()
    {
        return this.getTransValue(this.getMaxNumber());
    }

    public Number getMinNumber()
    {
        return this.minNumber;
    }

    public void setMinNumber(Number minNumber) {
        this.minNumber = minNumber;
    }

    public double getMinValue()
    {
        return this.getTransValue(this.getMinNumber());
    }

    public String getRangeTitle(String rangeTitle)
    {
        if (!ValueUtils.isEmpty(this.title))
            rangeTitle = this.title;
        if (ValueUtils.isEmpty(this.title) && this.fieldNames != null && this.fieldNames.length == 1)
        {
            Column column = this.getDataModule().getDataReader().getColumns().find(this.fieldNames[0]);
            if (column != null)
                rangeTitle = column.getTitle();
        }
        if (ValueUtils.isEmpty(rangeTitle))
        {
            Column column = this.getDataModule().getNumberColumn();
            if (column != null)
                rangeTitle = column.getTitle();
        }
        if (ValueUtils.isEmpty(rangeTitle))
            rangeTitle = "";
        if (!ValueUtils.isEmpty(this.unit))
            rangeTitle = String.format("%1s(%2s)", rangeTitle, this.unit);
        return rangeTitle;
    }

    private double getTransRate()
    {
        return this.transRate;
    }

    public TransRateMap getTransRateMap()
    {
        return this.transRateMap;
    }

    public void setTransRateMap(TransRateMap transRateMap) {
        this.transRateMap = transRateMap;
    }

    private double getTransValue(Number number)
    {
        double value = 0.0;
        if (number != null)
            value = number.doubleValue() * this.getTransRate();
        return value;
    }

    public String getUnit()
    {
        return unit;
    }

    public double getValue(Comparable<?> rowKey, Comparable<?> columnKey)
    {
        Number number = this.dataModule.getValue(rowKey, columnKey);
        double value = this.getTransValue(number);
        return this.transRateMap == null ? value : this.transRateMap.getValue(rowKey, columnKey, value);
    }
}

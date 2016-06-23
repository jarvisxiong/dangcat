package org.dangcat.chart.highcharts;

import java.util.Collection;
import java.util.LinkedList;

public class BarChartOption extends AxisChartOption
{
    private Collection<Column> categoryColumns = null;
    private Boolean showDataLabels = null;
    private Boolean stacked = null;

    public void addCategoryColumn(Column column)
    {
        if (column != null)
        {
            if (this.categoryColumns == null)
                this.categoryColumns = new LinkedList<Column>();
            if (!this.categoryColumns.contains(column))
                this.categoryColumns.add(column);
        }
    }

    public Collection<Column> getCategoryColumns()
    {
        return categoryColumns;
    }

    public Boolean getShowDataLabels()
    {
        return showDataLabels;
    }

    public void setShowDataLabels(Boolean showDataLabels)
    {
        this.showDataLabels = showDataLabels;
    }

    public Boolean getStacked()
    {
        return stacked;
    }

    public void setStacked(Boolean stacked)
    {
        this.stacked = stacked;
    }

    @Override
    public ChartType getType()
    {
        return ChartType.Bar;
    }
}

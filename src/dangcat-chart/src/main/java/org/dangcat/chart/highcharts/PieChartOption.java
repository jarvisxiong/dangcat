package org.dangcat.chart.highcharts;

public class PieChartOption extends ChartOption
{
    private Boolean showDataLabels = null;
    private Boolean showPercent = null;

    public Boolean getShowDataLabels()
    {
        return showDataLabels;
    }

    public void setShowDataLabels(Boolean showDataLabels)
    {
        this.showDataLabels = showDataLabels;
    }

    public Boolean getShowPercent()
    {
        return showPercent;
    }

    public void setShowPercent(Boolean showPercent)
    {
        this.showPercent = showPercent;
    }

    @Override
    public ChartType getType()
    {
        return ChartType.Pie;
    }
}

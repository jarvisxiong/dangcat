package org.dangcat.chart.highcharts;

public class AreaChartOption extends TimeChartOption
{
    private boolean useSpline = false;

    @Override
    public ChartType getType()
    {
        return this.isUseSpline() ? ChartType.AreaSpline : ChartType.Area;
    }

    public boolean isUseSpline()
    {
        return useSpline;
    }

    public void setUseSpline(boolean useSpline)
    {
        this.useSpline = useSpline;
    }
}

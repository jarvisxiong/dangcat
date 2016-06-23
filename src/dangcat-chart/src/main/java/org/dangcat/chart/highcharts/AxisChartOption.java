package org.dangcat.chart.highcharts;

import java.util.Collection;
import java.util.LinkedList;

import org.dangcat.commons.serialize.annotation.Serialize;

public class AxisChartOption extends ChartOption
{
    private Boolean inverted = null;
    private Collection<YAxis> yAxisProperties = null;

    public void addYAxis(YAxis yAxis)
    {
        if (yAxis != null)
        {
            if (this.yAxisProperties == null)
                this.yAxisProperties = new LinkedList<YAxis>();
            if (!this.yAxisProperties.contains(yAxis))
                this.yAxisProperties.add(yAxis);
        }
    }

    public Boolean getInverted()
    {
        return inverted;
    }

    @Serialize(name = "yAxisProperties")
    public Collection<YAxis> getYAxisProperties()
    {
        return yAxisProperties;
    }

    public void setInverted(Boolean inverted)
    {
        this.inverted = inverted;
    }
}

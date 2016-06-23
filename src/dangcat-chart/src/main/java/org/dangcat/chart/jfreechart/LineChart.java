package org.dangcat.chart.jfreechart;

import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

public class LineChart extends TimeSeriesChart
{
    protected XYItemRenderer createXYItemRenderer()
    {
        return new XYLineAndShapeRenderer(true, false);
    }

    @Override
    public void load()
    {
        this.load(false);
    }
}

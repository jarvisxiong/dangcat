package org.dangcat.chart.jfreechart;

import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYItemRenderer;

public class AreaChart extends TimeXYChart
{
    @Override
    protected XYItemRenderer createXYItemRenderer()
    {
        return new StackedXYAreaRenderer2();
    }

    @Override
    protected void initPlot(Plot plot)
    {
        super.initPlot(plot);
        // …Ë÷√∞ÎÕ∏√˜
        plot.setForegroundAlpha(0.7f);
    }

    @Override
    public void load()
    {
        this.load(true);
    }
}

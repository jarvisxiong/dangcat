package org.dangcat.chart.jfreechart;

import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;

import java.awt.*;

public class DiffLineChart extends TimeXYChart
{
    private Paint negativePaint = new Color(255, 0, 0, 70);
    private Paint positivePaint = Color.green;

    @Override
    protected XYItemRenderer createXYItemRenderer()
    {
        return new XYDifferenceRenderer(this.getPositivePaint(), this.getNegativePaint(), false);
    }

    public Paint getNegativePaint()
    {
        return negativePaint;
    }

    public void setNegativePaint(Paint negativePaint) {
        this.negativePaint = negativePaint;
    }

    public Paint getPositivePaint()
    {
        return positivePaint;
    }

    public void setPositivePaint(Paint positivePaint) {
        this.positivePaint = positivePaint;
    }

    @Override
    protected void iniItemRenderer(XYItemRenderer xyItemRenderer, int i)
    {
        super.iniItemRenderer(xyItemRenderer, i);

        XYDifferenceRenderer xyDifferenceRenderer = (XYDifferenceRenderer) xyItemRenderer;
        xyDifferenceRenderer.setRoundXCoordinates(true);
    }

    @Override
    protected void initPlot(Plot plot)
    {
        super.initPlot(plot);

        XYPlot xyPlot = (XYPlot) plot;
        xyPlot.setDomainCrosshairLockedOnData(true);
        xyPlot.setRangeCrosshairLockedOnData(true);
        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);
    }

    @Override
    public void load()
    {
        this.load(false);
    }
}

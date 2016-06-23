package org.dangcat.chart.jfreechart;

import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;

import java.util.Date;

class CombinedUnit
{
    private TimeChart timeChart = null;
    private int weight = 1;
    private XYPlot xyPlot = new XYPlot();

    CombinedUnit(TimeChart timeChart, int weight)
    {
        this.timeChart = timeChart;
        this.weight = weight;
        timeChart.createChart();
    }

    protected int getCount()
    {
        return this.getTimeChart().getDataModule().getRowKeys().size();
    }

    protected TimeChart getTimeChart()
    {
        return timeChart;
    }

    protected int getWeight()
    {
        return weight;
    }

    protected XYPlot getXYPlot()
    {
        return this.xyPlot;
    }

    protected void initialize()
    {
        this.timeChart.getDataModule().initialize();
    }

    protected void initPlot()
    {
        this.timeChart.setXYPlot(this.xyPlot);
        this.xyPlot.setRangeAxis(new NumberAxis());
        this.xyPlot.setRenderer(this.timeChart.createXYItemRenderer());
        this.timeChart.initPlot(this.xyPlot);
    }

    protected void load()
    {
        this.timeChart.load();
    }

    protected void setBeginColorIndex(int beginColorIndex)
    {
        this.timeChart.setBeginColorIndex(beginColorIndex);
    }

    protected void setBeginTime(Date beginTime)
    {
        this.timeChart.setBeginTime(beginTime);
    }

    protected void setDomainAxis(DateAxis domainAxis)
    {
        this.xyPlot.setDomainAxis(domainAxis);
    }

    protected void setEndTime(Date endTime)
    {
        this.timeChart.setEndTime(endTime);
    }

    protected void setTimeStep(int timeStep)
    {
        this.timeChart.setTimeStep(timeStep);
    }
}

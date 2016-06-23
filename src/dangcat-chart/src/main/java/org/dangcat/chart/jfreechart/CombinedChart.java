package org.dangcat.chart.jfreechart;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

public class CombinedChart extends TimeChart {
    private Collection<CombinedUnit> combinedUnits = new LinkedList<CombinedUnit>();

    public void addTimeChart(TimeChart timeChart) {
        this.addTimeChart(timeChart, 1);
    }

    public void addTimeChart(TimeChart timeChart, int weight) {
        this.combinedUnits.add(new CombinedUnit(timeChart, weight));
    }

    @Override
    protected JFreeChart createChart() {
        // 横坐标标题。
        DateAxis domainAxis = new DateAxis(this.getDomainTitle());
        // 绘图对象
        CombinedDomainXYPlot combinedDomainXYPlot = new CombinedDomainXYPlot(domainAxis);
        // 各图形间距。
        combinedDomainXYPlot.setGap(10.0);

        // 构建具体联合图形。
        for (CombinedUnit combinedUnit : this.combinedUnits) {
            combinedUnit.setDomainAxis(domainAxis);
            combinedDomainXYPlot.add(combinedUnit.getXYPlot(), combinedUnit.getWeight());
        }
        return new JFreeChart(combinedDomainXYPlot);
    }

    @Override
    protected XYDataset createXYDataset(Collection<Comparable<?>> rowKeys) {
        return null;
    }

    @Override
    protected XYItemRenderer createXYItemRenderer() {
        return null;
    }

    @Override
    public void initialize() {
        for (CombinedUnit combinedUnit : this.combinedUnits)
            combinedUnit.initialize();
        super.initialize();
    }

    @Override
    protected void initPlot(Plot plot) {
        super.initPlot(plot);

        for (CombinedUnit combinedUnit : this.combinedUnits)
            combinedUnit.initPlot();
    }

    @Override
    protected void initSeriesPaint() {
        int beginColorIndex = 0;
        for (CombinedUnit combinedUnit : this.combinedUnits) {
            combinedUnit.setBeginColorIndex(beginColorIndex);
            beginColorIndex += combinedUnit.getCount();
        }
    }

    @Override
    public void load() {
        for (CombinedUnit combinedUnit : this.combinedUnits)
            combinedUnit.load();
    }

    @Override
    public void setBeginTime(Date beginTime) {
        super.setBeginTime(beginTime);

        for (CombinedUnit combinedUnit : this.combinedUnits)
            combinedUnit.setBeginTime(beginTime);
    }

    @Override
    public void setEndTime(Date endTime) {
        super.setEndTime(endTime);

        for (CombinedUnit combinedUnit : this.combinedUnits)
            combinedUnit.setEndTime(endTime);
    }

    @Override
    public void setTimeStep(int timeStep) {
        super.setTimeStep(timeStep);

        for (CombinedUnit combinedUnit : this.combinedUnits)
            combinedUnit.setTimeStep(timeStep);
    }
}

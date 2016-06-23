package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.DataConverter;
import org.dangcat.chart.jfreechart.data.DataModule;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.CategoryTableXYDataset;
import org.jfree.data.xy.XYDataset;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class MultiLineChart extends TimeSeriesChart
{
    @Override
    protected JFreeChart createChart()
    {
        this.initDataConverters();

        // 区域图的表现模块。
        XYPlot xyPlot = new XYPlot();
        // 横坐标标题。
        xyPlot.setDomainAxis(new DateAxis(this.getDomainTitle()));

        for (int i = 0; i < this.getDataConverters().size(); i++)
        {
            CategoryTableXYDataset categoryTableXYDataset = new CategoryTableXYDataset();
            xyPlot.setDataset(i, categoryTableXYDataset);
            // 初始化纵坐标。
            xyPlot.setRangeAxis(i, new NumberAxis());
            xyPlot.mapDatasetToRangeAxis(i, i);

            xyPlot.setRenderer(i, this.createXYItemRenderer());
        }
        // 构建统计图对象。
        return new JFreeChart(xyPlot);
    }

    @Override
    protected XYItemRenderer createXYItemRenderer()
    {
        return new XYLineAndShapeRenderer(true, true);
    }

    @Override
    protected void iniItemRenderer(XYItemRenderer xyItemRenderer, int i)
    {
        super.iniItemRenderer(xyItemRenderer, i);

        XYLineAndShapeRenderer xyLineAndShapeRenderer = (XYLineAndShapeRenderer) xyItemRenderer;
        xyLineAndShapeRenderer.setBaseShapesVisible(false);
        xyLineAndShapeRenderer.setBaseShapesFilled(true);
        xyLineAndShapeRenderer.setDrawSeriesLineAsPath(true);
    }

    private void initDataConverters()
    {
        DataModule dataModule = this.getDataModule();

        List<DataConverter> dataConverters = this.getDataConverters();
        if (dataConverters.size() == 0)
        {
            for (Comparable<?> rowKey : dataModule.getRowKeys())
                dataConverters.add(new DataConverter((String) rowKey));
        }
        for (DataConverter dataConverter : dataConverters)
            dataConverter.setDataModule(dataModule);
    }

    @Override
    public void load()
    {
        DataModule dataModule = this.getDataModule();

        List<DataConverter> dataConverters = this.getDataConverters();
        XYPlot xyPlot = (XYPlot) this.getChart().getPlot();
        for (int i = 0; i < dataConverters.size(); i++)
        {
            DataConverter dataConverter = dataConverters.get(i);
            dataConverter.setDataModule(dataModule);
            dataConverter.calculate(false);

            Collection<Comparable<?>> fieldNames = new HashSet<Comparable<?>>();
            for (String fieldName : dataConverter.getFieldNames())
                fieldNames.add(fieldName);

            XYDataset xyDataset = this.createXYDataset(fieldNames);
            if (xyDataset != null)
                xyPlot.setDataset(i, xyDataset);
        }
        this.initPlot(xyPlot);
    }
}

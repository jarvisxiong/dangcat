package org.dangcat.chart.highcharts;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.dangcat.commons.serialize.json.JsonSerializer;

public class ChartDemo
{
    public static void main(String[] args) throws IOException
    {
        ChartDemo chartDemo = new ChartDemo();
        chartDemo.print();
    }

    private Collection<ChartOption> charts = new LinkedList<ChartOption>();

    public ChartDemo()
    {
        this.createPieChart();
        this.createBarChart();
        this.createStackedBarChart();
        this.createLineChart();
        this.createAreaChart(true);
        this.createAreaChart(null);
    }

    private void createAreaChart(Boolean useSpline)
    {
        AreaChartOption chart1 = new AreaChartOption();
        ChartDemoUtils.createPrimaryKeyColumns(chart1);
        ChartDemoUtils.createValueColumn(chart1);
        if (useSpline != null)
            chart1.setUseSpline(useSpline);
        ChartDemoUtils.createYAxisProperties(chart1);
        ChartDemoUtils.setTimeChartOption(chart1);
        ChartDemoUtils.createTimeRowData(chart1);
        this.charts.add(chart1);

        AreaChartOption chart2 = new AreaChartOption();
        ChartDemoUtils.createPacketsValueColumns(chart2);
        ChartDemoUtils.createYAxisProperties(chart2);
        chart2.setLogic("octets");
        if (useSpline != null)
            chart2.setUseSpline(useSpline);
        ChartDemoUtils.setTimeChartOption(chart2);
        ChartDemoUtils.createTimeColumnData(chart2);
        this.charts.add(chart2);

        AreaChartOption chart3 = new AreaChartOption();
        ChartDemoUtils.createPacketsValueColumns(chart3);
        ChartDemoUtils.createYAxisProperties(chart3);
        ChartDemoUtils.setTimeChartOption(chart3);
        ChartDemoUtils.createTimeColumnData(chart3);
        chart3.setLogic("octets");
        chart3.setInverted(true);
        if (useSpline != null)
            chart3.setUseSpline(useSpline);
        this.charts.add(chart3);
    }

    private void createBarChart()
    {
        BarChartOption chart1 = new BarChartOption();
        ChartDemoUtils.createPrimaryKeyColumns(chart1);
        ChartDemoUtils.createValueColumn(chart1);
        ChartDemoUtils.createCategoryRowData(chart1);
        this.charts.add(chart1);

        BarChartOption chart2 = new BarChartOption();
        ChartDemoUtils.createValueColumns(chart2);
        ChartDemoUtils.createYAxisProperties(chart2);
        chart2.setLogic("octets");
        ChartDemoUtils.createCategoryColumnData(chart2);
        this.charts.add(chart2);

        BarChartOption chart3 = new BarChartOption();
        ChartDemoUtils.createValueColumns(chart3);
        chart3.setLogic("octets");
        chart3.setInverted(true);
        chart3.setTitle("超级无敌福利彩票获利比较图");
        chart3.setSubtitle("The horizontal alignment of the subtitle");
        ChartDemoUtils.createYAxisProperties(chart3);
        ChartDemoUtils.createCategoryColumnData(chart3);
        this.charts.add(chart3);
    }

    private void createLineChart()
    {
        LineChartOption chart1 = new LineChartOption();
        ChartDemoUtils.createPrimaryKeyColumns(chart1);
        ChartDemoUtils.createYAxisProperties(chart1);
        ChartDemoUtils.createValueColumn(chart1);
        ChartDemoUtils.setTimeChartOption(chart1);
        ChartDemoUtils.createTimeRowData(chart1);
        this.charts.add(chart1);

        LineChartOption chart2 = new LineChartOption();
        ChartDemoUtils.createPacketsValueColumns(chart2);
        ChartDemoUtils.createYAxisProperties(chart2);
        ChartDemoUtils.setTimeChartOption(chart2);
        ChartDemoUtils.createTimeColumnData(chart2);
        chart2.setLogic("octets");
        this.charts.add(chart2);

        LineChartOption chart3 = new LineChartOption();
        ChartDemoUtils.createPacketsValueColumns(chart3);
        ChartDemoUtils.createYAxisProperties(chart3);
        ChartDemoUtils.setTimeChartOption(chart3);
        ChartDemoUtils.createTimeColumnData(chart3);
        chart3.setLogic("octets");
        chart3.setInverted(true);
        this.charts.add(chart3);
    }

    private void createPieChart()
    {
        PieChartOption chart1 = new PieChartOption();
        ChartDemoUtils.createPrimaryKeyColumns(chart1);
        ChartDemoUtils.createValueColumn(chart1);
        chart1.setShowPercent(true);
        chart1.setShowLegend(true);
        ChartDemoUtils.createCategoryRowData(chart1);
        this.charts.add(chart1);

        PieChartOption chart2 = new PieChartOption();
        ChartDemoUtils.createValueColumns(chart2);
        chart2.setLogic("octets");
        ChartDemoUtils.createCategoryColumnData(chart2);
        this.charts.add(chart2);

        PieChartOption chart3 = new PieChartOption();
        ChartDemoUtils.createValueColumns(chart3);
        chart3.setLogic("octets");
        chart3.setShowLegend(true);
        chart3.setShowDataLabels(false);
        ChartDemoUtils.createCategoryColumnData(chart3);
        this.charts.add(chart3);
    }

    private void createStackedBarChart()
    {
        BarChartOption chart1 = new BarChartOption();
        ChartDemoUtils.createCityPrimaryKeyColumns(chart1);
        ChartDemoUtils.createCategoryColumns(chart1);
        ChartDemoUtils.createValueColumn(chart1);
        chart1.setStacked(true);
        ChartDemoUtils.createStackedRowData(chart1);
        this.charts.add(chart1);

        BarChartOption chart2 = new BarChartOption();
        ChartDemoUtils.createCityPrimaryKeyColumns(chart2);
        ChartDemoUtils.createStackedCategoryRowData(chart2);
        ChartDemoUtils.createYAxisProperties(chart2);
        chart2.setStacked(true);
        ChartDemoUtils.createStackedColumnData(chart2);
        this.charts.add(chart2);

        BarChartOption chart3 = new BarChartOption();
        ChartDemoUtils.createCityPrimaryKeyColumns(chart3);
        ChartDemoUtils.createStackedCategoryRowData(chart3);
        ChartDemoUtils.createYAxisProperties(chart3);
        chart3.setInverted(true);
        chart3.setStacked(true);
        ChartDemoUtils.createStackedColumnData(chart3);
        this.charts.add(chart3);
    }

    public void print() throws IOException
    {
        File basedir = new File("../dangcat-web/src/main/webapp");
        ChartExporter chartExporter = new ChartExporter(basedir);
        int i = 0;
        for (ChartOption chartOption : this.charts)
        {
            String options = JsonSerializer.serialize(chartOption);
            String data = JsonSerializer.serialize(chartOption);

            File output = new File("log/CharDemo" + (i++) + ".png");
            chartExporter.setWidth(chartOption.getWidth());
            chartExporter.setHeight(chartOption.getHeight());
            chartExporter.export(options, data, output);
        }
    }
}

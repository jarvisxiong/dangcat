package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.DataModule;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;

import javax.swing.*;
import java.util.Date;

public class LineChartDemo extends ChartDemoBase
{
    private static final long serialVersionUID = 1L;
    private static final String TITLE = "线性统计图";

    public LineChartDemo()
    {
        super(LineChart.class.getSimpleName());
    }

    public static void main(final String[] args)
    {
        show(new LineChartDemo());
    }

    @Override
    protected void createTabbedPane(JTabbedPane tabbedPane)
    {
        Date[] dates = SimulateTimeData.today();
        DataModule columnDataModule = SimulateTimeData.createMultiColumnDataModule();
        columnDataModule.getDataReader().setFilterExpress(new FilterUnit(SimulateCategoryData.Name, FilterType.eq, "Name 2"));
        LineChart lineChart1 = new LineChart();
        lineChart1.setTitle(TITLE);
        lineChart1.setRangeTitle("流量");
        lineChart1.setDataModule(columnDataModule);
        this.initTimeChart(lineChart1, dates[0], dates[1]);
        lineChart1.initialize();
        tabbedPane.add("Column Module Data", this.createChartPanel(lineChart1.getChart()));
        this.renderFile(lineChart1, "LineChart1");

        DataModule rowDataModule = SimulateTimeData.createMultiRowDataModule();
        rowDataModule.getDataReader().setFilterExpress(new FilterUnit(SimulateCategoryData.Name, FilterType.eq, "Name 8"));
        LineChart lineChart2 = new LineChart();
        lineChart2.setLegendVisible(true);
        lineChart2.setTitle(TITLE);
        lineChart2.setRangeTitle("流量");
        lineChart2.setDataModule(rowDataModule);
        this.initTimeChart(lineChart2, dates[0], dates[1]);
        lineChart2.initialize();
        tabbedPane.add("Row Module Data", this.createChartPanel(lineChart2.getChart()));
        this.renderFile(lineChart2, "LineChart2");
    }
}

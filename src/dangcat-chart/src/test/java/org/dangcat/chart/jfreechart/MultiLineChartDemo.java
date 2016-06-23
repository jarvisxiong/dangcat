package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.DataConverter;
import org.dangcat.chart.jfreechart.data.DataModule;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;

import javax.swing.*;
import java.util.Date;

public class MultiLineChartDemo extends ChartDemoBase
{
    private static final long serialVersionUID = 1L;
    private static final String TITLE = "多轴线性统计图";

    public MultiLineChartDemo()
    {
        super(MultiLineChart.class.getSimpleName());
    }

    public static void main(final String[] args)
    {
        show(new MultiLineChartDemo());
    }

    @Override
    protected void createTabbedPane(JTabbedPane tabbedPane)
    {
        Date[] dates = SimulateTimeData.today();
        DataModule columnDataModule = SimulateTimeData.createMultiColumnDataModule();
        columnDataModule.getDataReader().setFilterExpress(new FilterUnit(SimulateCategoryData.Name, FilterType.eq, "Name 2"));
        MultiLineChart multiLineChart1 = new MultiLineChart();
        multiLineChart1.setTitle(TITLE);
        multiLineChart1.setRangeTitle("流量");
        multiLineChart1.setDataModule(columnDataModule);
        this.initTimeChart(multiLineChart1, dates[0], dates[1]);
        multiLineChart1.initialize();
        tabbedPane.add("Multi Axis", this.createChartPanel(multiLineChart1.getChart()));
        this.renderFile(multiLineChart1, "MultiLineChart1");

        DataModule rowDataModule = SimulateTimeData.createMultiRowDataModule();
        rowDataModule.getDataReader().setFilterExpress(new FilterUnit(SimulateCategoryData.Name, FilterType.eq, "Name 8"));
        MultiLineChart multiLineChart2 = new MultiLineChart();
        multiLineChart2.setLegendVisible(true);
        multiLineChart2.setTitle(TITLE);
        multiLineChart2.setRangeTitle("流量");
        multiLineChart2.setDataModule(rowDataModule);
        this.initTimeChart(multiLineChart2, dates[0], dates[1]);
        multiLineChart2.getDataConverters().add(
                new DataConverter("Sample Axis", new String[] { SimulateBase.Octets, SimulateBase.OctetsVelocity, SimulateBase.Value, SimulateBase.Velocity, SimulateBase.TimeLength }));
        multiLineChart2.getDataConverters().add(new DataConverter(SimulateBase.Percent));
        multiLineChart2.initialize();
        tabbedPane.add("Combo Axis", this.createChartPanel(multiLineChart2.getChart()));
        this.renderFile(multiLineChart2, "MultiLineChart2");
    }
}

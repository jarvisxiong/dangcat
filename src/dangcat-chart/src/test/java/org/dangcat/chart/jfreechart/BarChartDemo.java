package org.dangcat.chart.jfreechart;

import javax.swing.JTabbedPane;

import org.dangcat.chart.jfreechart.BarChart;
import org.dangcat.chart.jfreechart.data.DataModule;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;

public class BarChartDemo extends ChartDemoBase
{
    private static final long serialVersionUID = 1L;
    private static final String TITLE = "柱状统计图";

    public static void main(final String[] args)
    {
        show(new BarChartDemo());
    }

    public BarChartDemo()
    {
        super(BarChart.class.getSimpleName());
    }

    @Override
    protected void createTabbedPane(JTabbedPane tabbedPane)
    {
        DataModule dataModule1 = SimulateCategoryData.createPieColumnDataModule();
        dataModule1.getDataReader().setFilterExpress(new FilterUnit(SimulateCategoryData.Name, FilterType.eq, "Name 1"));
        BarChart barChart1 = new BarChart();
        barChart1.setTitle(TITLE);
        barChart1.setDataModule(dataModule1);
        barChart1.initialize();
        tabbedPane.add("列数据栏位主键", this.createChartPanel(barChart1.getChart()));
        this.renderFile(barChart1, "BarChart1");

        DataModule dataModule2 = SimulateCategoryData.createPieColumnDataModule();
        dataModule2.getDataReader().setFilterExpress(new FilterUnit(SimulateCategoryData.Name, FilterType.eq, "Name 9"));
        BarChart barChart2 = new BarChart();
        barChart2.setShowItemLabel(false);
        barChart2.setLegendVisible(true);
        barChart2.setTitle(TITLE);
        barChart2.setDataModule(dataModule2);
        barChart2.initialize();
        tabbedPane.add("行数据栏位主键", this.createChartPanel(barChart2.getChart()));
        this.renderFile(barChart2, "BarChart2");
    }
}

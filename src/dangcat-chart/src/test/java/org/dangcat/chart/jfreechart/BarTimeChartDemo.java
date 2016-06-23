package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.DataModule;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;

import javax.swing.*;
import java.util.Date;

public class BarTimeChartDemo extends ChartDemoBase {
    private static final long serialVersionUID = 1L;
    private static final String TITLE = "Öù×´Ç÷ÊÆÍ³¼ÆÍ¼";

    public BarTimeChartDemo() {
        super(BarTimeChart.class.getSimpleName());
    }

    public static void main(final String[] args) {
        show(new BarTimeChartDemo());
    }

    @Override
    protected void createTabbedPane(JTabbedPane tabbedPane) {
        Date[] dates = SimulateTimeData.today();

        DataModule muiltiColumnDataModule = SimulateTimeData.createMultiColumnDataModule();
        muiltiColumnDataModule.getDataReader().setFilterExpress(new FilterUnit(SimulateCategoryData.Name, FilterType.eq, "Name 1"));
        BarTimeChart barTimeChart1 = new BarTimeChart();
        barTimeChart1.setTitle(TITLE);
        barTimeChart1.setDataModule(muiltiColumnDataModule);
        this.initTimeChart(barTimeChart1, dates[0], dates[1]);
        barTimeChart1.initialize();
        tabbedPane.add("MultiColumn Data", this.createChartPanel(barTimeChart1.getChart()));
        this.renderFile(barTimeChart1, "BarTimeChart1");

        DataModule multiRowDataModule = SimulateTimeData.createMultiRowDataModule();
        multiRowDataModule.getDataReader().setFilterExpress(new FilterUnit(SimulateCategoryData.Name, FilterType.eq, "Name 9"));
        BarTimeChart barTimeChart2 = new BarTimeChart();
        barTimeChart2.setShowItemLabel(false);
        barTimeChart2.setLegendVisible(true);
        barTimeChart2.setTitle(TITLE);
        barTimeChart2.setDataModule(multiRowDataModule);
        this.initTimeChart(barTimeChart2, dates[0], dates[1]);
        barTimeChart2.initialize();
        tabbedPane.add("MultiRow Data", this.createChartPanel(barTimeChart2.getChart()));
        this.renderFile(barTimeChart2, "BarTimeChart2");

        DataModule singleColumnDataModule = SimulateTimeData.createSingleColumnDataModule();
        singleColumnDataModule.getDataReader().setFilterExpress(new FilterUnit(SimulateCategoryData.Name, FilterType.eq, "Name 1"));
        BarTimeChart barTimeChart3 = new BarTimeChart();
        barTimeChart3.setTitle(TITLE);
        barTimeChart3.setDataModule(singleColumnDataModule);
        this.initTimeChart(barTimeChart3, dates[0], dates[1]);
        barTimeChart3.initialize();
        tabbedPane.add("SingleColumn Data", this.createChartPanel(barTimeChart3.getChart()));
        this.renderFile(barTimeChart3, "BarTimeChart3");

        DataModule singleRowDataModule = SimulateTimeData.createSingleRowDataModule();
        singleRowDataModule.getDataReader().setFilterExpress(new FilterUnit(SimulateCategoryData.Name, FilterType.eq, "Name 9"));
        BarTimeChart barTimeChart4 = new BarTimeChart();
        barTimeChart4.setShowItemLabel(false);
        barTimeChart4.setLegendVisible(true);
        barTimeChart4.setTitle(TITLE);
        barTimeChart4.setDataModule(singleRowDataModule);
        this.initTimeChart(barTimeChart4, dates[0], dates[1]);
        barTimeChart4.initialize();
        tabbedPane.add("SingleRow Data", this.createChartPanel(barTimeChart4.getChart()));
        this.renderFile(barTimeChart4, "BarTimeChart4");
    }
}

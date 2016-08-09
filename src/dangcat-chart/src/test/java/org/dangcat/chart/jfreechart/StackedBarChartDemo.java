package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.DataModule;

import javax.swing.*;

public class StackedBarChartDemo extends ChartDemoBase {
    private static final long serialVersionUID = 1L;
    private static final String TITLE = "堆栈柱状统计图";

    public StackedBarChartDemo() {
        super(StackedBarChart.class.getSimpleName());
    }

    public static void main(final String[] args) {
        show(new StackedBarChartDemo());
    }

    @Override
    protected void createTabbedPane(JTabbedPane tabbedPane) {
        DataModule dataModule1 = SimulateCategoryData.createColumnDataModule(false);
        StackedBarChart stackedBarChart1 = new StackedBarChart();
        stackedBarChart1.setTitle(TITLE);
        stackedBarChart1.setDataModule(dataModule1);
        stackedBarChart1.initialize();
        tabbedPane.add("Column: Column Name Row FieldNames", this.createChartPanel(stackedBarChart1.getChart()));
        this.renderFile(stackedBarChart1, "StackedBarChart1");

        DataModule dataModule2 = SimulateCategoryData.createRowDataModule(false);
        StackedBarChart stackedBarChart2 = new StackedBarChart();
        stackedBarChart2.setShowItemLabel(false);
        stackedBarChart2.setLegendVisible(true);
        stackedBarChart2.setTitle(TITLE);
        stackedBarChart2.setDataModule(dataModule2);
        stackedBarChart2.initialize();
        tabbedPane.add("Row: Column Name Row FieldNames", this.createChartPanel(stackedBarChart2.getChart()));
        this.renderFile(stackedBarChart2, "StackedBarChart2");

        DataModule dataModule3 = SimulateCategoryData.createColumnDataModule(true);
        StackedBarChart stackedBarChart3 = new StackedBarChart();
        stackedBarChart3.setTitle(TITLE);
        stackedBarChart3.setDataModule(dataModule3);
        stackedBarChart3.initialize();
        tabbedPane.add("Column: Column FieldNames Row Name", this.createChartPanel(stackedBarChart3.getChart()));
        this.renderFile(stackedBarChart3, "StackedBarChart3");

        DataModule dataModule4 = SimulateCategoryData.createRowDataModule(true);
        StackedBarChart stackedBarChart4 = new StackedBarChart();
        stackedBarChart4.setShowItemLabel(false);
        stackedBarChart4.setLegendVisible(true);
        stackedBarChart4.setTitle(TITLE);
        stackedBarChart4.setDataModule(dataModule4);
        stackedBarChart4.initialize();
        tabbedPane.add("Row: Column FieldNames Row Name", this.createChartPanel(stackedBarChart4.getChart()));
        this.renderFile(stackedBarChart4, "StackedBarChart4");
    }
}

package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.DataModule;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;

import javax.swing.*;

public class PieChartDemo extends ChartDemoBase {
    private static final long serialVersionUID = 1L;

    public PieChartDemo() {
        super(PieChart.class.getSimpleName());
    }

    public static void main(final String[] args) {
        show(new PieChartDemo());
    }

    @Override
    protected void createTabbedPane(JTabbedPane tabbedPane) {
        DataModule columnDataModule = SimulateCategoryData.createPieColumnDataModule();
        columnDataModule.getDataReader().setFilterExpress(new FilterUnit(SimulateCategoryData.Name, FilterType.eq, "Name 1"));
        PieChart pieChart1 = new PieChart();
        pieChart1.setTitle("饼状统计图");
        pieChart1.setDataModule(columnDataModule);
        pieChart1.initialize();
        tabbedPane.add("Column Module Data", this.createChartPanel(pieChart1.getChart()));
        this.renderFile(pieChart1, "PieChart1");

        DataModule rowDataModule = SimulateCategoryData.createPieRowDataModule();
        rowDataModule.getDataReader().setFilterExpress(new FilterUnit(SimulateCategoryData.Name, FilterType.eq, "Name 9"));
        PieChart pieChart2 = new PieChart();
        pieChart2.setShowItemLabel(false);
        pieChart2.setLegendVisible(true);
        pieChart2.setTitle("饼状统计图");
        pieChart2.setDataModule(rowDataModule);
        pieChart2.initialize();
        tabbedPane.add("Row Module Data", this.createChartPanel(pieChart2.getChart()));
        this.renderFile(pieChart2, "PieChart2");
    }
}

package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.DataModule;
import org.dangcat.chart.jfreechart.data.TransRateMap;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;

import javax.swing.*;
import java.util.Date;

public class DiffLineChartDemo extends ChartDemoBase {
    private static final long serialVersionUID = 1L;
    private static final String TITLE = "对比线性统计图";

    public DiffLineChartDemo() {
        super(DiffLineChart.class.getSimpleName());
    }

    public static void main(final String[] args) {
        show(new DiffLineChartDemo());
    }

    @Override
    protected void createTabbedPane(JTabbedPane tabbedPane) {
        TransRateMap transRateMap = new TransRateMap();
        transRateMap.addRowTransRate(SimulateCategoryData.DownOctets, -1.0);

        Date[] dates = SimulateTimeData.today();
        DataModule columnDataModule = SimulateTimeData.createDiffColumnDataModule();
        columnDataModule.setTransRateMap(transRateMap);
        columnDataModule.getDataReader().setFilterExpress(new FilterUnit(SimulateCategoryData.Name, FilterType.eq, "Name 2"));

        DiffLineChart diffLineChart1 = new DiffLineChart();
        diffLineChart1.setTitle(TITLE);
        diffLineChart1.setRangeTitle("流量");
        diffLineChart1.setDataModule(columnDataModule);
        this.initTimeChart(diffLineChart1, dates[0], dates[1]);
        diffLineChart1.initialize();
        tabbedPane.add("Column Module Data", this.createChartPanel(diffLineChart1.getChart()));
        this.renderFile(diffLineChart1, "DiffLineChart1");

        DataModule rowDataModule = SimulateTimeData.createMultiRowDataModule();
        FilterGroup filterGroup = new FilterGroup();
        filterGroup.add(new FilterUnit(SimulateCategoryData.Name, FilterType.eq, "Name 8"));
        filterGroup.add(new FilterUnit(SimulateCategoryData.FieldName, FilterType.eq, SimulateCategoryData.UpOctets, SimulateCategoryData.DownOctets));
        rowDataModule.getDataReader().setFilterExpress(filterGroup);
        rowDataModule.setTransRateMap(transRateMap);

        DiffLineChart diffLineChart2 = new DiffLineChart();
        diffLineChart2.setLegendVisible(true);
        diffLineChart2.setTitle(TITLE);
        diffLineChart2.setRangeTitle("流量");
        diffLineChart2.setDataModule(rowDataModule);
        this.initTimeChart(diffLineChart2, dates[0], dates[1]);
        diffLineChart2.initialize();
        tabbedPane.add("Row Module Data", this.createChartPanel(diffLineChart2.getChart()));
        this.renderFile(diffLineChart2, "DiffLineChart2");
    }
}

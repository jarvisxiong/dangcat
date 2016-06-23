package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.ColumnDataModule;
import org.dangcat.chart.jfreechart.data.DataModule;
import org.dangcat.chart.jfreechart.data.RowDataModule;
import org.dangcat.chart.jfreechart.data.TransRateMap;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.model.TableDataReader;

import javax.swing.*;
import java.util.Date;

public class CombinedChartDemo extends ChartDemoBase {
    private static final long serialVersionUID = 1L;
    private static final String TITLE = "组合统计图";
    private static Date[] dates = SimulateTimeData.today();

    public CombinedChartDemo() {
        super(CombinedChart.class.getSimpleName());
    }

    public static void main(final String[] args) {
        show(new CombinedChartDemo());
    }

    private TimeChart createAreaChart(Table table) {
        DataModule dataModule = this.createDataModule(table, new String[]{SimulateTimeData.Octets}, SimulateTimeData.Octets);
        FilterExpress filterExpress = this.createFilterExpress(table, SimulateTimeData.Octets);

        dataModule.getDataReader().setFilterExpress(filterExpress);
        AreaChart areaChart = new AreaChart();
        areaChart.setDataModule(dataModule);
        return areaChart;
    }

    private TimeChart createBarTimeChart(Table table) {
        DataModule dataModule = this.createDataModule(table, new String[]{SimulateTimeData.UpOctets, SimulateTimeData.DownOctets}, SimulateTimeData.Octets);
        FilterExpress filterExpress = this.createFilterExpress(table, SimulateTimeData.UpOctets, SimulateTimeData.DownOctets);

        dataModule.getDataReader().setFilterExpress(filterExpress);
        BarTimeChart barTimeChart = new BarTimeChart();
        barTimeChart.setDataModule(dataModule);
        return barTimeChart;
    }

    private void createColumnDataModule(JTabbedPane tabbedPane) {
        Table table = SimulateTimeData.createTable(SimulateTimeData.Name, SimulateTimeData.DateTime, SimulateTimeData.Octets, SimulateTimeData.UpOctets, SimulateTimeData.DownOctets,
                SimulateTimeData.OctetsVelocity, SimulateTimeData.Percent);
        SimulateTimeData.createTableColumnData(table);

        CombinedChart combinedChart = new CombinedChart();
        combinedChart.setTitle(TITLE);
        combinedChart.addTimeChart(this.createAreaChart(table));
        combinedChart.addTimeChart(this.createLineChart(table));
        combinedChart.addTimeChart(this.createDiffChart(table));
        combinedChart.addTimeChart(this.createBarTimeChart(table));
        this.initTimeChart(combinedChart, dates[0], dates[1]);
        combinedChart.initialize();
        tabbedPane.add("Column Module Data", this.createChartPanel(combinedChart.getChart()));
        this.renderFile(combinedChart, "CombinedChart1");
    }

    private DataModule createDataModule(Table table, String[] rowKeyNames, String valueFieldName) {
        DataModule dataModule = null;
        if (table.getColumns().find(SimulateTimeData.FieldName) == null)
            dataModule = new ColumnDataModule(new TableDataReader(table), rowKeyNames, new String[]{SimulateTimeData.DateTime});
        else
            dataModule = new RowDataModule(new TableDataReader(table), new String[]{SimulateTimeData.FieldName}, new String[]{SimulateTimeData.DateTime}, valueFieldName);
        return dataModule;
    }

    private TimeChart createDiffChart(Table table) {
        DataModule dataModule = this.createDataModule(table, new String[]{SimulateTimeData.UpOctets, SimulateTimeData.DownOctets}, SimulateTimeData.Octets);
        FilterExpress filterExpress = this.createFilterExpress(table, SimulateTimeData.UpOctets, SimulateTimeData.DownOctets);

        TransRateMap transRateMap = new TransRateMap();
        transRateMap.addRowTransRate(SimulateCategoryData.DownOctets, -1.0);

        dataModule.getDataReader().setFilterExpress(filterExpress);
        dataModule.setTransRateMap(transRateMap);
        DiffLineChart diffLineChart = new DiffLineChart();
        diffLineChart.setRangeTitle("流量");
        diffLineChart.setDataModule(dataModule);
        return diffLineChart;
    }

    private FilterExpress createFilterExpress(Table table, Object... fieldNames) {
        FilterExpress filterExpress = null;
        if (table.getColumns().find(SimulateTimeData.FieldName) == null)
            filterExpress = new FilterUnit(SimulateCategoryData.Name, FilterType.eq, "Name 2");
        else {
            FilterGroup filterGroup = new FilterGroup();
            filterGroup.add(new FilterUnit(SimulateCategoryData.Name, FilterType.eq, "Name 2"));
            filterGroup.add(new FilterUnit(SimulateCategoryData.FieldName, FilterType.eq, fieldNames));
            filterExpress = filterGroup;
        }
        return filterExpress;
    }

    private TimeChart createLineChart(Table table) {
        DataModule dataModule = this.createDataModule(table, new String[]{SimulateTimeData.Octets}, SimulateTimeData.Octets);
        FilterExpress filterExpress = this.createFilterExpress(table, SimulateTimeData.Octets);

        dataModule.getDataReader().setFilterExpress(filterExpress);
        LineChart lineChart = new LineChart();
        lineChart.setDataModule(dataModule);
        return lineChart;
    }

    private void createRowDataModule(JTabbedPane tabbedPane) {
        Table table = SimulateTimeData.createTable(SimulateTimeData.Name, SimulateTimeData.FieldName, SimulateTimeData.DateTime, SimulateTimeData.Octets);
        SimulateTimeData.createTableRowData(table);

        CombinedChart combinedChart = new CombinedChart();
        combinedChart.setTitle(TITLE);
        combinedChart.addTimeChart(this.createDiffChart(table));
        combinedChart.addTimeChart(this.createAreaChart(table));
        combinedChart.addTimeChart(this.createLineChart(table));
        combinedChart.addTimeChart(this.createBarTimeChart(table));
        this.initTimeChart(combinedChart, dates[0], dates[1]);
        combinedChart.initialize();
        tabbedPane.add("Row Module Data", this.createChartPanel(combinedChart.getChart()));
        this.renderFile(combinedChart, "CombinedChart2");
    }

    @Override
    protected void createTabbedPane(JTabbedPane tabbedPane) {
        this.createColumnDataModule(tabbedPane);
        this.createRowDataModule(tabbedPane);
    }
}

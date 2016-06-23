package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.DataModule;
import org.dangcat.commons.utils.DateUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class TimeChart extends AxisChart {
    protected static final Double ZREO = new Double(0);
    /**
     * 起始颜色。
     */
    private int beginColorIndex = 0;
    /**
     * 起始时间。
     */
    private Date beginTime = null;
    /**
     * 截止时间。
     */
    private Date endTime = null;
    /**
     * 计算分钟步长。
     */
    private int timeStep = 0;
    /**
     * 显示面板。
     */
    private XYPlot xyPlot = null;

    /**
     * 初始化统计对象。
     */
    @Override
    protected JFreeChart createChart() {
        // 绘图对象
        XYPlot xyPlot = new XYPlot();
        // 横坐标标题。
        xyPlot.setDomainAxis(new DateAxis(this.getDomainTitle()));
        // 纵坐标标题。
        xyPlot.setRangeAxis(new NumberAxis(this.getRangeTitle()));
        // 区域图的表现模块。
        xyPlot.setRenderer(this.createXYItemRenderer());
        // 构建统计图对象。
        return new JFreeChart(xyPlot);
    }

    protected XYDataset createXYDataset() {
        return this.createXYDataset(null);
    }

    protected abstract XYDataset createXYDataset(Collection<Comparable<?>> rowKeys);

    protected abstract XYItemRenderer createXYItemRenderer();

    protected int getBeginColorIndex() {
        return beginColorIndex;
    }

    protected void setBeginColorIndex(int beginColorIndex) {
        this.beginColorIndex = beginColorIndex;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    private SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat(this.getDatePattern());
    }

    /**
     * 时间范围的天数。
     */
    private long getDateLength() {
        return this.getDateRange() / 60 / 60 / 24;
    }

    /**
     * 日期格式模版。
     */
    private String getDatePattern() {
        // 时间范围的天数。
        long days = this.getDateLength();
        // 计算时间模式。
        if (days >= 365)
            return "yy/MM";
        else if (days >= 28)
            return "MM-dd";
        else if (days > 7)
            return "MM-dd HH";
        else if (days == 7)
            return "E";
        return "HH:mm";
    }

    /**
     * 起始和截止时间长度，单位秒。
     */
    private long getDateRange() {
        Long beginTime = this.getBeginTime().getTime();
        Long endTime = this.getEndTime().getTime();
        return (endTime - beginTime) / 1000;
    }

    /**
     * 获得刻度单元。
     *
     * @return
     */
    private DateTickUnit getDateTickUnit() {
        DateTickUnit dateTickUnit = null;
        // 时间范围的天数。
        long days = this.getDateLength();
        if (days >= 365)
            dateTickUnit = new DateTickUnit(DateTickUnitType.MONTH, 1);
        else {
            int domainAxisStep = (int) this.getDomainAxisStep();
            // 计算横坐标标签个数。
            int muniteStep = domainAxisStep / 60;
            if (muniteStep == 0)
                dateTickUnit = new DateTickUnit(DateTickUnitType.SECOND, domainAxisStep);
            else
                dateTickUnit = new DateTickUnit(DateTickUnitType.MINUTE, muniteStep);
        }
        return dateTickUnit;
    }

    /**
     * 计算横坐标步长，单位秒。
     *
     * @return 坐标步长。
     */
    private long getDomainAxisStep() {
        // 时间范围的天数。
        long days = this.getDateLength();
        // 计算时间模式。
        if (days == 7)
            this.setDomainAxiaCount(7);
        // 计算横坐标标签个数。
        long domainAxisStep = this.getDateRange() / this.getDomainAxiaCount();
        if (domainAxisStep % 60 != 0)
            domainAxisStep = (domainAxisStep / 60 + 1) * 60;
        return domainAxisStep;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    protected Map<Date, Double> getTimeData(Comparable<?> rowKey) {
        return this.getTimeData(rowKey, 0);
    }

    protected Map<Date, Double> getTimeData(Comparable<?> rowKey, int index) {
        DataModule dataModule = this.getDataModule();
        Map<Date, Double> valueMap = new LinkedHashMap<Date, Double>();
        for (Comparable<?> columnKey : dataModule.getColumnKeys(rowKey)) {
            double value = this.getDataConverter(index).getValue(rowKey, columnKey);
            valueMap.put((Date) columnKey, value);
        }
        ChartUtils.fixValueMap(this, valueMap);
        return valueMap;
    }

    public int getTimeStep() {
        return this.timeStep;
    }

    public void setTimeStep(int timeStep) {
        this.timeStep = timeStep;
    }

    protected XYPlot getXYPlot() {
        if (this.xyPlot != null)
            return this.xyPlot;
        return (XYPlot) this.getChart().getPlot();
    }

    protected void setXYPlot(XYPlot xyPlot) {
        this.xyPlot = xyPlot;
    }

    protected void iniItemRenderer(XYItemRenderer xyItemRenderer, int i) {
        CustomXYItemLabelGenerator customXYItemLabelGenerator = new CustomXYItemLabelGenerator(this, i);
        // 产生提示。
        xyItemRenderer.setBaseToolTipGenerator(customXYItemLabelGenerator);
        // 产生热点区域。
        xyItemRenderer.setURLGenerator(customXYItemLabelGenerator);
    }

    /**
     * 设置固定颜色范围。
     */
    protected void initAxis(XYPlot xyPlot) {
        for (int i = 0; i < xyPlot.getDomainAxisCount(); i++) {
            DateAxis domainAxis = (DateAxis) xyPlot.getDomainAxis(i);
            this.initDomainAxis(domainAxis);
            // 起始时间。
            domainAxis.setMinimumDate(DateUtils.add(DateUtils.SECOND, this.getBeginTime(), -1));
            // 截止时间。
            domainAxis.setMaximumDate(DateUtils.add(DateUtils.SECOND, this.getEndTime(), 1));
            // 横坐标标题。
            domainAxis.setLabel(this.getDomainTitle(i));
        }

        for (int i = 0; i < xyPlot.getRangeAxisCount(); i++) {
            NumberAxis rangeAxis = (NumberAxis) xyPlot.getRangeAxis(i);
            if (rangeAxis != null) {
                this.initRangeAxis(rangeAxis);
                // 设置Y轴的最大值
                rangeAxis.setUpperBound(this.getMaxValue(i));
                // 设置Y轴的最小值
                rangeAxis.setLowerBound(this.getMinValue(i));
                // 纵坐标标题。
                rangeAxis.setLabel(this.getRangeTitle(i));
            }
        }
    }

    /**
     * 初始化横坐标。
     *
     * @param domainAxis
     */
    protected void initDomainAxis(DateAxis domainAxis) {
        this.initAxis(domainAxis);
        // 设定日期格式。
        domainAxis.setDateFormatOverride(this.getDateFormat());
        // 设置刻度单元。
        domainAxis.setTickUnit(this.getDateTickUnit());
        domainAxis.setTickMarkPosition(DateTickMarkPosition.START);
        // 是否自动选择刻度。
        domainAxis.setAutoTickUnitSelection(false);
    }

    @Override
    protected void initPlot(Plot plot) {
        super.initPlot(plot);
        XYPlot xyPlot = (XYPlot) plot;
        // 表格线颜色。
        xyPlot.setDomainGridlinePaint(Color.BLACK);
        // 表格线可见。
        xyPlot.setDomainGridlinesVisible(true);
        // 纵坐标线颜色。
        xyPlot.setRangeGridlinePaint(Color.BLACK);
        xyPlot.setRangeCrosshairVisible(true);
        // 坐标偏移距离。
        xyPlot.setAxisOffset(new RectangleInsets(2.0, 2.0, 2.0, 2.0));
        // 设置固定颜色范围。
        this.initAxis(xyPlot);

        this.initSeriesPaint();
    }

    /**
     * 初始化纵坐标。
     *
     * @param rangeAxis
     */
    protected void initRangeAxis(NumberAxis rangeAxis) {
        this.initAxis(rangeAxis);
        // 设置最高的一个 Item 与图片顶端的距离
        rangeAxis.setUpperMargin(0.15);
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // 设定纵坐标显示格式。
        rangeAxis.setNumberFormatOverride(new DecimalFormat("#.###"));
        // 设置最高的一个 Item 与图片顶端的距离
        rangeAxis.setUpperMargin(0.15);
    }

    protected void initSeriesPaint() {
        XYPlot xyPlot = this.getXYPlot();
        for (int i = 0; i < xyPlot.getRendererCount(); i++) {
            XYItemRenderer xyItemRenderer = xyPlot.getRenderer(i);
            if (xyItemRenderer != null) {
                this.iniItemRenderer(xyItemRenderer, i);
                for (int k = 0; k < ColorFactory.MAX_ITEM; k++) {
                    int index = k + i + this.beginColorIndex;
                    Color color = ColorFactory.sequence(index);
                    if (color != null)
                        xyItemRenderer.setSeriesPaint(k, color);
                }
            }
        }
    }

    protected void setXYDataset(XYDataset dataset) {
        XYPlot xyPlot = this.getXYPlot();
        xyPlot.setDataset(dataset);
        this.initPlot(xyPlot);
    }
}

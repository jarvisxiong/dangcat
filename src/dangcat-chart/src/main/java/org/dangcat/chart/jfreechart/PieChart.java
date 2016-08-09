package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.DataConverter;
import org.dangcat.chart.jfreechart.data.DataModule;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.Rotation;

import java.awt.*;

/**
 * 饼状统计图。
 *
 * @author dangcat
 */
public class PieChart extends ChartBase {
    @Override
    protected JFreeChart createChart() {
        // 建立统计对象。
        JFreeChart chart = ChartFactory.createPieChart(this.getTitle(), // 标题
                new DefaultPieDataset(), // 数据来源。
                true, // include legend
                true, // 是否提示。
                true);

        chart.setTextAntiAlias(false);
        // 设置Legend的位置。
        chart.getLegend().setPosition(RectangleEdge.RIGHT);
        return chart;
    }

    @Override
    protected void initPlot(Plot plot) {
        super.initPlot(plot);
        // 获得3D的水晶饼图对象
        PiePlot piePlot = (PiePlot) plot;
        // 固定圆形。
        piePlot.setCircular(true);
        // 外框不可见。
        piePlot.setOutlineVisible(false);
        // 设置开始角度
        piePlot.setStartAngle(150D);
        // 设置方向为顺时针方向
        piePlot.setDirection(Rotation.CLOCKWISE);
        // 设置透明度，0.5F为半透明，1为不透明，0为全透明
        // piePlot.setBackgroundAlpha(0);
        // piePlot.setForegroundAlpha(0.7F);
        // 设置扇区边框不可见
        piePlot.setSectionOutlinesVisible(false);
        // 设置标签字体。
        Font chartFont = this.getChartFont();
        if (chartFont != null)
            piePlot.setLabelFont(chartFont);
        // 设置扇区标签显示格式：关键字：值(百分比)
        CustomPieItemLabelGenerator customPieItemLabelGenerator = new CustomPieItemLabelGenerator(this);
        if (this.isShowItemLabel())
            piePlot.setLabelGenerator(customPieItemLabelGenerator);
        // 设置扇区提示
        piePlot.setToolTipGenerator(customPieItemLabelGenerator);
        // 产生热点区域。
        piePlot.setURLGenerator(customPieItemLabelGenerator);
        piePlot.setLegendLabelGenerator(customPieItemLabelGenerator);
        // 去掉背景色
        piePlot.setLabelBackgroundPaint(null);
        // 去掉阴影
        piePlot.setLabelShadowPaint(null);
        // 去掉边框
        piePlot.setLabelOutlinePaint(null);
        piePlot.setShadowPaint(null);
        // 图例形状
        piePlot.setLegendItemShape(new Rectangle(10, 10));
    }

    private void initSectionPaint() {
        // 设置固定颜色范围。
        PiePlot piePlot = (PiePlot) this.getChart().getPlot();
        DataModule dataModule = this.getDataModule();
        String[] labels = dataModule.getRowKeys().toArray(new String[0]);
        for (int i = 0; i < labels.length; i++) {
            Color color = ColorFactory.sequence(i);
            if (color != null)
                piePlot.setSectionPaint(labels[i], color);
        }
    }

    @Override
    public void load() {
        DataModule dataModule = this.getDataModule();

        DataConverter dataConverter = this.createDataConverter(false);
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        for (Comparable<?> rowKey : dataModule.getRowKeys()) {
            for (Comparable<?> columnKey : dataModule.getColumnKeys(rowKey)) {
                Double value = dataConverter.getValue(rowKey, columnKey);
                Comparable<?> key = null;
                if (!ChartUtils.isNull(rowKey) && !ChartUtils.isNull(columnKey))
                    key = dataModule.getLabel(rowKey, columnKey);
                else if (!ChartUtils.isNull(rowKey))
                    key = rowKey;
                else if (!ChartUtils.isNull(columnKey))
                    key = columnKey;
                pieDataset.setValue(key, value);
            }
        }
        PiePlot piePlot = (PiePlot) this.getChart().getPlot();
        piePlot.setDataset(pieDataset);

        this.initSectionPaint();
    }
}
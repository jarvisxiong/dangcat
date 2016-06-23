package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.DataConverter;
import org.dangcat.chart.jfreechart.data.DataModule;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * 柱状统计图。
 * @author dangcat
 * 
 */
public abstract class CategoryChart extends AxisChart
{
    /** 横标签是否可见。 */
    private boolean domainAxisVisible = true;
    /** 图板方向。 */
    private PlotOrientation orientation = PlotOrientation.VERTICAL;
    /** 纵标签是否可见。 */
    private boolean rangeAxisVisible = true;
    /** 是否显示标签。 */
    private boolean showItemLabel = true;

    protected CategoryDataset createCategoryDataset(DataConverter dataConverter)
    {
        DataModule dataModule = this.getDataModule();
        DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
        for (Comparable<?> rowKey : dataModule.getRowKeys())
        {
            for (Comparable<?> columnKey : dataModule.getColumnKeys(rowKey))
            {
                Double value = dataConverter.getValue(rowKey, columnKey);
                categoryDataset.addValue(value, rowKey, columnKey);
            }
        }
        return categoryDataset;
    }

    protected abstract JFreeChart createChart();

    public PlotOrientation getOrientation()
    {
        return orientation;
    }

    public void setOrientation(PlotOrientation orientation) {
        this.orientation = orientation;
    }

    protected void iniItemRenderer(CategoryItemRenderer categoryItemRenderer, int i)
    {
        // 显示每个柱的数值，并修改该数值的字体属性
        CustomCategoryItemLabelGenerator categoryItemLabelGenerator = new CustomCategoryItemLabelGenerator(this);
        categoryItemRenderer.setBaseItemLabelGenerator(categoryItemLabelGenerator);
        categoryItemRenderer.setBaseToolTipGenerator(categoryItemLabelGenerator);
        categoryItemRenderer.setBaseItemLabelsVisible(true);
        categoryItemRenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE2, TextAnchor.TOP_LEFT));
        // 产生热点区域。
        categoryItemRenderer.setBaseItemURLGenerator(categoryItemLabelGenerator);
    }

    protected void initDomainAxis(CategoryAxis domainAxis)
    {
        this.initAxis(domainAxis);
        // 横坐标是否可见。
        domainAxis.setVisible(this.isDomainAxisVisible());
        // 纵坐标标题。
        domainAxis.setLabel(this.getDomainTitle());
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
    }

    @Override
    protected void initPlot(Plot plot)
    {
        super.initPlot(plot);

        CategoryPlot categoryPlot = (CategoryPlot) plot;
        // 表格线颜色。
        categoryPlot.setDomainGridlinePaint(Color.GRAY);
        // 表格线可见。
        categoryPlot.setDomainGridlinesVisible(true);
        // 纵坐标线颜色。
        categoryPlot.setRangeGridlinePaint(Color.GRAY);
        // 坐标偏移距离。
        categoryPlot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        // 设置坐标位置。
        categoryPlot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
        // 设置坐标位置。
        categoryPlot.setOrientation(this.getOrientation());
        // 设定横坐标。
        this.initDomainAxis(categoryPlot.getDomainAxis());
        // 设定横坐标。
        this.initRangeAxis((NumberAxis) categoryPlot.getRangeAxis());
        // 设置固定颜色范围。
        for (int i = 0; i < categoryPlot.getRendererCount(); i++)
        {
            CategoryItemRenderer categoryItemRenderer = categoryPlot.getRenderer(i);
            this.iniItemRenderer(categoryItemRenderer, i);
            for (int k = 0; k < ColorFactory.MAX_ITEM; k++)
            {
                int index = k + i;
                if (index >= ColorFactory.MAX_ITEM)
                    index = index - ColorFactory.MAX_ITEM;
                Color color = ColorFactory.sequence(index);
                if (color != null)
                    categoryItemRenderer.setSeriesPaint(k, color);
            }
        }

        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
        for (int i = 0; i < ColorFactory.MAX_ITEM; i++)
        {
            Color color = ColorFactory.sequence(i);
            if (color != null)
                renderer.setSeriesPaint(i, color);
        }
    }

    private void initRangeAxis(NumberAxis rangeAxis)
    {
        this.initAxis(rangeAxis);
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // 设置最高的一个大柱子与图片顶端的距离。
        rangeAxis.setUpperMargin(0.15);
        // 设定纵坐标显示格式。
        rangeAxis.setNumberFormatOverride(new DecimalFormat("#.###"));
        // 横坐标是否可见。
        rangeAxis.setVisible(this.isRangeAxisVisible());
        // 纵坐标标题。
        rangeAxis.setLabel(this.getRangeTitle());
        // 设定边界值。
        rangeAxis.setUpperBound(this.getMaxValue());
    }

    public boolean isDomainAxisVisible()
    {
        return this.domainAxisVisible;
    }

    public void setDomainAxisVisible(boolean domainAxisVisible)
    {
        this.domainAxisVisible = domainAxisVisible;
    }

    public boolean isRangeAxisVisible()
    {
        return this.rangeAxisVisible;
    }

    public void setRangeAxisVisible(boolean rangeAxisVisible)
    {
        this.rangeAxisVisible = rangeAxisVisible;
    }

    public boolean isShowItemLabel() {
        return this.showItemLabel;
    }

    public void setShowItemLabel(boolean showItemLabel)
    {
        this.showItemLabel = showItemLabel;
    }

    protected void setCategoryDataset(CategoryDataset categoryDataset) {
        CategoryPlot categoryPlot = (CategoryPlot) this.getChart().getPlot();
        categoryPlot.setDataset(categoryDataset);
        this.initPlot(categoryPlot);
    }
}

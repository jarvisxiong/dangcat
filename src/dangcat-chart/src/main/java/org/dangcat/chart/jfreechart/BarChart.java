package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.DataConverter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.StandardGradientPaintTransformer;

/**
 * 柱状统计图。
 * @author dangcat
 * 
 */
public class BarChart extends CategoryChart
{
    @Override
    protected JFreeChart createChart()
    {
        // 建立统计对象。
        JFreeChart chart = ChartFactory.createBarChart(this.getTitle(), // 标题
                this.getDomainTitle(), // 横坐标标题。
                this.getRangeTitle(), // 纵坐标标题。
                new DefaultCategoryDataset(), // 数据来源。
                PlotOrientation.VERTICAL, // 坐标方向
                true, // include legend
                true, // 是否提示。
                false);

        // 设置Legend的位置。
        chart.getLegend().setPosition(RectangleEdge.RIGHT);
        this.setShowItemLabel(false);
        this.setDomainAxisVisible(true);

        CategoryPlot categoryPlot = (CategoryPlot) chart.getPlot();
        categoryPlot.setRenderer(new CustomBarRenderer(ColorFactory.getPaints()));
        return chart;
    }

    @Override
    protected void iniItemRenderer(CategoryItemRenderer categoryItemRenderer, int i)
    {
        super.iniItemRenderer(categoryItemRenderer, i);

        BarRenderer barRenderer = (BarRenderer) categoryItemRenderer;
        // 设置外部线可见
        barRenderer.setDrawBarOutline(false);
        barRenderer.setItemLabelAnchorOffset(10D);
        barRenderer.setMaximumBarWidth(0.1);
        barRenderer.setItemMargin(0.01);
        barRenderer.setBarPainter(new StandardBarPainter());
        barRenderer.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.CENTER_HORIZONTAL));
        barRenderer.setShadowVisible(false);
    }

    @Override
    public void load()
    {
        DataConverter dataConverter = this.createDataConverter(false);

        CategoryDataset categoryDataset = this.createCategoryDataset(dataConverter);
        if (categoryDataset != null)
            this.setCategoryDataset(categoryDataset);
    }
}

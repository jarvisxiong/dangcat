package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.DataConverter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

/**
 * 堆栈柱状统计图。
 *
 * @author dangcat
 */
public class StackedBarChart extends CategoryChart {
    @Override
    protected JFreeChart createChart() {
        // 建立统计对象。
        JFreeChart chart = ChartFactory.createStackedBarChart(this.getTitle(), // 标题
                this.getDomainTitle(), // 横坐标标题。
                this.getRangeTitle(), // 纵坐标标题。
                new DefaultCategoryDataset(), // 数据来源。
                this.getOrientation(), // 坐标方向
                true, // include legend
                true, // 是否提示。
                false);
        return chart;
    }

    @Override
    protected void iniItemRenderer(CategoryItemRenderer categoryItemRenderer, int i) {
        super.iniItemRenderer(categoryItemRenderer, i);
        BarRenderer renderer = (BarRenderer) categoryItemRenderer;
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.INSIDE1, TextAnchor.CENTER_RIGHT));
        renderer.setShadowVisible(false);
    }

    @Override
    public void load() {
        DataConverter dataConverter = this.createDataConverter(true);
        CategoryDataset categoryDataset = this.createCategoryDataset(dataConverter);
        if (categoryDataset != null)
            this.setCategoryDataset(categoryDataset);
    }
}

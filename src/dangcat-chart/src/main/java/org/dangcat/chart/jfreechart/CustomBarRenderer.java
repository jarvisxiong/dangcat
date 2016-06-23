package org.dangcat.chart.jfreechart;

import java.awt.Paint;

import org.jfree.chart.labels.CategorySeriesLabelGenerator;
import org.jfree.chart.renderer.category.BarRenderer;

class CustomBarRenderer extends BarRenderer
{
    private static final long serialVersionUID = 1L;

    private Paint[] colors;

    CustomBarRenderer(Paint[] paramArrayOfPaint)
    {
        this.colors = paramArrayOfPaint;
    }

    public Paint getItemPaint(int paramInt1, int paramInt2)
    {
        return this.colors[(paramInt2 % this.colors.length)];
    }

    @Override
    public CategorySeriesLabelGenerator getLegendItemLabelGenerator()
    {
        CategorySeriesLabelGenerator gategorySeriesLabelGenerator = super.getLegendItemLabelGenerator();
        return gategorySeriesLabelGenerator;
    }
}
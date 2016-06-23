package org.dangcat.chart.jfreechart;

import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;

public class BarTimeChart extends TimeXYChart
{
    @Override
    protected XYItemRenderer createXYItemRenderer()
    {
        return new StackedXYBarRenderer();
    }

    @Override
    protected void iniItemRenderer(XYItemRenderer xyItemRenderer, int i)
    {
        super.iniItemRenderer(xyItemRenderer, i);

        XYBarRenderer xyBarRenderer = (XYBarRenderer) xyItemRenderer;
        xyBarRenderer.setShadowVisible(false);
        xyBarRenderer.setDrawBarOutline(false);
        xyBarRenderer.setMargin(0.0);
    }

    @Override
    public void load()
    {
        boolean isStacked = this.getDataModule().getRowKeys().size() > 1;
        this.createDataConverter(isStacked);

        // 区域图的表现模块。
        XYBarRenderer xyBarRenderer = null;
        if (isStacked)
            xyBarRenderer = new StackedXYBarRenderer();
        else
            xyBarRenderer = new XYBarRenderer();
        this.getXYPlot().setRenderer(xyBarRenderer);
        this.iniItemRenderer(xyBarRenderer, 0);

        this.setXYDataset(this.createXYDataset());
    }
}

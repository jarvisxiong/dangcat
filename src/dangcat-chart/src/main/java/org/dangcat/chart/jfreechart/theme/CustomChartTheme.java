package org.dangcat.chart.jfreechart.theme;

import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.ui.RectangleInsets;

import java.awt.*;

public class CustomChartTheme extends StandardChartTheme {
    private static final long serialVersionUID = 1L;
    public static Color[] COLORS = {new Color(31, 129, 188), new Color(92, 92, 97), new Color(144, 237, 125), new Color(255, 188, 117), new Color(153, 158, 255), new Color(255, 117, 153),
            new Color(253, 236, 109), new Color(128, 133, 232), new Color(158, 90, 102), new Color(255, 204, 102)};
    private Font defaultFont = new Font("宋体", Font.PLAIN, 12);

    private Paint[] outLinePaintSequence = new Paint[]{Color.WHITE};

    public CustomChartTheme(String name) {
        super(name);
        this.initialize();
    }

    public DefaultDrawingSupplier getDefaultDrawingSupplier() {
        return new DefaultDrawingSupplier(COLORS, COLORS, outLinePaintSequence, DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
    }

    public Font getDefaultFont() {
        return defaultFont;
    }

    public void setDefaultFont(Font defaultFont) {
        this.defaultFont = defaultFont;
    }

    public void initialize() {
        // 设置标题字体
        this.setExtraLargeFont(this.getDefaultFont());
        // 设置图例的字体
        this.setRegularFont(this.getDefaultFont());
        // 设置轴向的字体
        this.setLargeFont(this.getDefaultFont());
        this.setSmallFont(this.getDefaultFont());
        this.setTitlePaint(new Color(51, 51, 51));
        this.setSubtitlePaint(new Color(85, 85, 85));

        // 设置标注
        this.setLegendBackgroundPaint(Color.WHITE);
        this.setLegendItemPaint(Color.BLACK);//
        this.setChartBackgroundPaint(Color.WHITE);

        this.setDrawingSupplier(this.getDefaultDrawingSupplier());
        // 绘制区域
        this.setPlotBackgroundPaint(Color.WHITE);
        // 绘制区域外边框
        // this.setPlotOutlinePaint(Color.WHITE);
        // 链接标签颜色
        this.setLabelLinkPaint(new Color(8, 55, 114));
        this.setLabelLinkStyle(PieLabelLinkStyle.CUBIC_CURVE);

        this.setAxisOffset(new RectangleInsets(5, 12, 5, 12));
        // X坐标轴垂直网格颜色
        this.setDomainGridlinePaint(new Color(192, 208, 224));
        // Y坐标轴水平网格颜色
        this.setRangeGridlinePaint(new Color(192, 192, 192));

        this.setBaselinePaint(Color.WHITE);
        this.setCrosshairPaint(Color.BLUE);
        // 坐标轴标题文字颜色
        this.setAxisLabelPaint(new Color(51, 51, 51));
        // 刻度数字
        this.setTickLabelPaint(new Color(67, 67, 72));
        // 设置柱状图渲染
        this.setBarPainter(new StandardBarPainter());
        // XYBar 渲染
        this.setXYBarPainter(new StandardXYBarPainter());
        this.setItemLabelPaint(Color.black);
        // 温度计
        this.setThermometerPaint(Color.white);
    }
}

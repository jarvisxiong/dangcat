package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.theme.CustomChartTheme;

import java.awt.*;

/**
 * 颜色工厂。
 * @author dangcat
 * 
 */
public class ColorFactory
{
    /** 最大图表表现的内容。 */
    public static final int MAX_ITEM = 10;
    /**
     * 固定颜色列表。
     */
    private static final Color[] colors = CustomChartTheme.COLORS;
    private static Paint[] paints = CustomChartTheme.COLORS;

    public static Color[] getAllColors()
    {
        return colors;
    }

    /**
     * 读取颜色的表达式。
     */
    public static String getColorStyle(Color color)
    {
        String style = null;
        if (color != null)
        {
            String styleText = String.format("%1$#6x", color.getRGB());
            styleText = "#" + styleText.substring(styleText.length() - 6);
            style = styleText.toUpperCase();
        }
        return style;
    }

    public static Paint[] getPaints()
    {
        if (paints == null)
        {
            Paint[] paintArray = new Paint[colors.length];
            for (int i = 0; i < colors.length; i++)
                paintArray[i] = new GradientPaint(0.0F, 0.0F, colors[i], 0.0F, 0.0F, Color.white);
            paints = paintArray;
        }
        return paints;
    }

    /**
     * 按照序号取颜色。
     * @param index 序号位置。
     */
    public static Color sequence(int index)
    {
        if (index < 0)
            index = 0;
        else if (index >= colors.length)
            index = index % colors.length;
        return colors[index];
    }
}

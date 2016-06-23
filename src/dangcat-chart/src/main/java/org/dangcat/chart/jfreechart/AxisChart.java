package org.dangcat.chart.jfreechart;

import java.awt.Font;

import org.dangcat.chart.jfreechart.data.DataConverter;
import org.jfree.chart.axis.Axis;

abstract class AxisChart extends ChartBase
{
    /** 横坐标标签个数。 */
    private int domainAxiaCount = 12;
    /** 横坐标标题。 */
    private String domainTitle;
    /** 纵坐标标题。 */
    private String rangeTitle;

    public int getDomainAxiaCount()
    {
        return this.domainAxiaCount;
    }

    public String getDomainTitle()
    {
        return this.domainTitle;
    }

    public String getDomainTitle(int i)
    {
        return null;
    }

    protected double getMaxValue()
    {
        return this.getMaxValue(0);
    }

    protected double getMaxValue(int i)
    {
        double maxValue = 0.0;
        DataConverter dataConverter = this.getDataConverter(i);
        if (dataConverter != null)
            maxValue = dataConverter.getMaxValue();
        return ChartUtils.getMaxValue(maxValue);
    }

    protected double getMinValue()
    {
        return this.getMinValue(0);
    }

    protected double getMinValue(int i)
    {
        double minValue = 0.0;
        DataConverter dataConverter = this.getDataConverter(i);
        if (dataConverter != null)
            minValue = dataConverter.getMinValue();
        return ChartUtils.getMaxValue(minValue);
    }

    public String getRangeTitle()
    {
        return this.getRangeTitle(0);
    }

    protected String getRangeTitle(int i)
    {
        DataConverter dataConverter = this.getDataConverter(i);
        if (dataConverter != null)
            return dataConverter.getRangeTitle(this.rangeTitle);
        return this.rangeTitle;
    }

    protected void initAxis(Axis axis)
    {
        Font chartFont = this.getChartFont();
        if (chartFont != null)
        {
            // 设置Y轴坐标上的文字
            axis.setTickLabelFont(chartFont);
            // 设置Y轴的标题文字
            axis.setLabelFont(chartFont);
        }
    }

    public void setDomainAxiaCount(int domainAxiaCount)
    {
        this.domainAxiaCount = domainAxiaCount;
    }

    public void setDomainTitle(String domainTitle)
    {
        this.domainTitle = domainTitle;
    }

    public void setRangeTitle(String rangeTitle)
    {
        this.rangeTitle = rangeTitle;
    }
}

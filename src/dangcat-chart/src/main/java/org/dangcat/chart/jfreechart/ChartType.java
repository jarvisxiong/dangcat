package org.dangcat.chart.jfreechart;

/**
 * 统计图形。
 *
 * @author dangcat
 */
public enum ChartType {
    /**
     * 区域图。
     */
    Area(1),
    /**
     * 柱状图。
     */
    Bar(2),
    /**
     * 柱状堆栈图。
     */
    BarTime(7),
    /**
     * 组合趋势图。
     */
    Combined(6),
    /**
     * 线状图。
     */
    Line(3),
    /**
     * 多栏位线状图。
     */
    MultiLine(5),
    /**
     * 无。
     */
    None(0),
    /**
     * 饼状图。
     */
    Pie(4);

    /**
     * 统计图形值。
     */
    private final int value;

    /**
     * 构造统计类型。
     *
     * @param value 统计图形值。
     */
    ChartType(int value) {
        this.value = value;
    }

    /**
     * 取得统计图形。
     *
     * @return 统计图形值。
     */
    public int getValue() {
        return this.value;
    }
}

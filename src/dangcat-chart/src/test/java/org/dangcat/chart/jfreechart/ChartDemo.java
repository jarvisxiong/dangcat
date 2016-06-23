package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.theme.CustomChartTheme;
import org.jfree.chart.ChartFactory;

import javax.swing.*;
import java.awt.*;

public class ChartDemo extends ChartDemoBase
{
    private static final long serialVersionUID = 1L;

    public ChartDemo()
    {
        super(ChartDemo.class.getSimpleName());
        this.setPreferredSize(new Dimension(800, 600));
    }

    public static void main(final String[] args) {
        ChartFactory.setChartTheme(new CustomChartTheme("CN"));
        show(new ChartDemo());
    }

    private void createTabbedPane(ChartDemoBase chartDemo, JTabbedPane parentTabbedPane)
    {
        JTabbedPane tabbedPane = new JTabbedPane();
        chartDemo.createTabbedPane(tabbedPane);
        parentTabbedPane.add(chartDemo.getClass().getSimpleName(), tabbedPane);
    }

    @Override
    protected void createTabbedPane(JTabbedPane tabbedPane)
    {
        this.createTabbedPane(new PieChartDemo(), tabbedPane);
        this.createTabbedPane(new BarChartDemo(), tabbedPane);
        this.createTabbedPane(new BarTimeChartDemo(), tabbedPane);
        this.createTabbedPane(new StackedBarChartDemo(), tabbedPane);
        this.createTabbedPane(new AreaChartDemo(), tabbedPane);
        this.createTabbedPane(new LineChartDemo(), tabbedPane);
        this.createTabbedPane(new MultiLineChartDemo(), tabbedPane);
        this.createTabbedPane(new DiffLineChartDemo(), tabbedPane);
        this.createTabbedPane(new CombinedChartDemo(), tabbedPane);
    }
}

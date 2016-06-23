package org.dangcat.chart.jfreechart;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.dangcat.swing.JFrameExt;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.layout.LCBLayout;

public abstract class ChartDemoBase extends JFrameExt
{
    private static final long serialVersionUID = 1L;

    public ChartDemoBase(String title)
    {
        super(title);
    }

    protected JPanel createChartPanel(JFreeChart chart)
    {
        ChartPanel panel = new ChartPanel(chart);
        panel.setLayout(new LCBLayout(20));
        panel.setPreferredSize(new Dimension(700, 450));
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        return panel;
    }

    @Override
    protected Container createContentPane()
    {
        JPanel content = new JPanel(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        this.createTabbedPane(tabbedPane);
        content.add(tabbedPane);
        return content;
    }

    protected abstract void createTabbedPane(JTabbedPane tabbedPane);

    protected void initTimeChart(TimeChart timeChart, Date beginTime, Date endTime)
    {
        timeChart.setBeginTime(beginTime);
        timeChart.setEndTime(endTime);
        timeChart.setTimeStep(300);
    }

    protected void renderFile(ChartBase chartBase, String name)
    {
        chartBase.setImageMapId(name + "_ImagMap");

        File imgFile = new File("./log/" + name + ".jpg");
        try
        {
            chartBase.render(imgFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        File imgMapFile = new File("./log/" + name + ".txt");
        try
        {
            PrintWriter printWriter = new PrintWriter(imgMapFile);
            printWriter.print(chartBase.getImageMap());
            printWriter.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}

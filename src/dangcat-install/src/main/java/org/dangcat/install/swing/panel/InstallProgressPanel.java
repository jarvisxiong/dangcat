package org.dangcat.install.swing.panel;

import org.apache.log4j.Logger;
import org.dangcat.commons.log.LogCallback;
import org.dangcat.install.swing.ConfigPanel;
import org.dangcat.swing.JLogPane;
import org.dangcat.swing.JProgressPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class InstallProgressPanel extends ConfigPanel implements LogCallback
{
    protected static final Logger logger = Logger.getLogger(InstallProgressPanel.class);
    private static final long serialVersionUID = 1L;

    private JLogPane logPane = null;
    private JProgressPanel progressPanel = null;

    private JPanel createLogPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        this.logPane = new JLogPane();
        JScrollPane scrollPane = new JScrollPane(this.logPane);
        scrollPane.setBorder(new TitledBorder(""));
        panel.add(scrollPane, constraints);
        return panel;
    }

    @Override
    public void debug(String message)
    {
        this.logPane.debug(message);
    }

    @Override
    public void error(String message)
    {
        this.logPane.error(message);
    }

    @Override
    public void fatal(String message)
    {
        this.logPane.fatal(message);
    }

    public Logger getLogger(Class<?> classType)
    {
        return new org.dangcat.commons.log.Logger(Logger.getLogger(classType), "InstallCallback", this.logPane);
    }

    public int getMin()
    {
        return this.progressPanel.getMin();
    }

    public void setMin(int min) {
        this.progressPanel.setMin(min);
    }

    public int getValue()
    {
        return this.progressPanel.getValue();
    }

    public void setValue(int value) {
        this.progressPanel.setValue(value);
    }

    @Override
    public void info(String message)
    {
        this.logPane.info(message);
    }

    @Override
    public void initialize()
    {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.progressPanel = new JProgressPanel(false);
        this.progressPanel.setBorder(new TitledBorder(this.getText("InstallProgress")));
        this.add(this.progressPanel);
        this.add(this.createLogPanel());
    }

    public void reset()
    {
        this.progressPanel.setValue(0);
        this.logPane.clear();
    }

    public void setMax(int max)
    {
        this.progressPanel.setMax(max);
    }

    @Override
    public void warn(String message)
    {
        this.logPane.warn(message);
    }
}

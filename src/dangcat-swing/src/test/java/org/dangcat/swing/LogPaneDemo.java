package org.dangcat.swing;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogPaneDemo extends JFrameExt
{
    private static final long serialVersionUID = 1L;
    protected static Logger logger = Logger.getLogger(LogPaneDemo.class);
    private JLogPane logPane = null;

    public LogPaneDemo()
    {
        super(LogPaneDemo.class.getSimpleName());
    }

    public static void main(final String[] args) {
        show(new LogPaneDemo());
    }

    @Override
    protected Container createContentPane()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(400, 300));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;

        JButton generateButton = new JButton("Generate");
        generateButton.setAlignmentX(CENTER_ALIGNMENT);
        generateButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                LogPaneDemo.this.log();
            }
        });
        panel.add(generateButton);

        this.logPane = new JLogPane();
        JScrollPane scrollPane = new JScrollPane(this.logPane);
        scrollPane.setBorder(new EtchedBorder());
        constraints.gridy = 1;
        panel.add(scrollPane, constraints);
        return panel;
    }

    private void log()
    {
        logger.info("Test info message");
        logger.warn("Test warn message");
        logger.debug("Test debug message");
        logger.fatal("Test fatal message");
        logger.error("Test error message");
        logger.error("Test error message", new Exception("run time exception"));
    }

    @Override
    public void pack()
    {
        super.pack();
        this.log();
    }
}

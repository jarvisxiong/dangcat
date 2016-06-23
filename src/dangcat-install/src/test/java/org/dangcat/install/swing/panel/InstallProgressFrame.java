package org.dangcat.install.swing.panel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.dangcat.commons.timer.Timer;
import org.dangcat.swing.JFrameExt;

public class InstallProgressFrame extends JFrameExt implements Runnable
{
    private static Dimension PERFECT_SIZE = new Dimension(800, 600);
    private static final long serialVersionUID = 1L;

    public static void main(final String[] args)
    {
        show(new InstallProgressFrame());
    }

    private InstallProgressPanel installProgressPanel = null;
    private Logger logger = null;
    private Timer timer = null;

    @Override
    protected Container createContentPane()
    {
        JPanel content = new JPanel(new BorderLayout());
        content.setPreferredSize(PERFECT_SIZE);

        this.installProgressPanel = new InstallProgressPanel();
        this.installProgressPanel.initialize();
        this.installProgressPanel.setMin(0);
        this.installProgressPanel.setMax(100);
        this.installProgressPanel.setValue(0);
        this.logger = this.installProgressPanel.getLogger(this.getClass());
        content.add(this.installProgressPanel);

        this.timer = new Timer("Timer1", this, 1000, 1000);
        this.timer.start();

        return content;
    }

    @Override
    public void run()
    {
        int value = this.installProgressPanel.getValue();
        if (value >= 100)
        {
            this.logger.info("install finished.");
            this.timer.stop();
        }
        else
        {
            if (value == 0)
                this.logger.info("install begin.");
            value += 2;
            this.installProgressPanel.setValue(value);
            this.logger.info("install step " + value);
        }
    }
}

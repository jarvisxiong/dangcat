package org.dangcat.install.swing.panel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JPanel;

import org.dangcat.swing.JFrameExt;

public class InstallPathFrame extends JFrameExt
{
    private static Dimension PERFECT_SIZE = new Dimension(800, 600);
    private static final long serialVersionUID = 1L;

    public static void main(final String[] args)
    {
        show(new InstallPathFrame());
    }

    @Override
    protected Container createContentPane()
    {
        JPanel content = new JPanel(new BorderLayout());
        content.setPreferredSize(PERFECT_SIZE);

        InstallSelectPanel installSelectPanel = new InstallSelectPanel();
        installSelectPanel.initialize();
        installSelectPanel.addComponent("zxdna-radius", "Radius App Server", true);
        installSelectPanel.addComponent("zxdna-radiusdb", "Radius DB Server", true);
        installSelectPanel.addComponent("zxdna-jms", "JMS Server", true);
        installSelectPanel.addComponent("zxdna-websrv", "Radius Web Server", true);
        content.add(installSelectPanel);

        return content;
    }
}

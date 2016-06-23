package org.dangcat.install.swing.panel;

import org.dangcat.swing.JFrameExt;

import javax.swing.*;
import java.awt.*;

public class InstallPathFrame extends JFrameExt
{
    private static final long serialVersionUID = 1L;
    private static Dimension PERFECT_SIZE = new Dimension(800, 600);

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

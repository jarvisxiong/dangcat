package org.dangcat.install.swing.panel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JPanel;

import org.dangcat.install.database.mysql.MySqlInstaller;
import org.dangcat.install.database.swing.DatabaseInstallPanel;
import org.dangcat.swing.JFrameExt;

public class DatabaseInstallFrame extends JFrameExt
{
    private static Dimension PERFECT_SIZE = new Dimension(800, 600);
    private static final long serialVersionUID = 1L;

    public static void main(final String[] args)
    {
        show(new DatabaseInstallFrame());
    }

    @Override
    protected Container createContentPane()
    {
        JPanel content = new JPanel(new BorderLayout());
        content.setPreferredSize(PERFECT_SIZE);

        DatabaseInstallPanel databaseInstallPanel = new DatabaseInstallPanel();
        databaseInstallPanel.setDatabaseInstaller(new MySqlInstaller());
        databaseInstallPanel.initialize();
        content.add(databaseInstallPanel);

        return content;
    }
}

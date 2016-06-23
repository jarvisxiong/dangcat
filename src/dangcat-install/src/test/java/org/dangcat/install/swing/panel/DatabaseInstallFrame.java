package org.dangcat.install.swing.panel;

import org.dangcat.install.database.mysql.MySqlInstaller;
import org.dangcat.install.database.swing.DatabaseInstallPanel;
import org.dangcat.swing.JFrameExt;

import javax.swing.*;
import java.awt.*;

public class DatabaseInstallFrame extends JFrameExt
{
    private static final long serialVersionUID = 1L;
    private static Dimension PERFECT_SIZE = new Dimension(800, 600);

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

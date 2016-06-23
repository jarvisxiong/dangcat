package org.dangcat.install.swing.panel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JPanel;

import org.dangcat.commons.database.MySqlDatabase;
import org.dangcat.install.database.swing.DatabaseConfigPanel;
import org.dangcat.swing.JFrameExt;

public class DatabaseConfigFrame extends JFrameExt
{
    private static Dimension PERFECT_SIZE = new Dimension(800, 600);
    private static final long serialVersionUID = 1L;

    public static void main(final String[] args)
    {
        show(new DatabaseConfigFrame());
    }

    @Override
    protected Container createContentPane()
    {
        JPanel content = new JPanel(new BorderLayout());
        content.setPreferredSize(PERFECT_SIZE);

        DatabaseConfigPanel databaseConfigPanel = new DatabaseConfigPanel();
        databaseConfigPanel.setDatabase(new MySqlDatabase());
        databaseConfigPanel.initialize();
        content.add(databaseConfigPanel);

        return content;
    }
}

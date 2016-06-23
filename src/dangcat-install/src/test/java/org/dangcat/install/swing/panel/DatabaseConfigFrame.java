package org.dangcat.install.swing.panel;

import org.dangcat.commons.database.MySqlDatabase;
import org.dangcat.install.database.swing.DatabaseConfigPanel;
import org.dangcat.swing.JFrameExt;

import javax.swing.*;
import java.awt.*;

public class DatabaseConfigFrame extends JFrameExt {
    private static final long serialVersionUID = 1L;
    private static Dimension PERFECT_SIZE = new Dimension(800, 600);

    public static void main(final String[] args) {
        show(new DatabaseConfigFrame());
    }

    @Override
    protected Container createContentPane() {
        JPanel content = new JPanel(new BorderLayout());
        content.setPreferredSize(PERFECT_SIZE);

        DatabaseConfigPanel databaseConfigPanel = new DatabaseConfigPanel();
        databaseConfigPanel.setDatabase(new MySqlDatabase());
        databaseConfigPanel.initialize();
        content.add(databaseConfigPanel);

        return content;
    }
}

package org.dangcat.swing;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PanelUtils {
    public static void decorateTitleBoder(JPanel panel, JPanel component, String title) {
        component.setBorder(new TitledBorder(title == null ? "" : title));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.gridwidth = 1;
        constraints.gridy = 0;
        constraints.weighty = 1;
        constraints.gridheight = 1;
        panel.add(component, constraints);
    }

    public static JPanel decorateTitleBoder(JPanel component, String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        decorateTitleBoder(panel, component, title);
        return panel;
    }
}

package org.dangcat.install.swing;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Collection;

public class ConfigTabPanel extends ConfigPanel {
    private static final long serialVersionUID = 1L;
    private Collection<ConfigPanel> configPanels = null;
    private JTabbedPane tabbedPane = new JTabbedPane();

    public ConfigTabPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(this.tabbedPane);
    }

    public Collection<ConfigPanel> getConfigPanels() {
        return this.configPanels;
    }

    public void setConfigPanels(Collection<ConfigPanel> configPanels) {
        this.configPanels = configPanels;
    }

    @Override
    public void initialize() {
        this.tabbedPane.removeAll();

        Collection<ConfigPanel> configPanels = this.getConfigPanels();
        if (configPanels != null && !configPanels.isEmpty()) {
            int index = 1;
            for (ConfigPanel configPanel : configPanels) {
                String title = index + "." + configPanel.getTitle();
                this.tabbedPane.add(title, configPanel);
                int mnemonic = KeyEvent.VK_0 + index;
                this.tabbedPane.setMnemonicAt(this.tabbedPane.getComponentCount() - 1, mnemonic);
                this.tabbedPane.setDisplayedMnemonicIndexAt(this.tabbedPane.getComponentCount() - 1, title.indexOf(mnemonic));
                index++;
            }
        }
    }

    @Override
    public boolean validateData() {
        Collection<ConfigPanel> configPanels = this.getConfigPanels();
        if (configPanels != null && !configPanels.isEmpty()) {
            for (ConfigPanel configPanel : configPanels) {
                if (!configPanel.validateData()) {
                    this.tabbedPane.setSelectedComponent(configPanel);
                    return false;
                }
            }
        }
        return super.validateData();
    }
}

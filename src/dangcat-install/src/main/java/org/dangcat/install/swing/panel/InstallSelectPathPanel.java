package org.dangcat.install.swing.panel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Map;

public class InstallSelectPathPanel extends InstallSelectPanel
{
    private static final long serialVersionUID = 1L;
    private InstallPathPanel installPathPanel = null;

    private JPanel createInstallPathPanel()
    {
        this.installPathPanel = new InstallPathPanel();
        this.installPathPanel.setBorder(new TitledBorder(this.getText("InstallPath")));
        this.installPathPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 500));
        this.installPathPanel.initialize();
        return this.installPathPanel;
    }

    public InstallPathPanel getInstallPathPanel()
    {
        return this.installPathPanel;
    }

    @Override
    public void initialize()
    {
        super.initialize();
        this.add(this.createInstallPathPanel());
    }

    @Override
    public boolean validateData()
    {
        if (!super.validateData())
            return false;

        Map<String, String> selectedMap = this.getComponentList().getSelectedMap();
        return this.installPathPanel.validateData(selectedMap);
    }
}

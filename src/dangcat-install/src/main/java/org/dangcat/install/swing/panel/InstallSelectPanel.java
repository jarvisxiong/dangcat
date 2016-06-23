package org.dangcat.install.swing.panel;

import org.apache.log4j.Logger;
import org.dangcat.install.swing.ConfigPanel;
import org.dangcat.swing.JCheckBoxList;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Collection;

public class InstallSelectPanel extends ConfigPanel
{
    protected static final Logger logger = Logger.getLogger(InstallSelectPanel.class);
    private static final long serialVersionUID = 1L;
    private JCheckBoxList componentList = null;

    public void addComponent(String name, String titile, boolean selected)
    {
        this.componentList.addItem(name, titile, selected);
    }

    private JPanel createComponentList()
    {
        this.componentList = new JCheckBoxList();
        this.componentList.setBorder(new TitledBorder(this.getText("InstallComponents")));
        this.componentList.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        return this.componentList;
    }

    public JCheckBoxList getComponentList()
    {
        return this.componentList;
    }

    public Collection<String> getSelectedValues()
    {
        return this.componentList.getSelectedValues();
    }

    public void setSelectedValues(Collection<String> values) {
        this.componentList.setSelectedValues(values);
    }

    @Override
    public void initialize()
    {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(this.createComponentList());
    }

    @Override
    public boolean validateData()
    {
        Collection<String> selectedValues = this.componentList.getSelectedValues();
        if (selectedValues == null || selectedValues.isEmpty())
        {
            JOptionPane.showMessageDialog(this, this.getText("SelectedComponentsEmpty"));
            return false;
        }
        return true;
    }
}

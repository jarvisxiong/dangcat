package org.dangcat.install.swing.panel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;
import org.dangcat.commons.formator.OctetsFormator;
import org.dangcat.commons.utils.ImageUtils;
import org.dangcat.install.swing.ConfigPanel;

public class InstallPathPanel extends ConfigPanel
{
    private static final String INSTALL_PATH = "InstallPath";
    protected static final Logger logger = Logger.getLogger(InstallPathPanel.class);
    private static Dimension MinimumSize = new Dimension(40, 22);
    private static final long serialVersionUID = 1L;

    private OctetsFormator formator = new OctetsFormator();
    private long freeSpace = 0l;
    private JLabel freeSpaceValueLabel = null;
    private JTextField installPathTextField = null;
    private long minNeedSpace = 0l;
    private JLabel needSpaceValueLabel = null;

    private void addComponents(int y, JLabel label, Component component, JButton button)
    {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0.1;
        constraints.gridy = y;
        label.setMinimumSize(MinimumSize);
        this.add(label, constraints);

        constraints.weightx = 0.7;
        constraints.gridwidth = 3;
        constraints.gridx = 1;
        constraints.gridy = y;
        this.add(component, constraints);

        if (button != null)
        {
            constraints.insets = new Insets(2, 2, 2, 40);
            constraints.weightx = 0.1;
            constraints.gridwidth = 1;
            constraints.gridx = 4;
            constraints.gridwidth = 1;
            constraints.gridy = y;
            button.setMargin(new Insets(0, 5, 0, 5));
            button.setMinimumSize(MinimumSize);
            this.add(button, constraints);
        }
    }

    private void addComponents(int y, JLabel needSpaceTitleLabel, JLabel freeSpaceTitleLabel)
    {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.weightx = 0.1;
        constraints.gridwidth = 1;
        constraints.gridy = y;
        needSpaceTitleLabel.setMinimumSize(MinimumSize);
        this.add(needSpaceTitleLabel, constraints);

        constraints.insets = new Insets(2, 2, 2, 50);
        constraints.gridx = 1;
        this.needSpaceValueLabel.setMinimumSize(MinimumSize);
        this.add(this.needSpaceValueLabel, constraints);

        constraints.gridx = 2;
        freeSpaceTitleLabel.setMinimumSize(MinimumSize);
        this.add(freeSpaceTitleLabel, constraints);

        constraints.gridx = 3;
        this.freeSpaceValueLabel.setMinimumSize(MinimumSize);
        this.add(this.freeSpaceValueLabel, constraints);
    }

    @Override
    protected void choicePath(JTextField pathTextField)
    {
        super.choicePath(pathTextField);
        this.installPathChanged();
    }

    private void createInstallPathPanel()
    {
        int y = 0;

        JLabel installPathLabel = new JLabel(this.getText(INSTALL_PATH), JLabel.RIGHT);
        installPathLabel.setLabelFor(this.installPathTextField);
        this.installPathTextField = new JTextField();
        this.installPathTextField.setColumns(Short.MAX_VALUE);
        this.installPathTextField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void changedUpdate(DocumentEvent e)
            {
                InstallPathPanel.this.installPathChanged();
            }

            @Override
            public void insertUpdate(DocumentEvent e)
            {
                InstallPathPanel.this.installPathChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                InstallPathPanel.this.installPathChanged();
            }
        });
        ImageIcon browseImage = ImageUtils.loadImageIcon(this.getClass(), "browse.png");
        JButton installPathButton = new JButton(this.getText("Browse"), browseImage);
        installPathButton.setAlignmentX(CENTER_ALIGNMENT);
        installPathButton.setMnemonic(KeyEvent.VK_B);
        installPathButton.setDisplayedMnemonicIndex(installPathButton.getText().indexOf("B"));
        installPathButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                InstallPathPanel.this.choicePath(InstallPathPanel.this.installPathTextField);
            }
        });
        this.addComponents(y++, installPathLabel, this.installPathTextField, installPathButton);

        JLabel needSpaceTitleLabel = new JLabel(this.getText("NeedSpace"), JLabel.RIGHT);
        this.needSpaceValueLabel = new JLabel(this.getNeedSpaceText(), JLabel.LEFT);
        JLabel freeSpaceTitleLabel = new JLabel(this.getText("FreeSpace"), JLabel.RIGHT);
        this.freeSpaceValueLabel = new JLabel(this.getFreeSpaceText(), JLabel.LEFT);
        this.addComponents(y++, needSpaceTitleLabel, freeSpaceTitleLabel);
        this.addGridBagSpace(this, y);
    }

    public long getFreeSpace()
    {
        return this.freeSpace;
    }

    private String getFreeSpaceText()
    {
        return this.formator.format(this.freeSpace);
    }

    public String getInstallPath()
    {
        return this.installPathTextField.getText();
    }

    public long getMinNeedSpace()
    {
        return this.minNeedSpace;
    }

    private String getNeedSpaceText()
    {
        return this.formator.format(this.minNeedSpace);
    }

    @Override
    public void initialize()
    {
        this.setLayout(new GridBagLayout());
        this.createInstallPathPanel();
    }

    private void installPathChanged()
    {
        File file = new File(this.installPathTextField.getText());
        while (!file.exists() && file.getParentFile() != null)
            file = file.getParentFile();
        if (file.exists())
            this.setFreeSpace(file.getFreeSpace());
        else
            this.setFreeSpace(0l);
    }

    public void setFreeSpace(long freeSpace)
    {
        this.freeSpace = freeSpace;
        this.freeSpaceValueLabel.setText(this.getFreeSpaceText());
    }

    public void setInstallPath(String value)
    {
        this.installPathTextField.setText(value);
    }

    public void setMinNeedSpace(long minNeedSpace)
    {
        this.minNeedSpace = minNeedSpace;
        this.needSpaceValueLabel.setText(this.getNeedSpaceText());
    }

    @Override
    public boolean validateData()
    {
        if (!ValidateUtils.validateEmpty(this, this.installPathTextField, INSTALL_PATH))
            return false;
        return true;
    }

    public boolean validateData(Map<String, String> selectedMap)
    {
        for (Entry<String, String> selectedService : selectedMap.entrySet())
        {
            String value = this.installPathTextField.getText() + File.separator + selectedService.getKey();
            if (!ValidateUtils.validateInstallPath(this, this.installPathTextField, value, selectedService.getValue()))
                return false;
        }
        return true;
    }
}
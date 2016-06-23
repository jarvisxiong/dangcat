package org.dangcat.business.code.swing;

import org.apache.log4j.Logger;
import org.dangcat.business.code.service.BusinessCodeGenerator;
import org.dangcat.swing.JLogPane;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class BusinessCodePanel extends CodePanel
{
    private static final String JNDI_NAME = "JndiName";
    private static final String MODULE_NAME = "ModuleName";
    private static final String PACKAGE = "Package";
    private static final String PATH = "Path";
    private static final long serialVersionUID = 1L;
    private static Dimension MinimumSize = new Dimension(40, 22);
    private JTextField jndiNameTextField = null;
    private JLogPane logPane = null;
    private JTextField moduleNameTextField = null;
    private JTextField packageTextField = null;
    private JTextField pathTextField = null;

    private void addComponents(JPanel panel, int y, JLabel label, JTextField textField, JButton button)
    {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.1;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridy = y;
        label.setMinimumSize(MinimumSize);
        panel.add(label, constraints);

        constraints.weightx = 0.8;
        constraints.gridx = 1;
        constraints.gridwidth = 3;
        constraints.gridy = y;
        panel.add(textField, constraints);

        constraints.weightx = 0.1;
        constraints.gridx = 4;
        constraints.gridwidth = 1;
        constraints.gridy = y;
        button.setMargin(new Insets(0, 10, 0, 10));
        button.setMinimumSize(MinimumSize);
        panel.add(button, constraints);
    }

    private JPanel createConfigPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, 0));
        panel.setBorder(new TitledBorder(this.getText("Config")));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.1;
        constraints.gridwidth = 1;
        constraints.gridy = 0;

        this.moduleNameTextField = new JTextField();
        this.moduleNameTextField.setColumns(Short.MAX_VALUE);
        JLabel moduleNameLabel = new JLabel(this.getText(MODULE_NAME), JLabel.RIGHT);
        moduleNameLabel.setLabelFor(this.moduleNameTextField);
        constraints.gridx = 0;
        moduleNameLabel.setMinimumSize(MinimumSize);
        panel.add(moduleNameLabel, constraints);
        constraints.gridx = 1;
        this.moduleNameTextField.setMinimumSize(MinimumSize);
        panel.add(this.moduleNameTextField, constraints);

        this.jndiNameTextField = new JTextField();
        this.jndiNameTextField.setColumns(Short.MAX_VALUE);
        JLabel jndiNameLabel = new JLabel(this.getText(JNDI_NAME), JLabel.RIGHT);
        jndiNameLabel.setLabelFor(this.jndiNameTextField);
        constraints.gridx = 2;
        jndiNameLabel.setMinimumSize(MinimumSize);
        panel.add(jndiNameLabel, constraints);
        constraints.gridx = 3;
        this.jndiNameTextField.setMinimumSize(MinimumSize);
        panel.add(this.jndiNameTextField, constraints);

        this.pathTextField = new JTextField();
        this.pathTextField.setColumns(Short.MAX_VALUE);
        JLabel pathLabel = new JLabel(this.getText(PATH), JLabel.RIGHT);
        pathLabel.setLabelFor(this.pathTextField);
        JButton browseButton = new JButton(this.getText("Browse"));
        browseButton.setAlignmentX(CENTER_ALIGNMENT);
        browseButton.setMnemonic(KeyEvent.VK_B);
        browseButton.setDisplayedMnemonicIndex(browseButton.getText().indexOf("B"));
        browseButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                BusinessCodePanel.this.choicePath(BusinessCodePanel.this.pathTextField);
            }
        });
        this.addComponents(panel, 1, pathLabel, this.pathTextField, browseButton);

        this.packageTextField = new JTextField();
        this.packageTextField.setColumns(Short.MAX_VALUE);
        JLabel packageLabel = new JLabel(this.getText(PACKAGE), JLabel.RIGHT);
        packageLabel.setLabelFor(this.packageTextField);
        JButton generateButton = new JButton(this.getText("Generate"));
        generateButton.setAlignmentX(CENTER_ALIGNMENT);
        generateButton.setMnemonic(KeyEvent.VK_G);
        generateButton.setDisplayedMnemonicIndex(generateButton.getText().indexOf("G"));
        generateButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                BusinessCodePanel.this.generate();
            }
        });
        this.addComponents(panel, 2, packageLabel, this.packageTextField, generateButton);

        return panel;
    }

    @Override
    protected Logger createLogger()
    {
        return new org.dangcat.commons.log.Logger(Logger.getLogger(BusinessCodeGenerator.class), "BusinessCallback", this.logPane);
    }

    private JPanel createLogPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        this.logPane = new JLogPane();
        JScrollPane scrollPane = new JScrollPane(this.logPane);
        scrollPane.setBorder(new EtchedBorder());
        panel.add(scrollPane, constraints);
        return panel;
    }

    private void generate()
    {
        if (!this.validateEmpty(this.moduleNameTextField, MODULE_NAME))
            return;
        if (!this.validateEmpty(this.jndiNameTextField, JNDI_NAME))
            return;
        if (!this.validateEmpty(this.pathTextField, PATH))
            return;
        if (!this.validateEmpty(this.packageTextField, PACKAGE))
            return;

        try
        {
            this.getLogger().info(this.getText("BeginGenerate") + ": " + this.pathTextField.getText());
            BusinessCodeGenerator businessCodeGenerator = new BusinessCodeGenerator(this.moduleNameTextField.getText(), this.jndiNameTextField.getText());
            businessCodeGenerator.setParentComponent(this);
            businessCodeGenerator.setLogger(this.getLogger());
            businessCodeGenerator.setPackageName(this.packageTextField.getText());
            businessCodeGenerator.setOutputDir(this.pathTextField.getText());
            businessCodeGenerator.generate();
            this.getLogger().info(this.getText("EndGenerate"));
        }
        catch (Exception e)
        {
            this.getLogger().error(this, e);
            JOptionPane.showMessageDialog(this, this.getText("GenerateError"));
        }
    }

    @Override
    public void initialize()
    {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(this.createConfigPanel());
        this.add(this.createLogPanel());
    }
}

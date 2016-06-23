package org.dangcat.business.code.swing;

import org.apache.log4j.Logger;
import org.dangcat.business.code.server.ServerCodeGenerator;
import org.dangcat.swing.JLogPane;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class ServerCodePanel extends CodePanel {
    private static final String PACKAGE_NAME = "Package";
    private static final String PATH = "Path";
    private static final long serialVersionUID = 1L;
    private static final String SERVER_NAME = "ServerName";
    private static Dimension MinimumSize = new Dimension(40, 22);
    private JLogPane logPane = null;
    private JTextField packageNameTextField = null;
    private JTextField pathTextField = null;
    private JTextField serverNameTextField = null;

    private void addComponents(JPanel panel, int y, JLabel label, JTextField textField, JButton button) {
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

        if (button != null) {
            constraints.weightx = 0.1;
            constraints.gridx = 4;
            constraints.gridwidth = 1;
            constraints.gridy = y;
            button.setMargin(new Insets(0, 10, 0, 10));
            button.setMinimumSize(MinimumSize);
            panel.add(button, constraints);
        }
    }

    private JPanel createConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, 0));
        panel.setBorder(new TitledBorder(this.getText("Config")));

        this.serverNameTextField = new JTextField();
        this.serverNameTextField.setColumns(Short.MAX_VALUE);
        JLabel jndiNameLabel = new JLabel(this.getText(SERVER_NAME), JLabel.RIGHT);
        jndiNameLabel.setLabelFor(this.serverNameTextField);
        jndiNameLabel.setMinimumSize(MinimumSize);
        this.serverNameTextField.setMinimumSize(MinimumSize);
        this.addComponents(panel, 1, jndiNameLabel, this.serverNameTextField, null);

        this.pathTextField = new JTextField();
        this.pathTextField.setColumns(Short.MAX_VALUE);
        JLabel pathLabel = new JLabel(this.getText(PATH), JLabel.RIGHT);
        pathLabel.setLabelFor(this.pathTextField);
        JButton browseButton = new JButton(this.getText("Browse"));
        browseButton.setAlignmentX(CENTER_ALIGNMENT);
        browseButton.setMnemonic(KeyEvent.VK_B);
        browseButton.setDisplayedMnemonicIndex(browseButton.getText().indexOf("B"));
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServerCodePanel.this.choicePath(ServerCodePanel.this.pathTextField);
            }
        });
        this.addComponents(panel, 2, pathLabel, this.pathTextField, browseButton);

        this.packageNameTextField = new JTextField();
        this.packageNameTextField.setColumns(Short.MAX_VALUE);
        JLabel packageLabel = new JLabel(this.getText(PACKAGE_NAME), JLabel.RIGHT);
        packageLabel.setLabelFor(this.packageNameTextField);
        JButton generateButton = new JButton(this.getText("Generate"));
        generateButton.setAlignmentX(CENTER_ALIGNMENT);
        generateButton.setMnemonic(KeyEvent.VK_G);
        generateButton.setDisplayedMnemonicIndex(generateButton.getText().indexOf("G"));
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServerCodePanel.this.generate();
            }
        });
        this.addComponents(panel, 3, packageLabel, this.packageNameTextField, generateButton);

        return panel;
    }

    @Override
    protected Logger createLogger() {
        return new org.dangcat.commons.log.Logger(Logger.getLogger(ServerCodeGenerator.class), "BusinessCallback", this.logPane);
    }

    private JPanel createLogPanel() {
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

    private void generate() {
        if (!this.validateEmpty(this.packageNameTextField, PACKAGE_NAME))
            return;
        if (!this.validateEmpty(this.serverNameTextField, SERVER_NAME))
            return;
        if (!this.validateEmpty(this.pathTextField, PATH))
            return;

        try {
            this.getLogger().info(this.getText("BeginGenerate") + ": " + this.pathTextField.getText());
            ServerCodeGenerator serverCodeGenerator = new ServerCodeGenerator(this.packageNameTextField.getText(), this.serverNameTextField.getText());
            serverCodeGenerator.setParentComponent(this);
            serverCodeGenerator.setLogger(this.getLogger());
            serverCodeGenerator.setOutputDir(this.pathTextField.getText());
            serverCodeGenerator.generate();
            this.getLogger().info(this.getText("EndGenerate"));
        } catch (Exception e) {
            this.getLogger().error(this, e);
            JOptionPane.showMessageDialog(this, this.getText("GenerateError"));
        }
    }

    @Override
    public void initialize() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(this.createConfigPanel());
        this.add(this.createLogPanel());
    }
}

package org.dangcat.install.database.swing;

import org.apache.log4j.Logger;
import org.dangcat.install.database.DatabaseInstaller;
import org.dangcat.install.swing.ConfigPanel;
import org.dangcat.install.swing.panel.ValidateUtils;
import org.dangcat.swing.keyadapter.MaxLengthKeyAdapter;
import org.dangcat.swing.keyadapter.PortKeyAdapter;

import javax.swing.*;
import java.awt.*;

public class DatabaseInstallPanel extends ConfigPanel {
    protected static final Logger logger = Logger.getLogger(DatabaseInstallPanel.class);
    private static final String DATABASE_NAME = "DatabaseName";
    private static final String PASSWORD = "Password";
    private static final String PASSWORD2 = "Password2";
    private static final String PORT = "Port";
    private static final long serialVersionUID = 1L;
    private static final String USER = "User";
    private static Dimension MinimumSize = new Dimension(40, 22);
    protected DatabaseInstallListener databaseInstallListener = null;
    private DatabaseInstaller databaseInstaller = null;
    private JTextField databaseTextField = null;
    private JPasswordField passwordTextField = null;
    private JPasswordField passwordTextField2 = null;
    private JTextField portTextField = null;
    private JTextField userTextField = null;

    protected void addComponents(JPanel panel, int y, JLabel label, Component component, JButton button) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(2, 2, 2, 10);
        constraints.gridx = 0;
        constraints.weightx = 0.1;
        constraints.gridwidth = 1;
        constraints.gridy = y;
        label.setMinimumSize(MinimumSize);
        panel.add(label, constraints);

        constraints.weightx = 0.8;
        constraints.gridx = 1;
        constraints.gridy = y;
        if (button != null)
            constraints.insets = new Insets(2, 2, 2, 2);
        else
            constraints.insets = new Insets(2, 2, 2, 40);
        panel.add(component, constraints);

        if (button != null) {
            constraints.insets = new Insets(2, 2, 2, 40);
            constraints.gridx = 2;
            constraints.weightx = 0.1;
            constraints.gridwidth = 1;
            constraints.gridy = y;
            button.setMargin(new Insets(0, 5, 0, 5));
            button.setMinimumSize(MinimumSize);
            panel.add(button, constraints);
        }
    }

    protected DatabaseInstallListener createDatabaseInstallListener() {
        return new DatabaseInstallListener(this);
    }

    protected JPanel createDatabaseInstallPanel(JPanel panel, int y) {
        this.portTextField = new JTextField();
        this.portTextField.setColumns(Short.MAX_VALUE);
        this.portTextField.addKeyListener(new PortKeyAdapter());
        JLabel portLabel = new JLabel(this.getText(PORT), JLabel.RIGHT);
        portLabel.setLabelFor(this.portTextField);
        this.addComponents(panel, y++, portLabel, this.portTextField, null);

        this.databaseTextField = new JTextField();
        this.databaseTextField.setColumns(Short.MAX_VALUE);
        this.databaseTextField.addKeyListener(new MaxLengthKeyAdapter(20));
        this.databaseTextField.setEditable(false);
        JLabel databaseLabel = new JLabel(this.getText(DATABASE_NAME), JLabel.RIGHT);
        databaseLabel.setLabelFor(this.databaseTextField);
        this.addComponents(panel, y++, databaseLabel, this.databaseTextField, null);

        this.userTextField = new JTextField();
        this.userTextField.setColumns(Short.MAX_VALUE);
        this.userTextField.addKeyListener(new MaxLengthKeyAdapter(20));
        JLabel userLabel = new JLabel(this.getText(USER), JLabel.RIGHT);
        userLabel.setLabelFor(this.userTextField);
        this.addComponents(panel, y++, userLabel, this.userTextField, null);

        this.passwordTextField = new JPasswordField();
        this.passwordTextField.setColumns(Short.MAX_VALUE);
        this.passwordTextField.addKeyListener(new MaxLengthKeyAdapter(20));
        JLabel passwordLabel = new JLabel(this.getText(PASSWORD), JLabel.RIGHT);
        passwordLabel.setLabelFor(this.passwordTextField);
        this.addComponents(panel, y++, passwordLabel, this.passwordTextField, null);

        this.passwordTextField2 = new JPasswordField();
        this.passwordTextField2.setColumns(Short.MAX_VALUE);
        this.passwordTextField2.addKeyListener(new MaxLengthKeyAdapter(20));
        JLabel passwordLabel2 = new JLabel(this.getText(PASSWORD2), JLabel.RIGHT);
        passwordLabel.setLabelFor(this.passwordTextField2);
        this.addComponents(panel, y++, passwordLabel2, this.passwordTextField2, null);

        this.addGridBagSpace(panel, y);
        return panel;
    }

    public DatabaseInstaller getDatabaseInstaller() {
        return this.databaseInstaller;
    }

    public void setDatabaseInstaller(DatabaseInstaller databaseInstaller) {
        this.databaseInstaller = databaseInstaller;
    }

    protected void initDatabaseInstall() {
        if (this.databaseInstallListener != null)
            this.databaseInstallListener.setEnabled(false);

        this.update();

        if (this.databaseInstallListener != null)
            this.databaseInstallListener.setEnabled(true);
    }

    @Override
    public void initialize() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel panel = new JPanel(new GridBagLayout());
        this.add(this.createDatabaseInstallPanel(panel, 1));
        this.initDatabaseInstall();

        this.databaseInstallListener = this.createDatabaseInstallListener();
        this.databaseInstallListener.setDatabaseInstaller(this.getDatabaseInstaller());
        this.databaseInstallListener.setDatabaseTextField(this.databaseTextField);
        this.databaseInstallListener.setPasswordTextField(this.passwordTextField);
        this.databaseInstallListener.setPortTextField(this.portTextField);
        this.databaseInstallListener.setUserTextField(this.userTextField);
        this.databaseInstallListener.initialize();
    }

    public void update() {
        DatabaseInstaller databaseInstaller = this.getDatabaseInstaller();
        this.databaseTextField.setText(databaseInstaller.getDatabaseName());
        this.passwordTextField.setText(databaseInstaller.getPassword());
        this.portTextField.setText(databaseInstaller.getPort().toString());
        this.userTextField.setText(databaseInstaller.getUser());
    }

    @Override
    public boolean validateData() {
        if (!ValidateUtils.validatePort(this, this.portTextField, PORT))
            return false;
        if (!ValidateUtils.validatePortValid(this, this.portTextField, PORT))
            return false;
        if (!ValidateUtils.validateDatabaseName(this, this.databaseTextField, DATABASE_NAME))
            return false;
        if (!ValidateUtils.validateUser(this, this.userTextField, USER))
            return false;
        if (!ValidateUtils.validatePassword(this, this.passwordTextField, PASSWORD))
            return false;
        return ValidateUtils.validatePassword2(this, this.passwordTextField, this.passwordTextField2, "Password2NotEquals");

    }
}

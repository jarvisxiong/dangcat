package org.dangcat.install.database.swing;

import org.apache.log4j.Logger;
import org.dangcat.commons.database.Database;
import org.dangcat.commons.utils.ImageUtils;
import org.dangcat.install.swing.ConfigPanel;
import org.dangcat.install.swing.panel.ValidateUtils;
import org.dangcat.swing.keyadapter.MaxLengthKeyAdapter;
import org.dangcat.swing.keyadapter.PortKeyAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConfigPanel extends ConfigPanel {
    protected static final Logger logger = Logger.getLogger(DatabaseConfigPanel.class);
    private static final String DATABASE_NAME = "DatabaseName";
    private static final String PASSWORD = "Password";
    private static final String PORT = "Port";
    private static final long serialVersionUID = 1L;
    private static final String SERVER = "Server";
    private static final String USER = "User";
    private static Dimension MinimumSize = new Dimension(40, 22);
    private Database database = null;
    private DatabaseConfigListener databaseConfigListener = null;
    private JTextField databaseTextField = null;
    private JPasswordField passwordTextField = null;
    private JTextField portTextField = null;
    private JTextField serverTextField = null;
    private JTextField userTextField = null;

    private void addComponents(JPanel panel, int y, JLabel label, Component component) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(2, 2, 2, 10);
        constraints.weightx = 0.1;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridy = y;
        label.setMinimumSize(MinimumSize);
        panel.add(label, constraints);

        constraints.insets = new Insets(2, 2, 2, 40);
        constraints.weightx = 0.8;
        constraints.gridx = 1;
        constraints.gridy = y;
        panel.add(component, constraints);
    }

    private void checkDatabaseConnection() {
        if (!this.validateData())
            return;

        Connection connection = null;
        try {
            Class.forName(this.database.getDriver());
            connection = DriverManager.getConnection(this.database.getUrl(), this.database.getUser(), this.database.getPassword());
            connection.getMetaData();
            JOptionPane.showMessageDialog(this, this.getText("ConnectionSuccess"));
        } catch (Exception e) {
            logger.error(this, e);
            JOptionPane.showMessageDialog(this, this.getText("ConnectionError") + e.getMessage());
        }
    }

    private JPanel createDatabaseConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, 160));

        int y = 1;
        this.serverTextField = new JTextField();
        this.serverTextField.setColumns(Short.MAX_VALUE);
        this.serverTextField.addKeyListener(new MaxLengthKeyAdapter(32));
        JLabel serverLabel = new JLabel(this.getText(SERVER), JLabel.RIGHT);
        serverLabel.setLabelFor(this.serverTextField);
        this.addComponents(panel, y++, serverLabel, this.serverTextField);

        this.portTextField = new JTextField();
        this.portTextField.setColumns(Short.MAX_VALUE);
        this.portTextField.addKeyListener(new PortKeyAdapter());
        JLabel portLabel = new JLabel(this.getText(PORT), JLabel.RIGHT);
        portLabel.setLabelFor(this.portTextField);
        this.addComponents(panel, y++, portLabel, this.portTextField);

        this.databaseTextField = new JTextField();
        this.databaseTextField.setColumns(Short.MAX_VALUE);
        this.databaseTextField.addKeyListener(new MaxLengthKeyAdapter(20));
        this.databaseTextField.setEditable(false);
        JLabel databaseLabel = new JLabel(this.getText(DATABASE_NAME), JLabel.RIGHT);
        databaseLabel.setLabelFor(this.databaseTextField);
        this.addComponents(panel, y++, databaseLabel, this.databaseTextField);

        this.userTextField = new JTextField();
        this.userTextField.setColumns(Short.MAX_VALUE);
        this.userTextField.addKeyListener(new MaxLengthKeyAdapter(20));
        JLabel userLabel = new JLabel(this.getText(USER), JLabel.RIGHT);
        userLabel.setLabelFor(this.userTextField);
        this.addComponents(panel, y++, userLabel, this.userTextField);

        this.passwordTextField = new JPasswordField();
        this.passwordTextField.setColumns(Short.MAX_VALUE);
        this.passwordTextField.addKeyListener(new MaxLengthKeyAdapter(20));
        JLabel passwordLabel = new JLabel(this.getText(PASSWORD), JLabel.RIGHT);
        passwordLabel.setLabelFor(this.passwordTextField);
        this.addComponents(panel, y++, passwordLabel, this.passwordTextField);

        this.createDatabaseTestButton(panel, y++);
        return panel;
    }

    private void createDatabaseTestButton(JPanel panel, int y) {
        ImageIcon testImage = ImageUtils.loadImageIcon(this.getClass(), "test.gif");
        JButton testButton = new JButton(this.getText("TestConnection"), testImage);
        testButton.setMargin(new Insets(0, 20, 0, 20));
        testButton.setMaximumSize(new Dimension(200, 0));
        testButton.setAlignmentX(CENTER_ALIGNMENT);
        testButton.setMnemonic(KeyEvent.VK_T);
        testButton.setDisplayedMnemonicIndex(testButton.getText().indexOf("T"));
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DatabaseConfigPanel.this.checkDatabaseConnection();
            }
        });
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(2, 2, 2, 40);
        constraints.weightx = 0.2;
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        constraints.gridy = y;
        constraints.anchor = GridBagConstraints.LINE_END;
        panel.add(testButton, constraints);
    }

    public Database getDatabase() {
        return this.database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    @Override
    public void initialize() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(this.createDatabaseConfigPanel());
        this.update();

        this.databaseConfigListener = new DatabaseConfigListener(this);
        this.databaseConfigListener.setDatabase(this.database);
        this.databaseConfigListener.setDatabaseTextField(this.databaseTextField);
        this.databaseConfigListener.setPasswordTextField(this.passwordTextField);
        this.databaseConfigListener.setPortTextField(this.portTextField);
        this.databaseConfigListener.setServerTextField(this.serverTextField);
        this.databaseConfigListener.setUserTextField(this.userTextField);
        this.databaseConfigListener.initialize();
    }

    public void update() {
        if (this.databaseConfigListener != null)
            this.databaseConfigListener.setEnabled(false);

        Database database = this.getDatabase();
        this.databaseTextField.setText(database.getName());
        this.passwordTextField.setText(database.getPassword());
        this.portTextField.setText(database.getPort().toString());
        this.serverTextField.setText(database.getServer());
        this.userTextField.setText(database.getUser());

        if (this.databaseConfigListener != null)
            this.databaseConfigListener.setEnabled(true);
    }

    @Override
    public boolean validateData() {
        if (!ValidateUtils.validateServer(this, this.serverTextField, SERVER))
            return false;
        if (!ValidateUtils.validatePort(this, this.portTextField, PORT))
            return false;
        if (!ValidateUtils.validateDatabaseName(this, this.databaseTextField, DATABASE_NAME))
            return false;
        if (!ValidateUtils.validateUser(this, this.userTextField, USER))
            return false;
        return ValidateUtils.validatePassword(this, this.passwordTextField, PASSWORD);

    }
}

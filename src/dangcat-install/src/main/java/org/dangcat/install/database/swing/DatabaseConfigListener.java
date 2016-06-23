package org.dangcat.install.database.swing;

import org.dangcat.commons.database.Database;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.install.swing.ConfigPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DatabaseConfigListener implements DocumentListener
{
    private ConfigPanel configPanel = null;
    private Database database = null;
    private JTextField databaseTextField = null;
    private boolean enabled = true;
    private JPasswordField passwordTextField = null;
    private JTextField portTextField = null;
    private JTextField serverTextField = null;
    private JTextField userTextField = null;

    public DatabaseConfigListener(ConfigPanel configPanel)
    {
        this.configPanel = configPanel;
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
        this.changedValue();
    }

    public void changedValue()
    {
        if (this.isEnabled())
        {
            Database database = this.getDatabase();
            database.setName(this.databaseTextField.getText());
            database.setPassword(new String(this.passwordTextField.getPassword()));
            database.setPort(ValueUtils.parseInt(this.portTextField.getText()));
            database.setServer(this.serverTextField.getText());
            database.setUser(this.userTextField.getText());
            this.configPanel.setChanged(true);
        }
    }

    public Database getDatabase()
    {
        return this.database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public JTextField getDatabaseTextField()
    {
        return this.databaseTextField;
    }

    public void setDatabaseTextField(JTextField databaseTextField) {
        this.databaseTextField = databaseTextField;
    }

    public JPasswordField getPasswordTextField()
    {
        return this.passwordTextField;
    }

    public void setPasswordTextField(JPasswordField passwordTextField) {
        this.passwordTextField = passwordTextField;
    }

    public JTextField getPortTextField()
    {
        return this.portTextField;
    }

    public void setPortTextField(JTextField portTextField) {
        this.portTextField = portTextField;
    }

    public JTextField getServerTextField()
    {
        return this.serverTextField;
    }

    public void setServerTextField(JTextField serverTextField) {
        this.serverTextField = serverTextField;
    }

    public JTextField getUserTextField()
    {
        return this.userTextField;
    }

    public void setUserTextField(JTextField userTextField) {
        this.userTextField = userTextField;
    }

    public void initialize()
    {
        this.databaseTextField.getDocument().addDocumentListener(this);
        this.passwordTextField.getDocument().addDocumentListener(this);
        this.portTextField.getDocument().addDocumentListener(this);
        this.serverTextField.getDocument().addDocumentListener(this);
        this.userTextField.getDocument().addDocumentListener(this);
    }

    @Override
    public void insertUpdate(DocumentEvent e)
    {
        this.changedValue();
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
        this.changedValue();
    }
}

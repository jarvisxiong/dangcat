package org.dangcat.install.database.swing;

import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.install.database.DatabaseInstaller;
import org.dangcat.install.swing.ConfigPanel;

public class DatabaseInstallListener implements DocumentListener
{
    private ConfigPanel configPanel = null;
    private DatabaseInstaller databaseInstaller = null;
    private JTextField databaseTextField = null;
    private boolean enabled = true;
    private JPasswordField passwordTextField = null;
    private JTextField portTextField = null;
    private JTextField userTextField = null;

    public DatabaseInstallListener(ConfigPanel configPanel)
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
            DatabaseInstaller databaseInstaller = this.getDatabaseInstaller();
            databaseInstaller.setDatabaseName(this.databaseTextField.getText());
            databaseInstaller.setPassword(new String(this.passwordTextField.getPassword()));
            Integer port = ValueUtils.parseInt(this.portTextField.getText());
            if (port != null)
                databaseInstaller.setPort(port);
            databaseInstaller.setUser(this.userTextField.getText());
            this.configPanel.setChanged(true);
        }
    }

    public DatabaseInstaller getDatabaseInstaller()
    {
        return this.databaseInstaller;
    }

    public JTextField getDatabaseTextField()
    {
        return this.databaseTextField;
    }

    public JPasswordField getPasswordTextField()
    {
        return this.passwordTextField;
    }

    public JTextField getPortTextField()
    {
        return this.portTextField;
    }

    public JTextField getUserTextField()
    {
        return this.userTextField;
    }

    public void initialize()
    {
        this.databaseTextField.getDocument().addDocumentListener(this);
        this.passwordTextField.getDocument().addDocumentListener(this);
        this.portTextField.getDocument().addDocumentListener(this);
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

    @Override
    public void removeUpdate(DocumentEvent e)
    {
        this.changedValue();
    }

    public void setDatabaseInstaller(DatabaseInstaller databaseInstaller)
    {
        this.databaseInstaller = databaseInstaller;
    }

    public void setDatabaseTextField(JTextField databaseTextField)
    {
        this.databaseTextField = databaseTextField;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void setPasswordTextField(JPasswordField passwordTextField)
    {
        this.passwordTextField = passwordTextField;
    }

    public void setPortTextField(JTextField portTextField)
    {
        this.portTextField = portTextField;
    }

    public void setUserTextField(JTextField userTextField)
    {
        this.userTextField = userTextField;
    }
}

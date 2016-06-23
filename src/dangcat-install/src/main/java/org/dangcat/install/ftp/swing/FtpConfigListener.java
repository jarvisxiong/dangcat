package org.dangcat.install.ftp.swing;

import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.install.ftp.FtpParameter;
import org.dangcat.install.swing.ConfigPanel;

public class FtpConfigListener implements DocumentListener
{
    private ConfigPanel configPanel = null;
    private boolean enabled = true;
    private FtpParameter ftpParameter = null;
    private JTextField initpathTextField = null;
    private JPasswordField passwordTextField = null;
    private JTextField portTextField = null;
    private JTextField serverTextField = null;
    private JTextField usernameTextField = null;

    public FtpConfigListener(ConfigPanel configPanel)
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
            FtpParameter ftpParameter = this.getFtpParamer();
            ftpParameter.setInitPath(this.initpathTextField.getText());
            ftpParameter.setPassword(new String(this.passwordTextField.getPassword()));
            ftpParameter.setPort(ValueUtils.parseInt(this.portTextField.getText()));
            ftpParameter.setServer(this.serverTextField.getText());
            ftpParameter.setUser(this.usernameTextField.getText());
            this.configPanel.setChanged(true);
        }
    }

    public JTextField getDatabaseTextField()
    {
        return this.initpathTextField;
    }

    public FtpParameter getFtpParamer()
    {
        return this.ftpParameter;
    }

    public JPasswordField getPasswordTextField()
    {
        return this.passwordTextField;
    }

    public JTextField getPortTextField()
    {
        return this.portTextField;
    }

    public JTextField getServerTextField()
    {
        return this.serverTextField;
    }

    public JTextField getUserTextField()
    {
        return this.usernameTextField;
    }

    public void initialize()
    {
        this.initpathTextField.getDocument().addDocumentListener(this);
        this.passwordTextField.getDocument().addDocumentListener(this);
        this.portTextField.getDocument().addDocumentListener(this);
        this.serverTextField.getDocument().addDocumentListener(this);
        this.usernameTextField.getDocument().addDocumentListener(this);
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

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void setFtpParameter(FtpParameter ftpParameter)
    {
        this.ftpParameter = ftpParameter;
    }

    public void setInitPathTextField(JTextField databaseTextField)
    {
        this.initpathTextField = databaseTextField;
    }

    public void setPasswordTextField(JPasswordField passwordTextField)
    {
        this.passwordTextField = passwordTextField;
    }

    public void setPortTextField(JTextField portTextField)
    {
        this.portTextField = portTextField;
    }

    public void setServerTextField(JTextField serverTextField)
    {
        this.serverTextField = serverTextField;
    }

    public void setUserTextField(JTextField userTextField)
    {
        this.usernameTextField = userTextField;
    }
}

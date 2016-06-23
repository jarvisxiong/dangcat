package org.dangcat.install.database.swing;

import javax.swing.JTextField;

import org.dangcat.install.database.mysql.MySqlInstaller;
import org.dangcat.install.swing.ConfigPanel;


public class MySqlInstallListener extends DatabaseInstallListener
{
    private JTextField baseDirField = null;
    private JTextField dataDirField = null;

    public MySqlInstallListener(ConfigPanel configPanel)
    {
        super(configPanel);
    }

    @Override
    public void changedValue()
    {
        super.changedValue();
        if (this.isEnabled())
        {
            MySqlInstaller mySqlInstaller = (MySqlInstaller) this.getDatabaseInstaller();
            mySqlInstaller.setBaseDir(this.baseDirField.getText());
            mySqlInstaller.setDataDir(this.dataDirField.getText());
        }
    }

    public JTextField getBaseDirField()
    {
        return this.baseDirField;
    }

    public JTextField getDataDirField()
    {
        return this.dataDirField;
    }

    @Override
    public void initialize()
    {
        super.initialize();
        this.baseDirField.getDocument().addDocumentListener(this);
        this.dataDirField.getDocument().addDocumentListener(this);
    }

    public void setBaseDirField(JTextField baseDirField)
    {
        this.baseDirField = baseDirField;
    }

    public void setDataDirField(JTextField dataDirField)
    {
        this.dataDirField = dataDirField;
    }
}

package org.dangcat.install.database.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.dangcat.commons.utils.ImageUtils;
import org.dangcat.install.database.mysql.MySqlInstaller;
import org.dangcat.install.swing.panel.ValidateUtils;

public class MySqlInstallPanel extends DatabaseInstallPanel
{
    private static final String BASE_DIR = "BaseDir";
    private static final String DATA_DIR = "DataDir";
    private static final long serialVersionUID = 1L;

    private JTextField baseDirField = null;
    private JTextField dataDirField = null;

    @Override
    protected DatabaseInstallListener createDatabaseInstallListener()
    {
        MySqlInstallListener mySqlInstallListener = new MySqlInstallListener(this);
        mySqlInstallListener.setBaseDirField(this.baseDirField);
        mySqlInstallListener.setDataDirField(this.dataDirField);
        return mySqlInstallListener;
    }

    @Override
    protected JPanel createDatabaseInstallPanel(JPanel panel, int y)
    {
        ImageIcon browseImage = ImageUtils.loadImageIcon(this.getClass(), "browse.png");
        this.baseDirField = new JTextField();
        this.baseDirField.setColumns(Short.MAX_VALUE);
        JLabel baseDirLabel = new JLabel(this.getText(BASE_DIR), JLabel.RIGHT);
        baseDirLabel.setLabelFor(this.baseDirField);
        JButton baseDirButton = new JButton(this.getText("Browse") + "(B)", browseImage);
        baseDirButton.setAlignmentX(CENTER_ALIGNMENT);
        baseDirButton.setMnemonic(KeyEvent.VK_B);
        baseDirButton.setDisplayedMnemonicIndex(baseDirButton.getText().lastIndexOf("B"));
        baseDirButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                MySqlInstallPanel.this.choicePath(MySqlInstallPanel.this.baseDirField);
            }
        });
        this.addComponents(panel, y++, baseDirLabel, this.baseDirField, baseDirButton);

        this.dataDirField = new JTextField();
        this.dataDirField.setColumns(Short.MAX_VALUE);
        JLabel dataDirLabel = new JLabel(this.getText(DATA_DIR), JLabel.RIGHT);
        dataDirLabel.setLabelFor(this.dataDirField);
        JButton dataDirButton = new JButton(this.getText("Browse") + "(L)", browseImage);
        dataDirButton.setAlignmentX(CENTER_ALIGNMENT);
        dataDirButton.setMnemonic(KeyEvent.VK_L);
        dataDirButton.setDisplayedMnemonicIndex(dataDirButton.getText().lastIndexOf("L"));
        dataDirButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                MySqlInstallPanel.this.choicePath(MySqlInstallPanel.this.dataDirField);
            }
        });
        this.addComponents(panel, y++, dataDirLabel, this.dataDirField, dataDirButton);
        super.createDatabaseInstallPanel(panel, y);
        return panel;
    }

    @Override
    public void update()
    {
        super.update();
        MySqlInstaller mySqlInstaller = (MySqlInstaller) this.getDatabaseInstaller();
        this.baseDirField.setText(mySqlInstaller.getBaseDir());
        this.dataDirField.setText(mySqlInstaller.getDataDir());
    }

    @Override
    public boolean validateData()
    {
        if (!ValidateUtils.validateBaseDir(this, this.baseDirField, BASE_DIR))
            return false;
        if (!ValidateUtils.validateInstallPath(this, this.baseDirField, BASE_DIR))
            return false;
        if (!ValidateUtils.validateDataDir(this, this.dataDirField, DATA_DIR))
            return false;
        if (!ValidateUtils.validateInstallPath(this, this.dataDirField, DATA_DIR))
            return false;

        return super.validateData();
    }
}
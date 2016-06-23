package org.dangcat.install.frame;

import org.dangcat.install.database.module.DatabaseConfigModule;
import org.dangcat.install.ftp.module.FtpConfigModule;
import org.dangcat.install.swing.ToolBarPanel;
import org.dangcat.install.swing.event.ValueChangedListener;
import org.dangcat.install.swing.toolbar.ConfigToolBar;
import org.dangcat.install.task.ConfigFileAccess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.EventObject;


public abstract class ConfigFrameBase extends FrameBase implements ValueChangedListener
{
    private static final long serialVersionUID = 1L;
    private ConfigFileAccess configFileAccess = new ConfigFileAccess();

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        if (actionEvent.getActionCommand().equals(ConfigToolBar.SAVE))
        {
            if (this.validateData())
                this.save();
            return;
        }
        super.actionPerformed(actionEvent);
    }

    @Override
    protected void cancel()
    {
        this.load();
        super.cancel();
    }

    @Override
    protected Container createContentPane()
    {
        Container container = super.createContentPane();
        this.getBodyContainer().addValueChangedListener(this);
        return container;
    }

    protected void createDatabaseConfigModule(String name, Integer defaultDatabasePort)
    {
        String title = this.getText(name);
        DatabaseConfigModule databaseConfigModule = new DatabaseConfigModule("database." + name, title);
        databaseConfigModule.setDefaultDatabaseName(name);
        databaseConfigModule.setDefaultPort(defaultDatabasePort);
        databaseConfigModule.createDatabaseConfigAccess(name, title);
        databaseConfigModule.initialize();
        this.addProcessModule(databaseConfigModule);
        this.getConfigFileAccess().addConfigureAccess(databaseConfigModule.getDatabaseConfigAccess());
    }

    protected void createFtpConfigModule(String name, String title)
    {
        FtpConfigModule ftpConfigModule = new FtpConfigModule("ftp." + name, title);
        ftpConfigModule.createFtpConfigAccess(name, title);
        ftpConfigModule.setDefaultFtpName(name);
        ftpConfigModule.initialize();
        this.getConfigFileAccess().addConfigureAccess(ftpConfigModule.getFtpConfigAccess());
        this.addProcessModule(ftpConfigModule);
    }

    @Override
    protected ToolBarPanel createToolBarContainer()
    {
        return new ConfigToolBar();
    }

    @Override
    protected void executeFinished()
    {
        String key = null;
        if (this.isSuccessFull())
            key = "Config.Successfull";
        else
            key = "Config.Error";
        String message = this.getText(key);
        JOptionPane.showMessageDialog(this, message);
        super.executeFinished();
    }

    protected abstract File getConfigFile();

    public ConfigFileAccess getConfigFileAccess()
    {
        return this.configFileAccess;
    }

    @Override
    public void initialize()
    {
        this.configFileAccess.setConfigFile(this.getConfigFile());
        this.configFileAccess.load();
    }

    private void load()
    {
        this.configFileAccess.load();
        this.getToolBarContainer().removeState(ToolBarPanel.PROCESS);
        this.getToolBarContainer().removeState(ConfigToolBar.CHANGED);
    }

    @Override
    public void onValueChanged(EventObject eventObject)
    {
        this.getToolBarContainer().addState(ConfigToolBar.CHANGED, true);
    }

    @Override
    public void pack()
    {
        super.pack();
        this.load();
    }

    protected void save()
    {
        this.logger.info("Save config data.");
        try
        {
            this.configFileAccess.save();
            super.executeProcess();
            this.load();
        }
        catch (Exception e)
        {
            this.logger.error("Save the resource file error", e);
        }
    }
}

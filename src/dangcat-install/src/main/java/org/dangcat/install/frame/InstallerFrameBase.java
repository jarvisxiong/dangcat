package org.dangcat.install.frame;

import org.dangcat.commons.resource.ResourceUtils;
import org.dangcat.install.Installer;
import org.dangcat.install.database.module.DatabaseConfigModule;
import org.dangcat.install.database.module.DatabaseInstallModule;
import org.dangcat.install.database.module.MySqlInstallModule;
import org.dangcat.install.ftp.module.FtpConfigModule;
import org.dangcat.install.module.InstallModuleBase;
import org.dangcat.install.module.ProcessModule;
import org.dangcat.install.module.ProcessModuleBase;
import org.dangcat.install.module.ServiceInstallModule;
import org.dangcat.install.service.ServiceInstaller;
import org.dangcat.install.swing.panel.InstallSelectPanel;
import org.dangcat.install.swing.panel.InstallSelectPathPanel;
import org.dangcat.install.task.ConfigFileAccess;

import javax.swing.*;
import java.io.File;
import java.util.Collection;

public abstract class InstallerFrameBase extends InstallerFrame
{
    private static final long serialVersionUID = 1L;

    private ConfigFileAccess configFileAccess = new ConfigFileAccess();
    private DatabaseConfigModule databaseConfigModule = null;
    private DatabaseInstallModule databaseInstallModule = null;
    private ServiceInstallModule serviceInstallModule = null;

    protected boolean checkExists()
    {
        if (this.checkExists(this.serviceInstallModule, this.serviceInstallModule.getServiceInstaller()))
            return true;
        return this.checkExists(this.databaseInstallModule, this.databaseInstallModule.getDatabaseInstaller());
    }

    protected boolean checkExists(ProcessModuleBase processModuleBase, Installer installer)
    {
        if (processModuleBase.isEnabled())
        {
            if (installer.exists())
            {
                String message = ResourceUtils.getText(ServiceInstaller.class, "service.exists", processModuleBase.getName());
                JOptionPane.showMessageDialog(this, message);
                return true;
            }
        }
        return false;
    }

    private void createDatabaseConfigModule()
    {
        String name = this.getDatabaseServiceName() + "-config";
        String title = this.getDatabaseTitle();

        DatabaseConfigModule databaseConfigModule = new DatabaseConfigModule(name, title);
        databaseConfigModule.setDefaultDatabaseName(this.getDatabaseName());
        databaseConfigModule.setDefaultPort(this.getDefaultDatabasePort());
        databaseConfigModule.createDatabaseConfigAccess(name, title);
        databaseConfigModule.initialize();
        this.configFileAccess.addConfigureAccess(databaseConfigModule.getDatabaseConfigAccess());

        this.addProcessModule(databaseConfigModule);
        this.databaseConfigModule = databaseConfigModule;
    }

    private void createDatabaseInstallModule()
    {
        String name = this.getDatabaseServiceName();
        String title = this.getDatabaseTitle();

        MySqlInstallModule databaseInstallModule = new MySqlInstallModule(name, title);
        databaseInstallModule.setDefaultDatabaseName(this.getDatabaseName());
        databaseInstallModule.setDisplayName(this.getDatabaseServiceDisplayName());
        databaseInstallModule.setDefaultPort(this.getDefaultDatabasePort());
        databaseInstallModule.setCurrentPath(this.getCurrentPath());
        databaseInstallModule.initialize();
        this.addProcessModule(databaseInstallModule);
        this.databaseInstallModule = databaseInstallModule;

        databaseInstallModule.createDatabaseAccess(name, title);
        this.configFileAccess.addConfigureAccess(databaseInstallModule.getDatabaseAccess());
    }

    protected FtpConfigModule createFtpConfigModule(String name, String title)
    {
        FtpConfigModule ftpConfigModule = new FtpConfigModule("ftp." + name, title);
        ftpConfigModule.createFtpConfigAccess(name, title);
        ftpConfigModule.setDefaultFtpName(name);
        ftpConfigModule.initialize();
        this.getConfigFileAccess().addConfigureAccess(ftpConfigModule.getFtpConfigAccess());
        this.addProcessModule(ftpConfigModule);
        return ftpConfigModule;
    }

    @Override
    protected InstallSelectPanel createInstallSelectPanel()
    {
        InstallSelectPanel installSelectPanel = super.createInstallSelectPanel();
        installSelectPanel.addComponent(this.serviceInstallModule.getName(), this.serviceInstallModule.getTitle(), true);
        installSelectPanel.addComponent(this.databaseInstallModule.getName(), this.databaseInstallModule.getTitle(), true);
        return installSelectPanel;
    }

    private void createServiceInstallModule()
    {
        String name = this.getServiceName();
        String title = this.getText(name);

        ServiceInstallModule serviceInstallModule = new ServiceInstallModule(name, title);
        serviceInstallModule.setCurrentPath(this.getCurrentPath());
        serviceInstallModule.addConfigFileAccess(this.configFileAccess);
        serviceInstallModule.initialize();
        this.addProcessModule(serviceInstallModule);
        this.serviceInstallModule = serviceInstallModule;
    }

    @Override
    protected void executeInstall()
    {
        if (this.checkExists())
            return;

        InstallSelectPathPanel installSelectPanel = (InstallSelectPathPanel) this.getInstallSelectPanel();
        if (this.serviceInstallModule.isEnabled())
        {
            this.serviceInstallModule.setTargetDir(installSelectPanel.getInstallPathPanel().getInstallPath());
            String configFile = this.serviceInstallModule.getInstallShellDir() + File.separator + "conf" + File.separator + this.getConfigFileName();
            this.configFileAccess.setConfigFile(new File(configFile));
        }

        Collection<ProcessModule> processModules = this.getProcessModules();
        if (processModules != null && !processModules.isEmpty())
        {
            for (ProcessModule processModule : processModules)
            {
                if (processModule instanceof InstallModuleBase)
                {
                    InstallModuleBase installModuleBase = (InstallModuleBase) processModule;
                    installModuleBase.setTargetDir(installSelectPanel.getInstallPathPanel().getInstallPath());
                }
            }
        }

        super.executeProcess();
    }

    public ConfigFileAccess getConfigFileAccess()
    {
        return this.configFileAccess;
    }

    protected abstract String getConfigFileName();

    protected abstract String getDatabaseName();

    protected String getDatabaseServiceDisplayName()
    {
        return null;
    }

    protected abstract String getDatabaseServiceName();

    protected String getDatabaseTitle()
    {
        return this.getText(this.getDatabaseServiceName());
    }

    protected abstract Integer getDefaultDatabasePort();

    protected abstract String getServiceName();

    @Override
    public void initialize()
    {
        this.createDatabaseInstallModule();
        this.createDatabaseConfigModule();
        this.createServiceInstallModule();
    }

    @Override
    protected void updateBodyContent()
    {
        Collection<String> selectedValues = this.getInstallSelectPanel().getSelectedValues();
        boolean isInstallService = selectedValues.contains(this.getServiceName());
        this.serviceInstallModule.setEnabled(isInstallService);

        boolean isInstallDatabase = selectedValues.contains(this.getDatabaseServiceName());
        this.databaseInstallModule.setEnabled(isInstallDatabase);
        this.databaseConfigModule.setEnabled(!isInstallDatabase);

        super.updateBodyContent();
    }
}

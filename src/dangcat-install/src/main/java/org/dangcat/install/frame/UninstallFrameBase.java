package org.dangcat.install.frame;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collection;

import javax.swing.JOptionPane;

import org.dangcat.commons.os.service.SystemServiceUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.install.database.module.MySqlUninstallModule;
import org.dangcat.install.module.ProcessModuleBase;
import org.dangcat.install.module.ServiceUninstallModule;
import org.dangcat.install.swing.ToolBarPanel;
import org.dangcat.install.swing.panel.InstallSelectPanel;
import org.dangcat.install.swing.toolbar.UninstallToolBar;

public abstract class UninstallFrameBase extends InstallProcessFrame
{
    private static final long serialVersionUID = 1L;
    private MySqlUninstallModule databaseUninstallModule = null;
    private ServiceUninstallModule serviceUninstallModule = null;

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        if (actionEvent.getActionCommand().equals(UninstallToolBar.UNINSTALL))
        {
        	this.updateBodyContent();
            if (this.getInstallSelectPanel().validateData())
            {
                this.getBodyContainer().last();
                this.uninstall();
            }
        }
        super.actionPerformed(actionEvent);
    }

    private void createDatabaseUninstallModule()
    {
        String name = this.getDatabaseServiceName();
        String title = this.getText(name);

        MySqlUninstallModule databaseUninstallModule = new MySqlUninstallModule(name, title);
        databaseUninstallModule.setDisplayName(this.getDatabaseServiceDisplayName());
        databaseUninstallModule.initialize();
        this.addProcessModule(databaseUninstallModule);
        this.databaseUninstallModule = databaseUninstallModule;
    }

    @Override
    protected InstallSelectPanel createInstallSelectPanel()
    {
        InstallSelectPanel installSelectPanel = super.createInstallSelectPanel();
        this.createInstallSelectPanel(installSelectPanel, this.serviceUninstallModule);
        this.createInstallSelectPanel(installSelectPanel, this.databaseUninstallModule);
        return installSelectPanel;
    }

    private void createInstallSelectPanel(InstallSelectPanel installSelectPanel, ProcessModuleBase processModule)
    {
        boolean enabled = this.exists(processModule.getName(), processModule.getDisplayName());
        if (enabled)
            installSelectPanel.addComponent(processModule.getName(), processModule.getTitle(), true);
        processModule.setEnabled(enabled);
    }

    private void createServiceUninstallModule()
    {
        String name = this.getServiceName();
        String title = this.getText(name);
        ServiceUninstallModule serviceUninstallModule = new ServiceUninstallModule(name, title);
        serviceUninstallModule.initialize();
        this.addProcessModule(serviceUninstallModule);
        this.serviceUninstallModule = serviceUninstallModule;
    }

    @Override
    protected ToolBarPanel createToolBarContainer()
    {
        return new UninstallToolBar();
    }

    @Override
    protected void executeFinished()
    {
        String key = null;
        if (this.isSuccessFull() && !this.exists(this.serviceUninstallModule.getName(), this.serviceUninstallModule.getDisplayName()))
        {
            this.getToolBarContainer().addState(ToolBarPanel.FINISHED, true);
            key = "Uninstall.Successfull";
        }
        else
        {
            this.getToolBarContainer().removeState(ToolBarPanel.FINISHED);
            key = "Uninstall.Error";
        }
        String message = this.getText(key);
        JOptionPane.showMessageDialog(this, message);
        super.executeFinished();
    }

    private boolean exists(String serviceName, String serviceDisplayName)
    {
        File servicePath = this.getServicePath(serviceName);
        if (!servicePath.isDirectory() || !servicePath.exists())
            return false;
        if (!ValueUtils.isEmpty(serviceDisplayName))
            return SystemServiceUtils.exists(serviceDisplayName);
        return SystemServiceUtils.exists(serviceName);
    }

    protected String getDatabaseServiceDisplayName()
    {
        return null;
    }

    protected abstract String getDatabaseServiceName();

    protected abstract String getServiceName();

    private File getServicePath(String serviceName)
    {
        return new File(this.getCurrentPath().getAbsolutePath() + File.separator + serviceName);
    }

    @Override
    public void initialize()
    {
        this.createServiceUninstallModule();
        this.createDatabaseUninstallModule();
    }

    protected void uninstall()
    {
        File baseDir = this.getServicePath(this.databaseUninstallModule.getName());
        this.databaseUninstallModule.setCurrentPath(baseDir);

        File homeDir = this.getServicePath(this.serviceUninstallModule.getName());
        this.serviceUninstallModule.setCurrentPath(homeDir);

        this.logger.info("Uninstall process begin execute.");
        super.executeProcess();
    }
    
    @Override
    protected void updateBodyContent()
    {
        Collection<String> selectedValues = this.getInstallSelectPanel().getSelectedValues();
        boolean isInstallService = selectedValues.contains(this.getServiceName());
        this.serviceUninstallModule.setEnabled(isInstallService);
        boolean isInstallDatabase = selectedValues.contains(this.getDatabaseServiceName());
        this.databaseUninstallModule.setEnabled(isInstallDatabase);

        super.updateBodyContent();
    }
}

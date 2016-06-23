package org.dangcat.install.module;

import org.dangcat.install.service.ServiceInstaller;
import org.dangcat.install.task.ServiceUninstallTask;


public class ServiceUninstallModule extends ProcessModuleBase
{
    private ServiceInstaller serviceUninstaller = null;

    public ServiceUninstallModule(String name, String title)
    {
        super(name, title);
    }

    public ServiceInstaller getServiceInstaller()
    {
        return this.serviceUninstaller;
    }

    @Override
    public void initialize()
    {
        ServiceUninstallTask serviceUninstallTask = new ServiceUninstallTask(this.getName());
        this.addProcessTask(serviceUninstallTask);
        this.serviceUninstaller = serviceUninstallTask;
    }

    @Override
    public void prepare()
    {
        this.serviceUninstaller.setHome(this.getCurrentPath().getAbsolutePath());
    }
}

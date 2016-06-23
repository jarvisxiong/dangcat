package org.dangcat.install.module;

import org.dangcat.install.service.ServiceInstaller;
import org.dangcat.install.task.ConfigAccessTask;
import org.dangcat.install.task.ConfigFileAccess;
import org.dangcat.install.task.FileCopyTask;
import org.dangcat.install.task.ServiceInstallTask;

import java.io.File;

public class ServiceInstallModule extends InstallModuleBase
{
    private ConfigAccessTask configAccessTask = new ConfigAccessTask();
    private FileCopyTask fileCopyTask = new FileCopyTask();
    private long minNeedSpace = 1024l * 1024l * 1024l * 2l;
    private ServiceInstallTask serviceInstaller = new ServiceInstallTask(this.getName());

    public ServiceInstallModule(String name, String title)
    {
        super(name, title);
    }

    public void addConfigFileAccess(ConfigFileAccess configFileAccess)
    {
        this.configAccessTask.addConfigFileAccess(configFileAccess);
    }

    public void addCopyTask(File source, File dest)
    {
        this.fileCopyTask.addTask(source, dest);
    }

    public void clearCopyTasks()
    {
        this.fileCopyTask.clearTasks();
    }

    public long getMinNeedSpace()
    {
        return this.minNeedSpace;
    }

    public void setMinNeedSpace(long minNeedSpace) {
        this.minNeedSpace = minNeedSpace;
    }

    public ServiceInstaller getServiceInstaller()
    {
        return this.serviceInstaller;
    }

    @Override
    public void initialize()
    {
        this.getServiceInstaller().setServiceName(this.getName());
        this.getServiceInstaller().setServiceDisplayName(this.getDisplayName());

        this.addProcessTask(this.fileCopyTask);
        this.addProcessTask(this.configAccessTask);
        this.addProcessTask(this.serviceInstaller);

        this.configAccessTask.load();
    }

    @Override
    public void prepare()
    {
        this.clearCopyTasks();
        this.serviceInstaller.setHome(this.getInstallShellDir());
        File sourceDir = new File(this.getCurrentPath().getAbsolutePath(), this.getName());
        this.addCopyTask(sourceDir, new File(this.getInstallShellDir()));
    }
}

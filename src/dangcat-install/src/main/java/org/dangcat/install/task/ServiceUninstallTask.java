package org.dangcat.install.task;

import org.apache.log4j.Logger;
import org.dangcat.install.service.ServiceInstaller;


public class ServiceUninstallTask extends ServiceInstaller implements ProcessTask
{
    private static final int TOTALSIZE = 2;
    private boolean enabled = true;
    private long finishedSize = 0;

    public ServiceUninstallTask(String serviceName)
    {
        super(serviceName);
    }

    @Override
    public void execute(Logger logger)
    {
        this.setLogger(logger);
        this.stop();
        this.finishedSize++;
        this.uninstall();
        this.finishedSize++;
    }

    @Override
    public long getFinishedSize()
    {
        return this.finishedSize;
    }

    @Override
    public long getTaskSize()
    {
        return TOTALSIZE;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}

package org.dangcat.commons.os.monitor;

import java.io.File;
import java.util.List;

abstract class OSMonitor
{
    private File currentPath = new File(".");

    protected void monitor(MonitorInfo monitorInfo)
    {
        this.monitorSpace(monitorInfo);
        this.monitorMemory(monitorInfo);
        this.monitorCPU(monitorInfo);
    }

    protected abstract void monitorCPU(MonitorInfo monitorInfo);

    protected abstract void monitorMemory(MonitorInfo monitorInfo);

    protected void monitorSpace(MonitorInfo monitorInfo)
    {
        List<File> pathList = SystemMonitor.getInstance().getMonitorDiskPathList();
        long totalDiskSpace = 0;
        long freeDiskSpace = 0;
        if (pathList.size() > 0)
        {
            synchronized (pathList)
            {
                for (File path : pathList)
                {
                    totalDiskSpace += path.getTotalSpace();
                    freeDiskSpace += path.getFreeSpace();
                }
            }
        }
        else
        {
            totalDiskSpace += this.currentPath.getTotalSpace();
            freeDiskSpace += this.currentPath.getFreeSpace();
        }
        monitorInfo.setValue(MonitorInfo.TotalDiskSpace, totalDiskSpace);
        monitorInfo.setValue(MonitorInfo.FreeDiskSpace, freeDiskSpace);
    }
}

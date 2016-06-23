package org.dangcat.commons.os.monitor;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.OSType;
import org.dangcat.commons.utils.ValueUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统资源监控。
 *
 * @author dangcat
 */
public class SystemMonitor {
    protected static final Logger logger = Logger.getLogger(SystemMonitor.class);
    private static SystemMonitor instance = new SystemMonitor();
    private static OSMonitor osMonitor = null;
    private List<File> monitorDiskPathList = new ArrayList<File>();

    private SystemMonitor() {
    }

    public static SystemMonitor getInstance() {
        return instance;
    }

    /**
     * 添加磁盘资源监控路径。
     *
     * @param path 路径对象。
     */
    public void addDiskPath(File path) {
        if (path != null && path.exists()) {
            synchronized (monitorDiskPathList) {
                if (!monitorDiskPathList.contains(path))
                    monitorDiskPathList.add(path);
            }
        }
    }

    public void addDiskPath(String path) {
        if (!ValueUtils.isEmpty(path)) {
            for (String pathName : path.split(";"))
                this.addDiskPath(new File(pathName));
        }
    }

    public List<File> getMonitorDiskPathList() {
        return monitorDiskPathList;
    }

    /**
     * 监控资源信息。
     */
    public void monitor(MonitorInfo monitorInfo) {
        if (osMonitor == null) {
            OSType osType = OSType.getOSType();
            if (OSType.Linux.equals(osType))
                osMonitor = new LinuxMonitor();
            else if (OSType.Windows.equals(osType))
                osMonitor = new WindowsMonitor();
            else
                throw new RuntimeException("The os is unknown! ");
        }
        osMonitor.monitor(monitorInfo);
    }
}

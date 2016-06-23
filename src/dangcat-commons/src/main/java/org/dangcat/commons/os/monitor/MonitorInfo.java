package org.dangcat.commons.os.monitor;

/**
 * 监控的资源信息。
 * @author dangcat
 * 
 */
public interface MonitorInfo extends java.io.Serializable
{
    String DiskUsageSpaceRatio = "DiskUsageSpaceRatio";
    String FreeDiskSpace = "FreeDiskSpace";
    String ProcessCpuRatio = "ProcessCpuRatio";
    String ProcessUsageMemory = "ProcessUsageMemory";
    String ProcessUsageMemoryRatio = "ProcessUsageMemoryRatio";
    String TotalCpuRatio = "TotalCpuRatio";
    String TotalDiskSpace = "TotalDiskSpace";
    String TotalPhysicalMemory = "TotalPhysicalMemory";
    String TotalUsageMemory = "TotalUsageMemory";

    Number getValue(String name);

    void setValue(String name, Number value);
}

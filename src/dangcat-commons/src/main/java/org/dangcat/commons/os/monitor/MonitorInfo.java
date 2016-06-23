package org.dangcat.commons.os.monitor;

/**
 * 监控的资源信息。
 * @author dangcat
 * 
 */
public interface MonitorInfo extends java.io.Serializable
{
    public static final String DiskUsageSpaceRatio = "DiskUsageSpaceRatio";
    public static final String FreeDiskSpace = "FreeDiskSpace";
    public static final String ProcessCpuRatio = "ProcessCpuRatio";
    public static final String ProcessUsageMemory = "ProcessUsageMemory";
    public static final String ProcessUsageMemoryRatio = "ProcessUsageMemoryRatio";
    public static final String TotalCpuRatio = "TotalCpuRatio";
    public static final String TotalDiskSpace = "TotalDiskSpace";
    public static final String TotalPhysicalMemory = "TotalPhysicalMemory";
    public static final String TotalUsageMemory = "TotalUsageMemory";

    public Number getValue(String name);

    public void setValue(String name, Number value);
}

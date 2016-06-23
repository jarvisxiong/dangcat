package org.dangcat.commons.os.monitor;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.CommandExecutor;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

class WindowsMonitor extends OSMonitor
{
    private static final int FAULTLENGTH = 10;
    private static final Logger logger = Logger.getLogger(WindowsMonitor.class);
    private static final String MONITOR_CPU_CMD = "wmic process get Caption, CommandLine, KernelModeTime, UserModeTime, WorkingSetSize, Handle";
    private static final String MONITOR_PHYSICALMEM_CMD = "wmic ComputerSystem get TotalPhysicalMemory|findstr \"[0-9]\"";
    private static final int WAIT_CPU_TIME = 30;
    private static final Long ZERO_LONG = 0l;
    private CpuInfo priorCpuInfo = null;

    private static long parseLong(String line, int startIndex, int endIndex)
    {
        String subString = substring(line, startIndex, endIndex - 1);
        Long value = ValueUtils.parseLong(subString);
        return value == null ? ZERO_LONG : value;
    }

    protected static String substring(String text, int startIndex, int endIndex)
    {
        byte[] bytes = text.getBytes();
        StringBuilder result = new StringBuilder();
        for (int i = startIndex; i <= endIndex; i++)
            result.append((char) bytes[i]);
        return result.toString();
    }

    @Override
    protected void monitorCPU(MonitorInfo monitorInfo)
    {
        try
        {
            // 取进程信息
            CpuInfo currentCpuInfo = this.readProcessInfo();
            if (this.priorCpuInfo == null)
            {
                this.priorCpuInfo = currentCpuInfo;
                Thread.sleep(WAIT_CPU_TIME);
                currentCpuInfo = this.readProcessInfo();
            }
            if (this.priorCpuInfo != null && currentCpuInfo != null)
            {
                // 系统总的CPU占用率
                long idleTime = currentCpuInfo.systemIdleTime - this.priorCpuInfo.systemIdleTime;
                long busyTime = currentCpuInfo.totalUsageTime - this.priorCpuInfo.totalUsageTime;
                long totalTime = busyTime + idleTime;
                if (totalTime > 0)
                {
                    double totalCpuRatio = 100.0 * busyTime / totalTime;
                    totalCpuRatio = Math.min(Math.max(totalCpuRatio, 0.0), 100.0);
                    monitorInfo.setValue(MonitorInfo.TotalCpuRatio, totalCpuRatio);
                }
                // 总共使用的内存数。
                monitorInfo.setValue(MonitorInfo.TotalUsageMemory, currentCpuInfo.totalUsageMemory);
                // 进程的CPU占用率
                long processTime = currentCpuInfo.processUsageTime - this.priorCpuInfo.processUsageTime;
                if (totalTime > 0)
                {
                    double processCpuRatio = 100.0 * processTime / totalTime;
                    processCpuRatio = Math.min(Math.max(processCpuRatio, 0.0), 100.0);
                    monitorInfo.setValue(MonitorInfo.ProcessCpuRatio, processCpuRatio);
                }
                // 进程占用的内存数。
                monitorInfo.setValue(MonitorInfo.ProcessUsageMemory, currentCpuInfo.processUsageMemory);
            }
            this.priorCpuInfo = currentCpuInfo;
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
                logger.error(this, e);
            else
                logger.error(e);
        }
    }

    @Override
    protected void monitorMemory(MonitorInfo monitorInfo)
    {
        Number totalPhysicalMemory = monitorInfo.getValue(MonitorInfo.TotalPhysicalMemory);
        if (monitorInfo.getValue(MonitorInfo.TotalPhysicalMemory) == null || totalPhysicalMemory.equals(ZERO_LONG))
        {
            try
            {
                String info = CommandExecutor.execute(MONITOR_PHYSICALMEM_CMD);
                BufferedReader bufferedReader = new BufferedReader(new StringReader(info));
                String line = null;
                while ((line = bufferedReader.readLine()) != null)
                {
                    if (line == null || line.trim().length() == 0)
                        continue;

                    monitorInfo.setValue(MonitorInfo.TotalPhysicalMemory, parseLong(line, 0, line.length()));
                }
            }
            catch (IOException e)
            {
            }
        }
    }

    private CpuInfo readProcessInfo()
    {
        CpuInfo cpuInfo = null;
        try
        {
            String info = CommandExecutor.execute(MONITOR_CPU_CMD);
            if (info == null || info.length() < FAULTLENGTH)
                return null;

            Integer currentPID = Environment.getCurrentPID();
            BufferedReader bufferedReader = new BufferedReader(new StringReader(info));
            String line = bufferedReader.readLine();
            int captionIndex = line.indexOf("Caption");
            int commandLineIndex = line.indexOf("CommandLine");
            int kernelModeTimeIndex = line.indexOf("KernelModeTime");
            int userModeTimeIndex = line.indexOf("UserModeTime");
            int handleIndex = line.indexOf("Handle");
            int workingSetSizeIndex = line.indexOf("WorkingSetSize");

            cpuInfo = new CpuInfo();
            while ((line = bufferedReader.readLine()) != null)
            {
                if (line.length() < handleIndex)
                    continue;

                String caption = substring(line, captionIndex, commandLineIndex - 1).trim();
                String commandLine = substring(line, commandLineIndex, kernelModeTimeIndex - 1).trim();
                if (commandLine.indexOf("wmic") >= 0)
                    continue;

                // 读取CPU的空闲时间。
                if (caption.equals("System Idle Process") || caption.equals("System"))
                {
                    cpuInfo.systemIdleTime += parseLong(line, kernelModeTimeIndex, userModeTimeIndex);
                    cpuInfo.systemIdleTime += parseLong(line, userModeTimeIndex, workingSetSizeIndex);
                    continue;
                }

                // 读取CPU的消耗时间。
                long kernelTime = parseLong(line, kernelModeTimeIndex, userModeTimeIndex);
                long userTime = parseLong(line, userModeTimeIndex, workingSetSizeIndex);
                cpuInfo.totalUsageTime += kernelTime + userTime;

                long workingSetSize = parseLong(line, workingSetSizeIndex, line.length());
                cpuInfo.totalUsageMemory += workingSetSize;
                // 进程的CPU占用率和内存占用数。
                String subString = substring(line, handleIndex, kernelModeTimeIndex - 1);
                Integer handle = ValueUtils.parseInt(subString);
                if (currentPID.equals(handle))
                {
                    cpuInfo.processUsageTime = kernelTime + userTime;
                    cpuInfo.processUsageMemory = workingSetSize;
                }
            }
        }
        catch (IOException e)
        {
        }
        return cpuInfo;
    }

    class CpuInfo {
        long processUsageMemory = 0;
        long processUsageTime = 0;
        long systemIdleTime = 0;
        long totalUsageMemory = 0;
        long totalUsageTime = 0;
    }
}

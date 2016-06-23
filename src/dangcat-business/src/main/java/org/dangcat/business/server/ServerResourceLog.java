package org.dangcat.business.server;

import org.dangcat.persistence.annotation.*;

import java.util.Date;

@Table("ServerStatusLog")
@Indexes( { @Index("ServerId, DateTime") })
@SqlXml
public class ServerResourceLog
{
    public static final String DateTime = "DateTime";
    public static final String Id = "Id";
    public static final String OtherUsageMemory = "OtherUsageMemory";
    public static final String ProcessCpuRatio = "ProcessCpuRatio";
    public static final String ProcessUsageMemory = "ProcessUsageMemory";
    public static final String ServerId = "ServerId";
    public static final String TotalCpuRatio = "TotalCpuRatio";
    private static final long serialVersionUID = 1L;
    @Column(index = 2, isNullable = false)
    private Date dateTime = null;

    @Column(isPrimaryKey = true, isAutoIncrement = true, index = 0)
    private Integer id = null;

    /** 其它进程使用的物理内存。 */
    @Column(index = 3, logic = "octets")
    private long otherUsageMemory;
    /** CPU利用率。 */
    @Column(index = 4, logic = "percent")
    private double processCpuRatio;
    /** 进程占用的内存。 */
    @Column(index = 2, logic = "octets")
    private long processUsageMemory;
    @Column(index = 1, displaySize = 32, isNullable = false)
    private Integer serverId = null;

    /** 系统的CPU占用率。 */
    @Column(index = 5, logic = "percent")
    private double totalCpuRatio;

    public Date getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(Date dateTime)
    {
        this.dateTime = dateTime;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public long getOtherUsageMemory()
    {
        return otherUsageMemory;
    }

    public void setOtherUsageMemory(long otherUsageMemory)
    {
        this.otherUsageMemory = otherUsageMemory;
    }

    public double getProcessCpuRatio()
    {
        return processCpuRatio;
    }

    public void setProcessCpuRatio(double processCpuRatio)
    {
        this.processCpuRatio = processCpuRatio;
    }

    public long getProcessUsageMemory()
    {
        return processUsageMemory;
    }

    public void setProcessUsageMemory(long processUsageMemory)
    {
        this.processUsageMemory = processUsageMemory;
    }

    public Integer getServerId()
    {
        return serverId;
    }

    public void setServerId(Integer serverId)
    {
        this.serverId = serverId;
    }

    public double getTotalCpuRatio()
    {
        return totalCpuRatio;
    }

    public void setTotalCpuRatio(double totalCpuRatio)
    {
        this.totalCpuRatio = totalCpuRatio;
    }
}

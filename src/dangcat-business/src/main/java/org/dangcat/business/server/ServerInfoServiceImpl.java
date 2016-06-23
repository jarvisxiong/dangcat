package org.dangcat.business.server;

import org.dangcat.boot.server.domain.ServerInfo;
import org.dangcat.business.security.BusinessPermissionProvider;
import org.dangcat.business.service.impl.BusinessServiceBase;
import org.dangcat.chart.TimeData;
import org.dangcat.chart.TimeRange;
import org.dangcat.commons.resource.Resources;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.annotation.PermissionProvider;
import org.dangcat.persistence.entity.LoadEntityContext;

import java.util.Date;
import java.util.List;

/**
 * 服务器管理。
 * @author
 * 
 */
@Resources( { ServerInfoException.class, ServerResourceLog.class })
@PermissionProvider(BusinessPermissionProvider.class)
public class ServerInfoServiceImpl extends BusinessServiceBase<ServerInfoQuery, ServerInfoQuery, ServerInfoFilter> implements ServerInfoService
{
    public ServerInfoServiceImpl(ServiceProvider parent)
    {
        super(parent);
    }

    @Override
    public long getTotalPhysicalMemory(Integer id)
    {
        long totalPhysicalMemory = 0;
        ServerInfo serverInfo = this.getEntityManager().load(ServerInfo.class, id);
        if (serverInfo != null)
            totalPhysicalMemory = serverInfo.getTotalPhysicalMemory();
        return totalPhysicalMemory;
    }

    @Override
    public TimeData<ServerResourceLog> loadServerResourceLogs(Integer id, TimeRange timeRange, Date lastTime)
    {
        Date beginTime = timeRange.getBeginTime();
        if (lastTime != null && lastTime.compareTo(beginTime) > 0)
            beginTime = lastTime;

        LoadEntityContext loadEntityContext = new LoadEntityContext(ServerResourceLog.class);
        loadEntityContext.getParams().put("serverId", id);
        loadEntityContext.getParams().put("beginTime", beginTime);
        loadEntityContext.getParams().put("endTime", timeRange.getEndTime());

        List<ServerResourceLog> serverResourceLogs = this.getEntityManager().load(loadEntityContext);
        TimeData<ServerResourceLog> timeData = new TimeData<ServerResourceLog>();
        timeData.setBeginTime(timeRange.getBeginTime());
        timeData.setEndTime(timeRange.getEndTime());
        timeData.setTimeStep(timeRange.getTimeStep());
        timeData.setData(serverResourceLogs);
        return timeData;
    }
}

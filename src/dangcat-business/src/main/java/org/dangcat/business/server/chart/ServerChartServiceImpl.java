package org.dangcat.business.server.chart;

import org.dangcat.business.security.BusinessPermissionProvider;
import org.dangcat.business.server.ServerInfoException;
import org.dangcat.business.server.ServerInfoFilter;
import org.dangcat.business.server.ServerInfoQuery;
import org.dangcat.business.server.ServerInfoService;
import org.dangcat.business.server.ServerResourceLog;
import org.dangcat.business.service.impl.BusinessServiceBase;
import org.dangcat.commons.resource.Resources;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.annotation.PermissionProvider;
import org.dangcat.framework.service.annotation.Service;

/**
 * 服务器管理。
 * @author
 * 
 */
@Resources( { ServerInfoException.class, ServerResourceLog.class })
@PermissionProvider(BusinessPermissionProvider.class)
public class ServerChartServiceImpl extends BusinessServiceBase<ServerInfoQuery, ServerInfoQuery, ServerInfoFilter> implements ServerChartService
{
    @Service
    private ServerInfoService serverInfoService;

    public ServerChartServiceImpl(ServiceProvider parent)
    {
        super(parent);
    }

    @Override
    public String createActiveArea(Integer id, Integer width, Integer height) throws ServiceException
    {
        ServiceContext.getInstance().removeSession(ResourceMonitorChart.class);
        ResourceMonitorChart resourceMonitorChart = ResourceMonitorChart.createInstance(this.serverInfoService, id, width, height);
        return resourceMonitorChart.getActiveArea();
    }

    @Override
    public void renderChartImg(Integer id, Integer width, Integer height) throws ServiceException
    {
        ResourceMonitorChart resourceMonitorChart = ResourceMonitorChart.createInstance(this.serverInfoService, id, width, height);
        resourceMonitorChart.render();
    }
}

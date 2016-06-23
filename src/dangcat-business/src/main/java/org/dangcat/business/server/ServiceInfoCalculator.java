package org.dangcat.business.server;

import org.apache.log4j.Logger;
import org.dangcat.boot.ApplicationContext;
import org.dangcat.commons.resource.ResourceReader;
import org.dangcat.commons.resource.ResourceReaderImpl;
import org.dangcat.framework.service.ServiceStatus;
import org.dangcat.persistence.calculate.Calculator;

import java.util.Collection;
import java.util.Map;

public class ServiceInfoCalculator implements Calculator
{
    public static final Logger logger = Logger.getLogger(ServiceInfoCalculator.class);
    private static final String SERVER_TYPE = "ServerType";
    private static ResourceReader resourceReader = new ResourceReaderImpl(ServiceStatus.class);

    public static Map<Integer, String> getServerTypeMap()
    {
        Map<Integer, String> serverTypeMap = null;
        ResourceReader resourceReader = ApplicationContext.getInstance().getResourceReader();
        if (resourceReader != null)
            serverTypeMap = resourceReader.getValueMap(SERVER_TYPE);
        return serverTypeMap;
    }

    public static Map<Integer, String> getStatusMap()
    {
        return resourceReader.getValueMap(ServiceStatus.class.getSimpleName());
    }

    @Override
    public void calculate(Collection<?> entityCollection)
    {
        Map<Integer, String> statusMap = getStatusMap();
        Map<Integer, String> serverTypeMap = getServerTypeMap();
        for (Object entity : entityCollection)
        {
            ServerInfoQuery serverInfoQuery = (ServerInfoQuery) entity;
            if (statusMap != null)
                serverInfoQuery.setStatusName(statusMap.get(serverInfoQuery.getStatus()));
            if (serverTypeMap != null)
                serverInfoQuery.setTypeName(serverTypeMap.get(serverInfoQuery.getType()));
        }
    }

    @Override
    public void calculate(Object entity)
    {
        Map<Integer, String> statusMap = getStatusMap();
        Map<Integer, String> serverTypeMap = getServerTypeMap();
        ServerInfoQuery serverInfoQuery = (ServerInfoQuery) entity;
        if (statusMap != null)
            serverInfoQuery.setStatusName(statusMap.get(serverInfoQuery.getStatus()));
        if (serverTypeMap != null)
            serverInfoQuery.setTypeName(serverTypeMap.get(serverInfoQuery.getType()));
    }
}

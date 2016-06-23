package org.dangcat.business.staff.service.impl;

import org.apache.log4j.Logger;
import org.dangcat.business.staff.domain.RoleInfo;
import org.dangcat.business.staff.service.RoleInfoService;
import org.dangcat.business.systeminfo.PermissionInfo;
import org.dangcat.business.systeminfo.SystemInfoService;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.impl.ServiceFactory;
import org.dangcat.persistence.calculate.Calculator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RoleInfoCalculator implements Calculator
{
    public static final Logger logger = Logger.getLogger(RoleInfoCalculator.class);
    private Map<Integer, String> permissionInfoMap = null;

    public static Map<Integer, String> getPermissionInfoMap()
    {
        Map<Integer, String> permissionInfoMap = null;
        try
        {
            SystemInfoService systemInfoService = ServiceFactory.getServiceLocator().getService(SystemInfoService.class);
            if (systemInfoService != null)
            {
                Collection<PermissionInfo> permissionInfoCollection = systemInfoService.loadPermissions();
                if (permissionInfoCollection != null && permissionInfoCollection.size() > 0)
                {
                    permissionInfoMap = new HashMap<Integer, String>();
                    for (PermissionInfo permissionInfo : permissionInfoCollection)
                        permissionInfoMap.put(permissionInfo.getId(), permissionInfo.getName());
                }
            }
        }
        catch (Exception e)
        {
            logger.error(e, e);
        }
        return permissionInfoMap;
    }

    public static Map<Integer, String> getRoleInfoMap()
    {
        Map<Integer, String> roleInfoMap = null;
        try
        {
            RoleInfoService roleInfoService = ServiceFactory.getServiceLocator().getService(RoleInfoService.class);
            roleInfoMap = roleInfoService.select(null);
        }
        catch (ServiceException e)
        {
            logger.error(e, e);
        }
        return roleInfoMap;
    }

    @Override
    public void calculate(Collection<?> entityCollection)
    {
        for (Object entity : entityCollection)
            this.calculate(entity);
    }

    @Override
    public void calculate(Object entity)
    {
        if (this.permissionInfoMap == null)
            this.permissionInfoMap = getPermissionInfoMap();
        if (this.permissionInfoMap != null)
        {
            RoleInfo roleInfo = (RoleInfo) entity;
            Collection<PermissionInfo> permissionInfoCollection = roleInfo.getPermissions();
            for (PermissionInfo permissionInfo : permissionInfoCollection)
                permissionInfo.setName(this.permissionInfoMap.get(permissionInfo.getId()));
        }
    }
}

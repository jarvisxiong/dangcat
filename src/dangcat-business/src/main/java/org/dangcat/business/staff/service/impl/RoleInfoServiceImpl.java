package org.dangcat.business.staff.service.impl;

import java.util.Collection;
import java.util.Map;

import org.dangcat.boot.service.impl.ServiceCalculator;
import org.dangcat.business.annotation.BusinessValidator;
import org.dangcat.business.security.BusinessPermissionProvider;
import org.dangcat.business.service.impl.BusinessServiceBase;
import org.dangcat.business.staff.domain.RoleBasic;
import org.dangcat.business.staff.domain.RoleInfo;
import org.dangcat.business.staff.domain.RolePermission;
import org.dangcat.business.staff.exceptions.RoleInfoException;
import org.dangcat.business.staff.filter.RoleInfoFilter;
import org.dangcat.business.staff.service.RoleInfoService;
import org.dangcat.business.staff.validator.RoleInfoValidator;
import org.dangcat.business.systeminfo.PermissionInfo;
import org.dangcat.commons.resource.Resources;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.annotation.PermissionProvider;

/**
 * The service implements for RoleInfo.
 * @author dangcat
 * 
 */
@Resources( { RoleInfoException.class })
@BusinessValidator(RoleInfoValidator.class)
@PermissionProvider(BusinessPermissionProvider.class)
public class RoleInfoServiceImpl extends BusinessServiceBase<RoleBasic, RoleInfo, RoleInfoFilter> implements RoleInfoService
{
    public RoleInfoServiceImpl(ServiceProvider parent)
    {
        super(parent);
    }

    @Override
    protected void prepareStore(RoleInfo roleInfo)
    {
        Map<Integer, String> permissionInfoMap = RoleInfoCalculator.getPermissionInfoMap();
        Collection<RolePermission> rolePermissionCollection = roleInfo.getRolePermissions();
        rolePermissionCollection.clear();
        for (PermissionInfo permissionInfo : roleInfo.getPermissions())
        {
            if (permissionInfoMap != null && !permissionInfoMap.containsKey(permissionInfo.getId()))
                continue;
            if (ServiceCalculator.isMethod(permissionInfo.getId()))
                rolePermissionCollection.add(new RolePermission(roleInfo.getId(), permissionInfo.getId()));
        }
    }
}

package org.dangcat.business.staff.service.impl;

import org.dangcat.business.security.BusinessPermissionProvider;
import org.dangcat.commons.reflect.Permission;

import java.util.Collection;

/**
 * 操作员权限控制。
 * @author dangcat
 * 
 */
public class OperatorInfoPermissionProvider extends BusinessPermissionProvider
{
    public static final Integer ADVANCEDMODIFY = 11;
    public static final Integer RESETPASSWORD = 10;
    private static final String ADVANCEDMODIFY_NAME = "advencedModify";
    private static final String RESETPASSWORD_NAME = "resetPassword";

    @Override
    protected void createPermissions(Collection<Permission> permissions)
    {
        super.createPermissions(permissions);
        permissions.add(new Permission(RESETPASSWORD, RESETPASSWORD_NAME));
        permissions.add(new Permission(ADVANCEDMODIFY, ADVANCEDMODIFY_NAME, false));
    }

    @Override
    protected Integer getMethodPermissionId(String methodName)
    {
        if (RESETPASSWORD_NAME.equals(methodName))
            return RESETPASSWORD;
        return super.getMethodPermissionId(methodName);
    }
}

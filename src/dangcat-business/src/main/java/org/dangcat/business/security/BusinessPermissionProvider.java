package org.dangcat.business.security;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.dangcat.commons.reflect.Permission;
import org.dangcat.framework.service.PermissionProvider;

/**
 * 业务权限提供者。
 * @author dangcat
 * 
 */
public class BusinessPermissionProvider implements PermissionProvider
{
    public static final Integer CONFIG = 6;
    private static final String CONFIG_NAME = "config";
    public static final Integer CREATE = 3;
    private static final String CREATE_NAME = "create";
    public static final Integer DELETE = 2;
    private static final String DELETE_NAME = "delete";
    public static final Integer QUERY = 1;
    private static final String QUERY_NAME = "query";
    public static final Integer SAVE = 4;
    private static final String SAVE_NAME = "save";
    public static final Integer VIEW = 5;
    private static final String VIEW_NAME = "view";
    private Map<Integer, Permission> permissionMap = null;

    protected void createPermissions(Collection<Permission> permissions)
    {
        permissions.add(new Permission(QUERY, QUERY_NAME));
        permissions.add(new Permission(DELETE, DELETE_NAME));
        permissions.add(new Permission(CREATE, CREATE_NAME));
        permissions.add(new Permission(SAVE, SAVE_NAME));
        permissions.add(new Permission(VIEW, VIEW_NAME));
        permissions.add(new Permission(CONFIG, CONFIG_NAME));
    }

    @Override
    public Permission getMethodPermission(String methodName)
    {
        Permission permission = null;
        Integer permissionId = this.getMethodPermissionId(methodName);
        if (permissionId != null)
            permission = this.getPermissionMap().get(permissionId);
        return permission;
    }

    protected Integer getMethodPermissionId(String methodName)
    {
        Integer permissionId = null;
        if (QUERY_NAME.equalsIgnoreCase(methodName))
            permissionId = QUERY;
        else if (CREATE_NAME.equalsIgnoreCase(methodName))
            permissionId = CREATE;
        else if (DELETE_NAME.equalsIgnoreCase(methodName))
            permissionId = DELETE;
        else if (SAVE_NAME.equalsIgnoreCase(methodName))
            permissionId = SAVE;
        else if (VIEW_NAME.equalsIgnoreCase(methodName))
            permissionId = VIEW;
        else if (CONFIG_NAME.equalsIgnoreCase(methodName))
            permissionId = CONFIG;
        return permissionId;
    }

    @Override
    public Map<Integer, Permission> getPermissionMap()
    {
        if (this.permissionMap == null)
        {
            Collection<Permission> permissions = new HashSet<Permission>();
            this.createPermissions(permissions);
            Map<Integer, Permission> permissionMap = new HashMap<Integer, Permission>();
            for (Permission permission : permissions)
                permissionMap.put(permission.getId(), permission);
            this.permissionMap = permissionMap;
        }
        return this.permissionMap;
    }

    @Override
    public Permission getRootPermission()
    {
        return this.permissionMap.get(QUERY);
    }
}

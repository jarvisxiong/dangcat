package org.dangcat.framework.service;

import org.dangcat.commons.reflect.Permission;

import java.util.Map;

/**
 * 权限控制表。
 * @author dangcat
 * 
 */
public interface PermissionProvider
{
    /**
     * 读取方法的权限对象。
     * @param methodName 方法名。
     * @return 权限对象。
     */
    Permission getMethodPermission(String methodName);

    /**
     * 提供权限控制映射表。
     */
    Map<Integer, Permission> getPermissionMap();

    /**
     * 读取根权限对象。
     * @return 权限对象。
     */
    Permission getRootPermission();
}

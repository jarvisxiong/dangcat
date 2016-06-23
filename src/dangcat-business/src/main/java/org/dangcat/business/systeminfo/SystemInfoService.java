package org.dangcat.business.systeminfo;

import org.dangcat.boot.menus.Menu;
import org.dangcat.framework.service.annotation.JndiName;

import java.util.Collection;
import java.util.Map;

/**
 * 系统服务。
 * @author Administrator
 * 
 */
@JndiName(module = "System", name = "SystemInfo")
public interface SystemInfoService
{
    /**
     * 加载系统菜单项。
     */
    Collection<Menu> loadMenus();

    /**
     * 参数映射表。
     */
    Map<Integer, String> loadParamMap(String name);

    /**
     * 所有权限信息。
     */
    Collection<PermissionInfo> loadPermissions();

    /**
     * 系统信息。
     */
    SystemInfo loadSystemInfo();
}

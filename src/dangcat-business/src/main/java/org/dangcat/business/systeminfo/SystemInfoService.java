package org.dangcat.business.systeminfo;

import java.util.Collection;
import java.util.Map;

import org.dangcat.boot.menus.Menu;
import org.dangcat.framework.service.annotation.JndiName;

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
    public Collection<Menu> loadMenus();

    /**
     * 参数映射表。
     */
    public Map<Integer, String> loadParamMap(String name);

    /**
     * 所有权限信息。
     */
    public Collection<PermissionInfo> loadPermissions();

    /**
     * 系统信息。
     */
    public SystemInfo loadSystemInfo();
}

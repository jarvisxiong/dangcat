package org.dangcat.business.systeminfo.impl;

import org.dangcat.boot.ApplicationContext;
import org.dangcat.boot.menus.Menu;
import org.dangcat.boot.menus.Menus;
import org.dangcat.boot.menus.MenusLoader;
import org.dangcat.boot.menus.MenusManager;
import org.dangcat.business.systeminfo.*;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.resource.ResourceReader;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.annotation.Context;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 系统服务。
 *
 * @author fanst174766
 */
public class SystemInfoServiceImpl extends ServiceBase implements SystemInfoService {
    @Context
    private ServiceContext serviceContext = null;

    public SystemInfoServiceImpl(ServiceProvider parent) {
        super(parent);
    }

    private Locale getLocale() {
        return this.serviceContext.getLocale();
    }

    private ResourceReader getResourceReader() {
        return ApplicationContext.getInstance().getResourceReader();
    }

    private String getText(String key) {
        return this.getResourceReader().getText(this.getLocale(), key);
    }

    private void loadExtendSystemInfo(SystemInfo systemInfo) {
        ExtendSystemInfo extendSystemInfoAnnotation = ReflectUtils.findAnnotation(ApplicationContext.getInstance().getMainService().getClass(), ExtendSystemInfo.class);
        if (extendSystemInfoAnnotation != null && extendSystemInfoAnnotation.value() != null) {
            SystemInfoProvider systemInfoProvider = (SystemInfoProvider) ReflectUtils.newInstance(extendSystemInfoAnnotation.value());
            if (systemInfoProvider != null)
                systemInfoProvider.createExtendInfos(systemInfo);
        }
    }

    /**
     * 加载系统菜单项。
     */
    public Collection<Menu> loadMenus() {
        MenusLoader menusLoader = new MenusLoader(this.getResourceReader(), this.getLocale());
        Menus menus = menusLoader.load();
        return menus.getData();
    }

    @Override
    public Map<Integer, String> loadParamMap(String name) {
        Map<Integer, String> paramMap = new LinkedHashMap<Integer, String>();
        String propertyName = System.getProperty("ParamMap." + name + ".MaxValue");
        Integer maxValue = ValueUtils.parseInt(propertyName, 3);
        for (int i = 0; i < maxValue; i++) {
            String value = this.getText("ParamMap." + name + "." + i);
            if (!ValueUtils.isEmpty(value))
                paramMap.put(i, value);
        }
        return paramMap;
    }

    @Override
    public Collection<PermissionInfo> loadPermissions() {
        return new PermissionInfoCreator(this.getResourceReader(), this.getLocale()).create();
    }

    /**
     * 加载系统信息。
     */
    public SystemInfo loadSystemInfo() {
        SystemInfo systemInfo = new SystemInfo();
        systemInfo.setVersion(ApplicationContext.getInstance().getConfigureReader().getVersion());
        systemInfo.setProjectTitle(this.getText("projectTitle"));
        systemInfo.setMasterTitle(this.getText("masterTitle"));
        systemInfo.setSecondTitle(this.getText("secondTitle"));
        systemInfo.setCopyRight(this.getText("copyRight"));
        systemInfo.setSessionId(this.serviceContext.getSessionId());
        Menus menus = MenusManager.getInstance().getMenus();
        if (menus != null)
            systemInfo.setBaseUrl(menus.getBaseUrl());
        systemInfo.setServicePrincipal(this.serviceContext.getServicePrincipal());
        this.loadExtendSystemInfo(systemInfo);
        return systemInfo;
    }
}
package org.dangcat.boot.menus;

import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.commons.reflect.Permission;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.resource.ResourceReader;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.PermissionProvider;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.ServiceLocator;
import org.dangcat.framework.service.ServicePrincipal;
import org.dangcat.framework.service.impl.ServiceFactory;
import org.dangcat.framework.service.impl.ServiceInfo;

import java.util.Locale;

public class MenusLoader {
    private Locale locale = null;
    private ResourceReader resourceReader = null;

    public MenusLoader(ResourceReader resourceReader, Locale locale) {
        this.resourceReader = resourceReader;
        this.locale = locale;
    }

    private Menu createMenu(Menu srcMenu) {
        Menu dstMenu = new Menu();
        ReflectUtils.copyProperties(srcMenu, dstMenu);
        this.createMenuDataCollection(srcMenu, dstMenu);
        return (Menu) this.getResult(dstMenu);
    }

    private MenuData createMenuData(MenuData srcMenuData) {
        MenuData dstMenuData = null;
        if (srcMenuData instanceof MenuItem) {
            MenuItem srcMenuItem = (MenuItem) srcMenuData;
            if (this.hasPermission(srcMenuItem)) {
                dstMenuData = new MenuItem();
                ReflectUtils.copyProperties(srcMenuItem, dstMenuData);
            }
        } else if (srcMenuData instanceof Submenu)
            dstMenuData = this.createSubmenu((Submenu) srcMenuData);
        else if (srcMenuData instanceof Separator)
            dstMenuData = srcMenuData;

        if (dstMenuData instanceof MenuBase)
            this.createTitle((MenuBase) dstMenuData);
        return dstMenuData;
    }

    private void createMenuDataCollection(MenuDataCollection srcMenuDataCollection, MenuDataCollection dstMenuDataCollection) {
        if (srcMenuDataCollection.getDataCollection() != null) {
            for (MenuData srcMenuData : srcMenuDataCollection.getDataCollection()) {
                MenuData dstMenuData = this.createMenuData(srcMenuData);
                if (dstMenuData != null)
                    dstMenuDataCollection.addMenuData(dstMenuData);
            }
        }
    }

    private Submenu createSubmenu(Submenu srcSubmenu) {
        Submenu dstSubmenu = new Submenu();
        ReflectUtils.copyProperties(srcSubmenu, dstSubmenu);
        this.createMenuDataCollection(srcSubmenu, dstSubmenu);
        return (Submenu) this.getResult(dstSubmenu);
    }

    private void createTitle(MenuBase menuBase) {
        String title = null;
        if (this.resourceReader != null) {
            if (!ValueUtils.isEmpty(menuBase.getTitle()))
                title = this.resourceReader.getText(this.locale, menuBase.getTitle());
            if (ValueUtils.isEmpty(title)) {
                String key = menuBase.getClass().getSimpleName() + "." + menuBase.getName() + ".title";
                title = this.resourceReader.getText(this.locale, key);
            }
            if (ValueUtils.isEmpty(title) && menuBase instanceof MenuItem) {
                ServiceInfo serviceInfo = this.getServiceInfo((MenuItem) menuBase);
                if (serviceInfo != null)
                    title = serviceInfo.getTitle(this.locale);
            }
        }
        if (ValueUtils.isEmpty(title))
            title = menuBase.getName();
        menuBase.setTitle(title);
    }

    private MenuDataCollection getResult(MenuDataCollection menuDataCollection) {
        this.removeSeparator(menuDataCollection);
        if (menuDataCollection instanceof Menu) {
            Menu menu = (Menu) menuDataCollection;
            if (!ValueUtils.isEmpty(menu.getUrl())) {
                this.createTitle(menu);
                return menu;
            }
        }
        if (menuDataCollection == null || menuDataCollection.getDataCollection() == null || menuDataCollection.getDataCollection().isEmpty())
            return null;
        this.createTitle(menuDataCollection);
        return menuDataCollection;
    }

    private ServiceInfo getServiceInfo(MenuItem menuItem) {
        ServiceInfo serviceInfo = null;
        ServiceLocator serviceLocator = ServiceFactory.getServiceLocator();
        if (serviceLocator != null)
            serviceInfo = serviceLocator.getServiceInfo(menuItem.getJndiName());
        return serviceInfo;
    }

    private boolean hasPermission(MenuItem menuItem) {
        ServiceContext serviceContext = ServiceContext.getInstance();
        if (serviceContext == null)
            return true;

        ServicePrincipal servicePrincipal = ServiceContext.getInstance().getServicePrincipal();
        ServiceInfo serviceInfo = this.getServiceInfo(menuItem);
        if (serviceInfo == null)
            return !ValueUtils.isEmpty(menuItem.getUrl());

        PermissionProvider permissionProvider = serviceInfo.getPermissionProvider();
        if (permissionProvider == null)
            return true;

        Permission rootPermission = permissionProvider.getRootPermission();
        if (rootPermission != null && !servicePrincipal.hasPermission(rootPermission.getValue()))
            return false;

        for (MethodInfo methodInfo : serviceInfo.getServiceMethodInfo().getMethodInfos()) {
            if (methodInfo.getPermission() == null)
                continue;
            if (servicePrincipal.hasPermission(methodInfo.getPermission().getValue()))
                return true;
        }
        return false;
    }

    public Menus load() {
        Menus srcMenus = MenusManager.getInstance().getMenus();
        Menus dstMenus = new Menus();
        ReflectUtils.copyProperties(srcMenus, dstMenus);
        for (Menu srcMenu : srcMenus.getData()) {
            Menu dstMenu = this.createMenu(srcMenu);
            if (dstMenu != null)
                dstMenus.getData().add(dstMenu);
        }
        return dstMenus;
    }

    private void removeSeparator(MenuDataCollection menuDataCollection) {
        if (menuDataCollection != null && menuDataCollection.getDataCollection() != null) {
            MenuData[] menuDataArray = menuDataCollection.getDataCollection().toArray(new MenuData[0]);
            // 清除连续的Separator
            for (int i = 0; i < menuDataArray.length; i++) {
                if (i < menuDataArray.length - 1) {
                    if (menuDataArray[i] instanceof Separator && menuDataArray[i + 1] instanceof Separator)
                        menuDataArray[i] = null;
                }
            }
            // 清除开头和结尾的Separator
            if (menuDataArray.length > 0) {
                if (menuDataArray[0] instanceof Separator)
                    menuDataArray[0] = null;
                if (menuDataArray[menuDataArray.length - 1] instanceof Separator)
                    menuDataArray[menuDataArray.length - 1] = null;
            }
            menuDataCollection.getDataCollection().clear();
            for (MenuData menuData : menuDataArray) {
                if (menuData != null)
                    menuDataCollection.getDataCollection().add(menuData);
            }
        }
    }
}

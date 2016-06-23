package org.dangcat.business.systeminfo.impl;

import org.apache.log4j.Logger;
import org.dangcat.boot.permission.JndiName;
import org.dangcat.boot.permission.Module;
import org.dangcat.boot.permission.PermissionManager;
import org.dangcat.business.systeminfo.PermissionInfo;
import org.dangcat.commons.reflect.Permission;
import org.dangcat.commons.resource.ResourceReader;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.PermissionProvider;
import org.dangcat.framework.service.ServiceLocator;
import org.dangcat.framework.service.impl.ServiceFactory;
import org.dangcat.framework.service.impl.ServiceInfo;

import java.util.*;

/**
 * 权限信息。
 *
 * @author dangcat
 */
public class PermissionInfoCreator {
    protected static final Logger logger = Logger.getLogger(PermissionInfoCreator.class);
    private Locale locale = null;
    private ResourceReader resourceReader = null;

    public PermissionInfoCreator(ResourceReader resourceReader, Locale locale) {
        this.resourceReader = resourceReader;
        this.locale = locale;
    }

    public Collection<PermissionInfo> create() {
        List<PermissionInfo> permissionInfoList = new ArrayList<PermissionInfo>();
        try {
            Collection<PermissionInfo> permissionInfoSet = new HashSet<PermissionInfo>();
            for (Module module : PermissionManager.getInstance().getModules())
                this.createModule(permissionInfoSet, module);
            permissionInfoList.addAll(permissionInfoSet);
            Collections.sort(permissionInfoList);
        } catch (Exception e) {
            logger.error("create the permission error: ", e);
        }
        return permissionInfoList;
    }

    private void createModule(Collection<PermissionInfo> permissionInfoCollection, Module module) {
        int count = 0;
        for (JndiName jndiName : module.getJndiNameCollection()) {
            ServiceInfo serviceInfo = this.getServiceInfo(module.getName(), jndiName.getName());
            if (this.createPermissions(permissionInfoCollection, serviceInfo) > 0) {
                permissionInfoCollection.add(new PermissionInfo(serviceInfo.getId(), serviceInfo.getTitle(this.locale)));
                count++;
            }
        }
        if (count > 0) {
            String moduleTitle = this.getModuleTitle(module);
            permissionInfoCollection.add(new PermissionInfo(module.getPermission(), moduleTitle));
        }
    }

    private int createPermissions(Collection<PermissionInfo> permissionInfoCollection, ServiceInfo serviceInfo) {
        int count = 0;
        if (serviceInfo != null && serviceInfo.getId() != null) {
            PermissionProvider permissionProvider = serviceInfo.getPermissionProvider();
            if (permissionProvider != null) {
                for (Permission permission : permissionProvider.getPermissionMap().values()) {
                    String methodTitle = serviceInfo.getMethodTitle(this.locale, permission.getName());
                    PermissionInfo permissionInfo = new PermissionInfo(permission.getValue(), methodTitle);
                    if (!permissionInfoCollection.contains(permissionInfo))
                        permissionInfoCollection.add(permissionInfo);
                    count++;
                }
            }
        }
        return count;
    }

    private String getModuleTitle(Module module) {
        String moduleTitle = this.resourceReader.getText(this.locale, "Module." + module.getName() + ".title");
        if (ValueUtils.isEmpty(moduleTitle))
            moduleTitle = module.getName();
        return moduleTitle;
    }

    private ServiceInfo getServiceInfo(String moduleName, String servieName) {
        ServiceInfo serviceInfo = null;
        ServiceLocator serviceLocator = ServiceFactory.getServiceLocator();
        if (serviceLocator != null)
            serviceInfo = serviceLocator.getServiceInfo(moduleName + "/" + servieName);
        return serviceInfo;
    }
}

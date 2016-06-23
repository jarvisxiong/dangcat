package org.dangcat.boot.permission;

import org.apache.log4j.Logger;
import org.dangcat.boot.ApplicationContext;
import org.dangcat.boot.ConfigureReader;
import org.dangcat.boot.permission.xml.PermissionManagerXmlResolver;
import org.dangcat.boot.permission.xml.RolePermission;
import org.dangcat.boot.service.impl.ServiceCalculator;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.reflect.Permission;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.impl.ServiceFactory;
import org.dangcat.framework.service.impl.ServiceInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

/**
 * 权限管理。
 *
 * @author dangcat
 */
public class PermissionManager {
    protected static Logger logger = Logger.getLogger(PermissionManager.class);
    private static PermissionManager instance = new PermissionManager();
    private Map<Integer, Permission> methodPermissionMap = new HashMap<Integer, Permission>();
    private Map<String, Module> moduleMap = new HashMap<String, Module>();
    private Map<String, Collection<Integer>> rolePermissionMap = new HashMap<String, Collection<Integer>>();
    private Collection<RolePermission> rolePermissions = null;
    private Map<Integer, ServiceInfo> serviceInfoMap = new HashMap<Integer, ServiceInfo>();

    private PermissionManager() {
    }

    public static PermissionManager getInstance() {
        return instance;
    }

    public static void stop() {
        instance = new PermissionManager();
    }

    public void addModule(Module module) {
        if (this.moduleMap.containsKey(module.getName()))
            logger.error("The permission congfig is error: " + module);
        else
            this.moduleMap.put(module.getName(), module);
    }

    public void addPermissions(String roleName, Collection<Integer> permissions) {
        if (!ValueUtils.isEmpty(roleName)) {
            if (this.rolePermissionMap.containsKey(roleName))
                logger.info("Change role " + roleName + " permissions.");
            if (permissions == null || permissions.size() == 0)
                this.rolePermissionMap.remove(roleName);
            else
                this.rolePermissionMap.put(roleName, permissions);
        }
    }

    private void configure(File configFile) {
        if (configFile == null || !configFile.exists()) {
            logger.warn("The permission file is not exists : " + configFile.getAbsolutePath());
            return;
        }

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(configFile);
            PermissionManagerXmlResolver permissionManagerXmlResolver = new PermissionManagerXmlResolver(this);
            permissionManagerXmlResolver.open(inputStream);
            permissionManagerXmlResolver.resolve();
        } catch (Exception e) {
            logger.error(this, e);
        } finally {
            inputStream = FileUtils.close(inputStream);
        }
    }

    public Module findModule(String jndiName) {
        String moduleName = null;
        int index = jndiName.indexOf("/");
        if (index == -1)
            moduleName = ServiceInfo.DEFAULT_MODULE;
        moduleName = jndiName.substring(0, index);
        return this.moduleMap.get(moduleName);
    }

    public Permission getMethodPermission(Integer permissionValue) {
        return this.methodPermissionMap.get(permissionValue);
    }

    public Collection<Integer> getMethodPermissions() {
        return this.methodPermissionMap.keySet();
    }

    public Collection<Module> getModules() {
        return this.moduleMap.values();
    }

    public Collection<Integer> getPermissions(String roleName) {
        return this.rolePermissionMap.get(roleName);
    }

    public Collection<String> getRoles() {
        return this.rolePermissionMap.keySet();
    }

    public ServiceInfo getServiceInfo(Integer id) {
        return this.serviceInfoMap.get(id);
    }

    private void initialize() {
        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        Collection<String> jndiNameCollection = serviceFactory.getJndiNames(false);
        if (jndiNameCollection != null) {
            for (String jndiName : jndiNameCollection) {
                ServiceInfo serviceInfo = serviceFactory.getServiceInfo(jndiName);
                Module module = this.findModule(jndiName);
                if (module != null) {
                    JndiName serviceName = module.findJndiName(jndiName);
                    if (serviceName != null) {
                        Map<Integer, Permission> methodPermissionMap = ServiceCalculator.createPermissionValues(serviceInfo, module.getId(), serviceName.getId());
                        if (methodPermissionMap != null && !methodPermissionMap.isEmpty())
                            this.methodPermissionMap.putAll(methodPermissionMap);
                        if (serviceInfo.getId() != null)
                            this.serviceInfoMap.put(serviceInfo.getId(), serviceInfo);
                        ServiceCalculator.createMethodValues(serviceInfo, module.getId(), serviceName.getId());
                    }
                }
            }
        }

        if (this.rolePermissions != null) {
            for (RolePermission rolePermission : this.rolePermissions)
                rolePermission.createPermissions(this);
        }
        this.log();
    }

    public void load() {
        ConfigureReader configureReader = ApplicationContext.getInstance().getConfigureReader();
        String permissionConfigFileName = configureReader.getPermissionConfigFileName();
        if (!ValueUtils.isEmpty(permissionConfigFileName)) {
            this.configure(new File(ApplicationContext.getInstance().getContextPath().getConf() + File.separator + permissionConfigFileName));
            this.initialize();
        }
    }

    private void log() {
        if (logger.isDebugEnabled()) {
            for (Entry<String, Collection<Integer>> entry : this.rolePermissionMap.entrySet()) {
                StringBuilder info = new StringBuilder();
                List<Integer> permissionList = new ArrayList<Integer>();
                permissionList.addAll(entry.getValue());
                Collections.sort(permissionList);
                for (Integer permission : permissionList) {
                    if (info.length() == 0)
                        info.append(entry.getKey() + ": ");
                    else
                        info.append(", ");
                    info.append(permission);
                }
                logger.info(info);
            }
        }
    }

    public void setRolePermissions(Collection<RolePermission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }
}

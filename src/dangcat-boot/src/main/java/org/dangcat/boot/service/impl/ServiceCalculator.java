package org.dangcat.boot.service.impl;

import org.dangcat.boot.ApplicationContext;
import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.commons.reflect.Permission;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.PermissionProvider;
import org.dangcat.framework.service.impl.ServiceInfo;

import java.util.*;

public class ServiceCalculator {
    public static final Integer METHOD_TYPE = 2;
    public static final Integer MODULE_TYPE = 0;
    public static final Integer SERVICE_TYPE = 1;
    private static final Integer OFFSET = 100;

    public static Integer create(Integer... ids) {
        Integer id = getSystemId();
        if (ids != null && ids.length > 0) {
            for (Integer number : ids)
                id = id * OFFSET + number;

        }
        return id;
    }

    public static void createMethodValues(ServiceInfo serviceInfo, Integer moduleId, Integer serviceId) {
        serviceInfo.setId(create(moduleId, serviceId));

        Map<Integer, MethodInfo> methodInfoValueMap = null;
        Collection<MethodInfo> methodInfoCollection = serviceInfo.getServiceMethodInfo().getMethodInfos();
        if (methodInfoCollection != null) {
            for (MethodInfo methodInfo : methodInfoCollection) {
                Integer value = null;
                if (methodInfo.getId() == null) {
                    if (methodInfo.getPermission() != null && methodInfo.getPermission().getValue() != null)
                        value = methodInfo.getPermission().getValue();
                } else
                    value = create(moduleId, serviceId, methodInfo.getId());
                if (value != null) {
                    if (methodInfoValueMap == null)
                        methodInfoValueMap = new HashMap<Integer, MethodInfo>();
                    methodInfo.setValue(value);
                    methodInfoValueMap.put(value, methodInfo);
                }
            }
            serviceInfo.getServiceMethodInfo().setMethodInfoValueMap(methodInfoValueMap);
        }
    }

    public static Map<Integer, Permission> createPermissionValues(ServiceInfo serviceInfo, Integer moduleId, Integer serviceId) {
        serviceInfo.setId(create(moduleId, serviceId));

        Map<Integer, Permission> permissionMap = null;
        PermissionProvider permissionProvider = serviceInfo.getPermissionProvider();
        if (permissionProvider != null) {
            for (Permission permission : permissionProvider.getPermissionMap().values()) {
                Integer value = create(moduleId, serviceId, permission.getId());
                if (value != null) {
                    if (permissionMap == null)
                        permissionMap = new HashMap<Integer, Permission>();
                    permission.setValue(value);
                    permissionMap.put(value, permission);
                }
            }
        }
        return permissionMap;
    }

    public static Integer getMenuId(Integer id) {
        return getNumber(id, SERVICE_TYPE);
    }

    public static Integer getMethodId(Integer id) {
        return getNumber(id, METHOD_TYPE);
    }

    public static Integer getModuleId(Integer id) {
        return getNumber(id, MODULE_TYPE);
    }

    private static Integer getNumber(Integer id, int index) {
        Integer[] ids = parse(id);
        Integer number = null;
        if (ids != null && ids.length > index)
            number = ids[index];
        return number;
    }

    public static Integer getParentId(Integer id) {
        Integer parentId = null;
        Integer[] ids = parse(id);
        if (ids != null && ids.length > 2) {
            parentId = getSystemId();
            for (int i = 1; i < ids.length - 1; i++) {
                parentId *= OFFSET;
                if (i < ids.length)
                    parentId += ids[i];

            }
        }
        return parentId;
    }

    public static Integer getSortNumber(Integer id) {
        int number = getSystemId();
        Integer[] ids = parse(id);
        for (int i = 1; i < 4; i++) {
            number *= OFFSET;
            if (i < ids.length)
                number += ids[i];

        }
        return number;
    }

    public static Integer getSystemId() {
        return ApplicationContext.getInstance().getConfigureReader().getId();
    }

    public static Integer getType(Integer id) {
        Integer[] ids = parse(id);
        if (ids != null)
            return ids.length - 2;
        return -1;
    }

    public static boolean isMethod(Integer id) {
        return METHOD_TYPE.equals(getType(id));
    }

    public static boolean isModule(Integer id) {
        return MODULE_TYPE.equals(getType(id));
    }

    public static boolean isService(Integer id) {
        return SERVICE_TYPE.equals(getType(id));
    }

    private static Integer[] parse(Integer id) {
        List<Integer> idList = new LinkedList<Integer>();
        Integer permissionId = id;
        while (permissionId % OFFSET > 0) {
            idList.add(0, permissionId % OFFSET);
            permissionId = permissionId / OFFSET;
        }
        return idList.toArray(new Integer[0]);
    }

    public static Integer parse(String text) {
        Integer permission = ValueUtils.parseInt(text);
        if (permission != null) {
            Integer[] ids = ServiceCalculator.parse(permission);
            if (ids != null && ids.length > 0)
                permission = create(ids);
        }
        return permission;
    }
}

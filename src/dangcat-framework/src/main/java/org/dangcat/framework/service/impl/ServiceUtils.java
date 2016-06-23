package org.dangcat.framework.service.impl;

import org.apache.log4j.Logger;
import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.commons.reflect.Permission;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.serialize.xml.Property;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.conf.ConfigProvider;
import org.dangcat.framework.service.PermissionProvider;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.annotation.Context;
import org.dangcat.framework.service.annotation.JndiName;
import org.dangcat.framework.service.annotation.MethodId;
import org.dangcat.framework.service.interceptor.AfterInterceptor;
import org.dangcat.framework.service.interceptor.BeforeInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class ServiceUtils {
    protected static final Logger logger = Logger.getLogger(ServiceInfo.class);

    private static void createConfigProvider(ServiceInfo serviceInfo) {
        org.dangcat.framework.service.annotation.ConfigProvider configProviderAnnotation = readServiceAnnotation(serviceInfo, org.dangcat.framework.service.annotation.ConfigProvider.class);
        if (configProviderAnnotation != null && configProviderAnnotation.value() != null)
            serviceInfo.setConfigProvider((ConfigProvider) ReflectUtils.getInstance(configProviderAnnotation.value()));
    }

    protected static Object createInstance(ServiceInfo serviceInfo, ServiceProvider parent) {
        Object service = ReflectUtils.newInstance(serviceInfo.getServiceClassType(), new Class<?>[]{ServiceProvider.class}, new Object[]{parent});
        if (service != null) {
            for (Property property : serviceInfo.getProperties())
                ReflectUtils.setProperty(service, property.getName(), property.getValue());
        }
        return service;
    }

    protected static Collection<Object> createInterceptors(Collection<Class<?>> interceptorClasses) {
        Collection<Object> interceptors = null;
        for (Class<?> interceptorClass : interceptorClasses) {
            if (!BeforeInterceptor.class.isAssignableFrom(interceptorClass) && !AfterInterceptor.class.isAssignableFrom(interceptorClass)) {
                logger.error("The interceptor class " + interceptorClass.getName() + " is invalid.");
                continue;
            }
            Object interceptor = ReflectUtils.newInstance(interceptorClass);
            if (interceptor == null)
                logger.error("The interceptor instance " + interceptorClass.getName() + " is not create.");
            else {
                if (interceptors == null)
                    interceptors = new LinkedHashSet<Object>();
                interceptors.add(interceptor);
            }
        }
        return interceptors;
    }

    protected static void createInterceptors(ServiceInfo serviceInfo) {
        Collection<Class<?>> interceptorClasses = serviceInfo.getInterceptors();
        org.dangcat.framework.service.annotation.Interceptors interceptorsAnnotation = readServiceAnnotation(serviceInfo, org.dangcat.framework.service.annotation.Interceptors.class);
        if (interceptorsAnnotation != null) {
            for (Class<?> interceptorClass : interceptorsAnnotation.value())
                interceptorClasses.add(interceptorClass);
        }
        if (!interceptorClasses.isEmpty()) {
            Collection<Object> interceptors = createInterceptors(interceptorClasses);
            if (interceptors != null)
                serviceInfo.addInterceptors(interceptors.toArray());
        }
    }

    public static void createJndiName(ServiceInfo serviceInfo) {
        if (!ValueUtils.isEmpty(serviceInfo.getJndiName()))
            return;

        JndiName jndiName = readServiceAnnotation(serviceInfo, JndiName.class);
        String moduleName = ServiceInfo.DEFAULT_MODULE;
        String name = serviceInfo.getAccessClassType().getSimpleName();
        if (jndiName != null) {
            if (!ValueUtils.isEmpty(jndiName.module()))
                moduleName = jndiName.module();
            if (!ValueUtils.isEmpty(jndiName.name()))
                name = jndiName.name();
        }
        serviceInfo.setJndiName(moduleName + "/" + name);
    }

    private static void createMethodIds(ServiceInfo serviceInfo) {
        Collection<MethodInfo> methodInfoCollection = serviceInfo.getServiceMethodInfo().getMethodInfos();
        if (methodInfoCollection != null) {
            for (MethodInfo methodInfo : methodInfoCollection) {
                MethodId methodIdAnnotation = methodInfo.getMethod().getAnnotation(MethodId.class);
                if (methodIdAnnotation != null)
                    methodInfo.setId(methodIdAnnotation.value());
            }
        }
    }

    private static void createPermissionProvider(ServiceInfo serviceInfo) {
        org.dangcat.framework.service.annotation.PermissionProvider permissionProviderAnnotation = readServiceAnnotation(serviceInfo, org.dangcat.framework.service.annotation.PermissionProvider.class);
        if (permissionProviderAnnotation != null && permissionProviderAnnotation.value() != null)
            serviceInfo.setPermissionProvider((PermissionProvider) ReflectUtils.newInstance(permissionProviderAnnotation.value()));
    }

    private static void createPermissions(ServiceInfo serviceInfo) {
        createPermissionProvider(serviceInfo);

        PermissionProvider permissionProvider = serviceInfo.getPermissionProvider();
        if (permissionProvider == null)
            return;

        Map<Integer, Permission> permissionMap = permissionProvider.getPermissionMap();
        if (permissionMap.size() == 0)
            return;

        Collection<Integer> permissions = new HashSet<Integer>();
        Collection<MethodInfo> methodInfoCollection = serviceInfo.getServiceMethodInfo().getMethodInfos();
        if (methodInfoCollection != null) {
            for (MethodInfo methodInfo : methodInfoCollection) {
                Permission permission = permissionProvider.getMethodPermission(methodInfo.getName());
                if (permission != null) {
                    methodInfo.setPermission(permission);
                    permissions.add(permission.getId());
                }
            }
        }

        // 过滤不存在的权限。
        Integer[] permissionIds = permissionMap.keySet().toArray(new Integer[0]);
        for (Integer permissionId : permissionIds) {
            Permission permission = permissionMap.get(permissionId);
            if (permission.isMethodPermission() && !permissions.contains(permissionId))
                permissionMap.remove(permissionId);
        }
    }

    protected static void initialize(ServiceInfo serviceInfo) {
        createJndiName(serviceInfo);
        createInterceptors(serviceInfo);
        createPermissions(serviceInfo);
        createConfigProvider(serviceInfo);
        createMethodIds(serviceInfo);
    }

    public static void injectContext(Object instance, ServiceContext serviceContext) {
        if (instance != null) {
            List<Field> fieldList = ReflectUtils.findFields(instance.getClass(), Context.class);
            for (Field field : fieldList) {
                try {
                    field.setAccessible(true);
                    field.set(instance, serviceContext);
                } catch (Exception e) {
                    logger.error(e, e);
                }
            }
        }
    }

    public static String readJndiName(Class<?> accessClassType, Class<?> serviceClassType) {
        JndiName jndiName = readServiceAnnotation(accessClassType, serviceClassType, JndiName.class);
        String module = ServiceInfo.DEFAULT_MODULE;
        String name = accessClassType.getSimpleName();
        if (jndiName != null) {
            if (!ValueUtils.isEmpty(jndiName.name()))
                name = jndiName.name();
            if (!ValueUtils.isEmpty(jndiName.module()))
                module = jndiName.module();
        }
        return module + "/" + name;
    }

    private static <T extends Annotation> T readServiceAnnotation(Class<?> accessClassType, Class<?> serviceClassType, Class<T> annotationClass) {
        T annotation = ReflectUtils.findAnnotation(accessClassType, annotationClass);
        if (annotation == null && serviceClassType != null)
            annotation = ReflectUtils.findAnnotation(serviceClassType, annotationClass);
        return annotation;
    }

    private static <T extends Annotation> T readServiceAnnotation(ServiceInfo serviceInfo, Class<T> annotationClass) {
        return readServiceAnnotation(serviceInfo.getAccessClassType(), serviceInfo.getServiceClassType(), annotationClass);
    }
}

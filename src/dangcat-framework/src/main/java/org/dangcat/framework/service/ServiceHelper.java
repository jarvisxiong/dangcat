package org.dangcat.framework.service;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.annotation.ServiceXml;
import org.dangcat.framework.service.impl.ServiceFactory;

/**
 * 服务工具。
 * @author dangcat
 * 
 */
public final class ServiceHelper
{
    private static Collection<InjectProvider> injectProviders = new LinkedHashSet<InjectProvider>();
    protected static final Logger logger = Logger.getLogger(ServiceHelper.class);

    public static synchronized void addInjectProvider(InjectProvider injectProvider)
    {
        if (injectProvider != null)
            injectProviders.add(injectProvider);
    }

    /**
     * 按照服务类型向下找子服务。
     */
    @SuppressWarnings("unchecked")
    private static <T> void findChildren(List<T> findList, Object parent, Class<T> classType)
    {
        if (parent != null)
        {
            if (parent instanceof ServiceProvider && classType != null)
            {
                ServiceProvider serviceProvider = (ServiceProvider) parent;
                T serviceObject = serviceProvider.getService(classType);
                if (serviceObject != null && !findList.contains(serviceObject))
                    findList.add(serviceObject);
            }
            if (classType == null || classType.isAssignableFrom(parent.getClass()))
            {
                if (!findList.contains(parent))
                    findList.add((T) parent);
            }
        }

        if (parent instanceof ServiceBase)
        {
            ServiceBase serviceBase = (ServiceBase) parent;
            for (Object childServiceObject : serviceBase.getChildren())
            {
                ServiceProvider serviceProvider = (ServiceProvider) childServiceObject;
                findChildren(findList, serviceProvider, classType);
            }
        }
    }

    /**
     * 按照服务类型向下找子服务。
     * @param parent 父服务。
     * @param classType 要找的子服务类型。
     * @return 找到的子服务。
     */
    public static <T> List<T> findChildren(Object parent, Class<T> classType)
    {
        List<T> findList = new ArrayList<T>();
        if (parent != null)
            findChildren(findList, parent, classType);
        return findList;
    }

    /**
     * 向服务实例中注入对象。
     * @param serviceProvider 服务提供者。
     * @param serviceInstance 服务实例。
     */
    public static void inject(ServiceProvider serviceProvider, Object serviceInstance)
    {
        for (InjectProvider injectProvider : injectProviders)
            injectProvider.inject(serviceProvider, serviceInstance);
    }

    /**
     * 载入配置文件里的服务设定。
     * @param serviceBase 服务实例。
     */
    public static void loadFromServiceXml(ServiceBase serviceBase)
    {
        Class<?> serviceType = serviceBase.getClass();
        List<Annotation> annotationList = new LinkedList<Annotation>();
        ReflectUtils.findAnnotations(serviceType, ServiceXml.class, annotationList);
        for (Annotation annotation : annotationList)
        {
            ServiceXml serviceXml = (ServiceXml) annotation;
            String fileName = serviceXml.value();
            if (ValueUtils.isEmpty(serviceXml.value()))
                fileName = serviceType.getSimpleName() + ".xml";
            ServiceFactory.getInstance().load(serviceBase, serviceType, fileName, null);
        }
    }

    /**
     * 重启服务和所有子服务。
     * @param serviceBase 服务。
     */
    public static void restart(ServiceBase serviceBase)
    {
        stop(serviceBase);
        start(serviceBase);
    }

    /**
     * 启动服务和所有子服务。
     * @param serviceBase 服务。
     */
    public static void start(ServiceBase serviceBase)
    {
        if (serviceBase instanceof ServiceControl)
        {
            ServiceControl serviceControl = (ServiceControl) serviceBase;
            if (!serviceControl.getServiceStatus().equals(ServiceStatus.Started))
                serviceControl.start();
        }
        for (Object childService : serviceBase.getChildren())
        {
            if (childService instanceof ServiceBase)
                start((ServiceBase) childService);
        }
    }

    /**
     * 停止服务和所有子服务。
     * @param serviceBase 服务。
     */
    public static void stop(ServiceBase serviceBase)
    {
        if (serviceBase instanceof ServiceControl)
        {
            ServiceControl serviceControl = (ServiceControl) serviceBase;
            serviceControl.stop();
        }
        for (Object childService : serviceBase.getChildren())
        {
            if (childService instanceof ServiceBase)
                stop((ServiceBase) childService);
        }
    }
}

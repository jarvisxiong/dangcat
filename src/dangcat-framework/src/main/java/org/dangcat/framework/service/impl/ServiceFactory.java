package org.dangcat.framework.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.io.Resource;
import org.dangcat.commons.io.ResourceLoader;
import org.dangcat.commons.timer.Timer;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceHelper;
import org.dangcat.framework.service.ServiceLocator;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.xml.ServicesXmlResolver;
import org.dom4j.DocumentException;

/**
 * 服务工厂。
 * @author dangcat
 * 
 */
public class ServiceFactory extends ServiceBase implements ServiceLocator
{
    private static final int CLEAN_PERIOD = 60 * 60 * 1000;
    private static final String DEFAULT_MODULE = ServiceInfo.DEFAULT_MODULE + "/";
    private static final String FILENAME_POSTFIX = ".servicebeans.xml";
    private static ServiceFactory instance = null;
    private static final String SERVICE_NAME = "SERVICE-CLEAN";

    public static synchronized ServiceFactory createInstance(ServiceProvider parent)
    {
        instance = new ServiceFactory(parent);
        instance.initialize();
        return instance;
    }

    public static ServiceFactory getInstance()
    {
        return instance;
    }

    public static ServiceLocator getServiceLocator()
    {
        return instance;
    }

    /** 服务信息映射表。 */
    private Map<Class<?>, ServiceInfo> serviceInfoClassMap = new LinkedHashMap<Class<?>, ServiceInfo>();
    /** 服务信息映射表。 */
    private Map<String, ServiceInfo> serviceInfoMap = new LinkedHashMap<String, ServiceInfo>();
    /** 对象池列表。 */
    private List<ServicePool> servicePoolList = new ArrayList<ServicePool>();
    /** 定时清理。 */
    private Timer timer = null;

    private ServiceFactory(ServiceProvider parent)
    {
        super(parent);
    }

    @Override
    public void addService(Class<?> classType, Object service)
    {
        if (service != null && service != this)
        {
            ServiceInfo serviceInfo = this.createServiceInfo(classType, service.getClass());
            if (serviceInfo != null)
            {
                this.addServiceInfo(serviceInfo);
                serviceInfo.setInstance(service);
            }
        }
        super.addService(classType, service);
    }

    public Object addService(ServiceInfo serviceInfo)
    {
        return this.addService(this, serviceInfo);
    }

    public Object addService(ServiceProvider parent, ServiceInfo serviceInfo)
    {
        Object service = this.createService(parent, serviceInfo);
        if (service != null)
            this.addService(serviceInfo.getAccessClassType(), service);
        return service;
    }

    private void addServiceInfo(ServiceInfo serviceInfo)
    {
        this.serviceInfoMap.put(serviceInfo.getJndiName(), serviceInfo);
        this.serviceInfoClassMap.put(serviceInfo.getAccessClassType(), serviceInfo);
    }

    public Object createService(ServiceProvider parent, ServiceInfo serviceInfo)
    {
        if (!serviceInfo.isValid())
        {
            this.logger.error("The service is not valid : " + serviceInfo);
            return null;
        }

        ServiceProvider serviceProvider = (parent == null ? this : parent);
        Object service = null;
        if (serviceInfo.isProxy())
        {
            ClassLoader classLoader = serviceInfo.getServiceClassType().getClassLoader();
            Class<?>[] interfaces = new Class[] { serviceInfo.getAccessClassType() };
            InvocationHandler invocationHandler = null;
            if (serviceInfo.isPool())
            {
                ServicePool serviceBeanPool = new ServicePool(serviceProvider, serviceInfo);
                ServicePoolInvocationHandler servicePoolInvocationHandler = new ServicePoolInvocationHandler(serviceBeanPool);
                if (servicePoolInvocationHandler.isValid())
                {
                    this.servicePoolList.add(serviceBeanPool);
                    invocationHandler = servicePoolInvocationHandler;
                }
            }
            else
            {
                ServiceInvocationHandler serviceInvocationHandler = new ServiceInvocationHandler(serviceProvider, serviceInfo);
                if (serviceInvocationHandler.isValid())
                    invocationHandler = serviceInvocationHandler;
            }
            if (invocationHandler != null)
                service = Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
        }
        else
            service = serviceInfo.createInstance(serviceProvider);

        if (service == null)
            this.logger.error("The service is not create : " + serviceInfo);
        else if (!ValueUtils.isEmpty(serviceInfo.getJndiName()))
        {
            serviceInfo.setInstance(service);
            this.addServiceInfo(serviceInfo);
        }
        return service;
    }

    public ServiceInfo createServiceInfo(Class<?> accessClassType, Class<?> serviceClassType)
    {
        ServiceInfo serviceInfo = null;
        String jndiName = ServiceUtils.readJndiName(accessClassType, serviceClassType);
        if (!ValueUtils.isEmpty(jndiName))
        {
            serviceInfo = this.serviceInfoMap.get(jndiName);
            if (serviceInfo == null)
            {
                serviceInfo = new ServiceInfo();
                serviceInfo.setJndiName(jndiName);
                serviceInfo.setAccessClassType(accessClassType);
                serviceInfo.setServiceClassType(serviceClassType);
                serviceInfo.initialize();
            }
        }
        return serviceInfo;
    }

    public Collection<String> getJndiNames(boolean includeDefault)
    {
        List<String> jndiNameList = null;
        if (this.serviceInfoMap.size() > 0)
        {
            for (String jndiName : this.serviceInfoMap.keySet())
            {
                if (!includeDefault && jndiName.startsWith(DEFAULT_MODULE))
                    continue;
                if (jndiNameList == null)
                    jndiNameList = new ArrayList<String>();
                jndiNameList.add(jndiName);
            }
            if (jndiNameList != null)
                Collections.sort(jndiNameList);
        }
        return jndiNameList;
    }

    public ServiceInfo getServiceInfo(Class<?> classType)
    {
        return this.serviceInfoClassMap.get(classType);
    }

    public ServiceInfo getServiceInfo(String jndiName)
    {
        if (ValueUtils.isEmpty(jndiName))
            return null;
        if (jndiName.indexOf("/") == -1)
            jndiName = DEFAULT_MODULE + jndiName;
        return this.serviceInfoMap.get(jndiName);
    }

    @Override
    public void initialize()
    {
        super.initialize();

        this.timer = new Timer(SERVICE_NAME, new Runnable()
        {
            @Override
            public void run()
            {
                for (ServicePool servicePool : ServiceFactory.this.servicePoolList)
                    servicePool.cleatTimeOut(CLEAN_PERIOD);
            }
        }, CLEAN_PERIOD, CLEAN_PERIOD);
        this.timer.setDaemon(true);
        this.timer.start();
    }

    /**
     * 载入配置文件里的服务设定。
     * @param classType 服务类型。
     */
    public void load(Class<?> classType)
    {
        this.load(this, classType, null, FILENAME_POSTFIX);
    }

    /**
     * 载入配置文件里的服务设定。
     * @param file 配置文件。
     */
    public void load(File file)
    {
        this.load(this, file);
    }

    /**
     * 载入配置文件里的服务设定。
     * @param parent 所属父服务。
     * @param classType 服务类型。
     * @param namePreFix 配置前缀。
     * @param namePostFix 配置后缀。
     */
    public void load(ServiceBase parent, Class<?> classType, String namePreFix, String namePostFix)
    {
        ResourceLoader resourceLoader = new ResourceLoader(classType, namePreFix, namePostFix);
        resourceLoader.load();
        try
        {
            for (Resource resource : resourceLoader.getResourceList())
            {
                this.logger.info("Load service bean config from " + resource + " by " + classType.getSimpleName());
                this.load(parent, resource.getInputStream());
            }
        }
        finally
        {
            resourceLoader.close();
        }
    }

    /**
     * 载入配置文件里的服务设定。
     * @param parent 所属父服务。
     * @param file 配置文件。
     */
    public void load(ServiceBase parent, File file)
    {
        if (file == null || !file.exists())
        {
            this.logger.warn("The service bean config file " + file + " is not exists.");
            return;
        }

        InputStream inputStream = null;
        try
        {
            inputStream = new FileInputStream(file);
            this.load(parent, inputStream);
        }
        catch (FileNotFoundException e)
        {
            this.logger.error(file, e);
        }
        finally
        {
            inputStream = FileUtils.close(inputStream);
        }
    }

    /**
     * 载入配置文件里的服务设定。
     * @param serviceBase 服务实例。
     * @param inputStream 配置输入流。
     */
    private void load(ServiceBase parent, InputStream inputStream)
    {
        try
        {
            ServicesXmlResolver servicesXmlResolver = new ServicesXmlResolver();
            servicesXmlResolver.open(inputStream);
            servicesXmlResolver.resolve();
            Collection<Object> interceptors = ServiceUtils.createInterceptors(servicesXmlResolver.getInterceptors());
            for (ServiceInfo serviceInfo : servicesXmlResolver.getServiceInfos())
            {
                serviceInfo.initialize();
                if (interceptors != null && !interceptors.isEmpty())
                    serviceInfo.addInterceptors(interceptors.toArray());

                Object service = this.createService(parent, serviceInfo);
                if (service != null)
                {
                    parent.addService(serviceInfo.getAccessClassType(), service);

                    if (service instanceof ServiceBase)
                        ((ServiceBase) service).initialize();

                    StringBuilder info = new StringBuilder();
                    StringTokenizer stringTokenizer = new StringTokenizer(serviceInfo.toString(), Environment.LINE_SEPARATOR);
                    while (stringTokenizer.hasMoreElements())
                    {
                        info.append(Environment.LINETAB_SEPARATOR);
                        info.append(stringTokenizer.nextToken());
                    }
                    this.logger.info(parent.getClass() + " load services : " + info.toString());
                }
            }
            for (ServiceInfo serviceInfo : servicesXmlResolver.getServiceInfos())
            {
                if (serviceInfo.getInstance() != null)
                    ServiceHelper.inject(parent, serviceInfo.getInstance());
            }
        }
        catch (DocumentException e)
        {
            this.logger.error(parent, e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T lookup(String jndiName)
    {
        ServiceInfo serviceInfo = this.serviceInfoMap.get(jndiName);
        return serviceInfo == null ? null : (T) serviceInfo.getInstance();
    }
}
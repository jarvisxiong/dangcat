package org.dangcat.framework.service;

import org.dangcat.framework.service.impl.ServiceFactory;

/**
 * 主服务接口。
 * @author dangcat
 * 
 */
public abstract class MainServiceBase extends ServiceBase
{
    public MainServiceBase(ServiceProvider parent)
    {
        super(parent);
    }

    @Override
    public void addService(Class<?> classType, Object service)
    {
        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        if (serviceFactory != null)
            serviceFactory.addService(classType, service);
    }
}

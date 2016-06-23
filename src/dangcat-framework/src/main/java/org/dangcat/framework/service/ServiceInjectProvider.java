package org.dangcat.framework.service;

import org.dangcat.framework.service.annotation.Service;

import java.lang.annotation.Annotation;

public class ServiceInjectProvider extends InjectProvider
{
    @Override
    public Class<? extends Annotation> getAnnotation()
    {
        return Service.class;
    }

    @Override
    protected Object getObject(ServiceProvider serviceProvider, Object serviceInstance, Class<?> accessClassType, Annotation annotation)
    {
        Service serviceAnnotation = (Service) annotation;
        Object injectService = null;
        if (!Object.class.equals(serviceAnnotation.value()))
            accessClassType = serviceAnnotation.value();
        injectService = serviceProvider.getService(accessClassType);
        if (injectService == null)
        {
            StringBuilder info = new StringBuilder();
            info.append("serviceProvider: " + serviceProvider.getClass().getName());
            info.append(", ");
            info.append("serviceInstance: " + serviceInstance.getClass().getName());
            info.append(", ");
            info.append(accessClassType);
            info.append(" is not found.");
            this.logger.warn(info);
        }
        return injectService;
    }
}

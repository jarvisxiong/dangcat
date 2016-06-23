package org.dangcat.framework;

import org.dangcat.framework.service.InjectProvider;
import org.dangcat.framework.service.ServiceProvider;

import java.lang.annotation.Annotation;

public class EntityResourceInjectProvider extends InjectProvider
{
    @Override
    public Class<? extends Annotation> getAnnotation()
    {
        return EntityResource.class;
    }

    @Override
    protected Object getObject(ServiceProvider serviceProvider, Object serviceInstance, Class<?> accessClassType, Annotation annotation)
    {
        return new EntityResourceManager(((EntityResource) annotation).value());
    }
}

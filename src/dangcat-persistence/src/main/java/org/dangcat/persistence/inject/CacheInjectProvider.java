package org.dangcat.persistence.inject;

import org.dangcat.framework.service.InjectProvider;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.persistence.annotation.Cache;
import org.dangcat.persistence.cache.EntityCache;
import org.dangcat.persistence.cache.EntityCacheManager;
import org.dangcat.persistence.cache.MemCache;

import java.lang.annotation.Annotation;

public class CacheInjectProvider extends InjectProvider
{
    @Override
    public Class<? extends Annotation> getAnnotation()
    {
        return Cache.class;
    }

    @Override
    protected Object getObject(ServiceProvider serviceProvider, Object serviceInstance, Class<?> accessClassType, Annotation annotation)
    {
        Object injectObject = null;
        Class<?> entityClassType = ((Cache) annotation).value();
        if (entityClassType != null)
        {
            if (accessClassType.equals(EntityCache.class))
                injectObject = EntityCacheManager.getInstance().getEntityCache(entityClassType);
            else if (accessClassType.equals(MemCache.class))
                injectObject = EntityCacheManager.getInstance().getMemCache(entityClassType);
        }
        return injectObject;
    }
}

package org.dangcat.persistence.resource;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.dangcat.commons.resource.ResourceManager;
import org.dangcat.commons.resource.ResourceReader;
import org.dangcat.commons.resource.Resources;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;

public class EntityResourceManager
{
    private static EntityResourceManager instance = new EntityResourceManager();

    public static EntityResourceManager getInstance()
    {
        return instance;
    }

    private EntityResourceManager()
    {
    }

    private void findEntityClassTypes(Class<?> classType, Collection<ResourceReader> resourceReaders)
    {
        if (classType == null || Object.class.equals(classType))
            return;

        resourceReaders.add(ResourceManager.getInstance().getResourceReader(classType));

        Resources resourcesAnnotation = classType.getAnnotation(Resources.class);
        if (resourcesAnnotation != null)
        {
            for (Class<?> resourceClassType : resourcesAnnotation.value())
                this.findEntityClassTypes(resourceClassType, resourceReaders);
        }
        this.findEntityClassTypes(classType.getSuperclass(), resourceReaders);
    }

    public ResourceReader getResourceReader(Class<?> classType)
    {
        ResourceReader resourceReader = ResourceManager.getInstance().getResourceReader(classType);
        if (resourceReader instanceof EntityResourceReader)
            return resourceReader;

        Collection<ResourceReader> resourceReaders = new LinkedHashSet<ResourceReader>();
        this.findEntityClassTypes(classType, resourceReaders);
        if (resourceReaders == null || resourceReaders.isEmpty())
            return null;

        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classType);
        resourceReader = new EntityResourceReader(entityMetaData, resourceReaders.toArray(new ResourceReader[0]));
        ResourceManager.getInstance().addResourceReader(classType, resourceReader);
        return resourceReader;
    }

    public ResourceReader getResourceReader(EntityMetaData entityMetaData)
    {
        return this.getResourceReader(entityMetaData.getEntityClass());
    }
}

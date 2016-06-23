package org.dangcat.persistence.entity;

import java.util.Collection;
import java.util.LinkedHashSet;

class ClassAccessSorter
{
    private RelationLink header = null;

    protected void access(Class<?> accessClassType)
    {
        if (!this.contains(accessClassType))
        {
            Collection<Class<?>> classTypeCollection = new LinkedHashSet<Class<?>>();
            this.list(classTypeCollection, accessClassType);
            for (Class<?> classType : classTypeCollection)
                this.remove(classType);
            this.append(classTypeCollection);
        }
    }

    private void append(Collection<Class<?>> classTypeCollection)
    {
        for (Class<?> classType : classTypeCollection)
        {
            RelationLink relationLink = new RelationLink(classType);
            if (this.header == null)
                this.header = relationLink;
            else
                this.header.last().next = relationLink;
        }
    }

    private boolean contains(Class<?> classType)
    {
        return this.header != null && this.header.contains(classType);
    }

    protected Collection<Class<?>> getClassTypes()
    {
        Collection<Class<?>> classTypes = new LinkedHashSet<Class<?>>();
        RelationLink relationLink = this.header;
        while (relationLink != null)
        {
            if (!classTypes.contains(relationLink.classType))
                classTypes.add(relationLink.classType);
            relationLink = relationLink.next;
        }
        return classTypes;
    }

    private void list(Collection<Class<?>> classTypeCollection, Class<?> classType)
    {
        if (classType != null)
        {
            if (!classTypeCollection.contains(classType))
                classTypeCollection.add(classType);

            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classType);
            if (entityMetaData.getRelations() != null && entityMetaData.getRelations().size() > 0)
            {
                for (Relation relation : entityMetaData.getRelations())
                    list(classTypeCollection, relation.getMemberType());
            }
        }
    }

    private void remove(Class<?> classType)
    {
        if (this.header != null)
            this.header = this.header.remove(classType);
    }
}

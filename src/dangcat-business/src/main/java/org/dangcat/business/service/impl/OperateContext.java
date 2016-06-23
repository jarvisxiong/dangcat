package org.dangcat.business.service.impl;

import org.dangcat.persistence.entity.EntityManager;

/**
 * 业务操作上下文。
 * @author dangcat
 * 
 * @param <T>
 */
public class OperateContext
{
    private Class<?> classType = null;
    private EntityManager entityManager = null;

    public Class<?> getClassType()
    {
        return classType;
    }

    protected void setClassType(Class<?> entityClass)
    {
        this.classType = entityClass;
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

    protected void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }
}

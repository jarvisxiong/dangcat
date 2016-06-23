package org.dangcat.business.validator;

import org.dangcat.business.service.impl.LoadContext;
import org.dangcat.business.service.impl.SaveContext;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.entity.EntityManager;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;
import org.dangcat.persistence.model.Range;

import java.util.List;

/**
 * 业务校验。
 * @author dangcat
 * 
 */
public abstract class BusinessValidator<T extends EntityBase>
{
    private EntityManager entityManager = null;

    /**
     * 触发删除前事件。
     * @param deleteContext 操作上下文。
     */
    public void beforeDelete(LoadContext<T> deleteContext) throws ServiceException
    {
        this.setEntityManager(deleteContext.getEntityManager());
        this.beforeDelete(deleteContext.getData());
    }

    public abstract void beforeDelete(T entity) throws ServiceException;

    /**
     * 触发存储前事件。
     * @param saveContext 操作上下文。
     */
    public void beforeSave(SaveContext<T> saveContext) throws ServiceException
    {
        this.setEntityManager(saveContext.getEntityManager());
        this.beforeSave(saveContext.getData());
    }

    public abstract void beforeSave(T entity) throws ServiceException;

    protected <K extends EntityBase> boolean checkRepeat(Class<K> classType, T entity, String fieldName, Object value)
    {
        boolean result = false;
        List<K> entityList = this.load(classType, fieldName, value);
        if (entityList != null && entityList.size() > 0)
        {
            for (K fountEntity : entityList)
            {
                if (entity.equals(fountEntity))
                    continue;
                result = true;
                break;
            }
        }
        return result;
    }

    protected <K extends EntityBase> boolean exists(Class<K> classType, String fieldName, Object value)
    {
        FilterUnit filterExpress = new FilterUnit(fieldName, FilterType.eq, value);
        List<K> entityList = this.entityManager.load(classType, filterExpress, new Range(1), null);
        return entityList != null && entityList.size() > 0;
    }

    protected <K extends EntityBase> boolean exists(Class<K> classType, String[] fieldNames, Object... values)
    {
        int index = 0;
        FilterGroup filterExpress = new FilterGroup();
        for (String fieldName : fieldNames)
            filterExpress.add(fieldName, FilterType.eq, values[index++]);
        List<K> entityList = this.entityManager.load(classType, filterExpress, new Range(1), null);
        return entityList != null && entityList.size() > 0;
    }

    protected EntityManager getEntityManager()
    {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected boolean hasPermission(Integer permissionId)
    {
        return ServiceContext.getInstance().hasPermission(permissionId);
    }

    protected <K extends EntityBase> List<K> load(Class<K> classType, String fieldName, Object value)
    {
        FilterUnit filterExpress = new FilterUnit(fieldName, FilterType.eq, value);
        return this.entityManager.load(classType, filterExpress);
    }

    protected <K extends EntityBase> List<K> load(Class<K> classType, String[] fieldNames, Object... values)
    {
        FilterGroup filterGroup = new FilterGroup();
        for (int i = 0; i < fieldNames.length; i++)
            filterGroup.add(new FilterUnit(fieldNames[i], FilterType.eq, values[i]));
        return this.entityManager.load(classType, filterGroup);
    }
}

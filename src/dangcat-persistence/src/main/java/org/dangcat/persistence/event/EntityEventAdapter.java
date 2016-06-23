package org.dangcat.persistence.event;

import org.dangcat.persistence.entity.LoadEntityContext;
import org.dangcat.persistence.entity.SaveEntityContext;
import org.dangcat.persistence.exception.EntityException;

/**
 * 实体事件管理。
 * @author dangcat
 * 
 */
public abstract class EntityEventAdapter
{
    public void afterCommit(SaveEntityContext saveEntityContext)
    {
    }

    public void afterDelete(SaveEntityContext saveEntityContext) throws EntityException
    {
    }

    public void afterLoad(LoadEntityContext loadEntityContext) throws EntityException
    {
    }

    public void afterSave(SaveEntityContext saveEntityContext) throws EntityException
    {
    }

    public boolean beforeDelete(SaveEntityContext saveEntityContext) throws EntityException
    {
        return true;
    }

    public boolean beforeLoad(LoadEntityContext loadEntityContext) throws EntityException
    {
        return true;
    }

    public boolean beforeSave(SaveEntityContext saveEntityContext) throws EntityException
    {
        return true;
    }

    public void onDeleteError(SaveEntityContext saveEntityContext, EntityException entityException)
    {
    }

    public void onLoadError(LoadEntityContext loadEntityContext, EntityException entityException)
    {
    }

    public void onSaveError(SaveEntityContext saveEntityContext, EntityException entityException)
    {
    }
}

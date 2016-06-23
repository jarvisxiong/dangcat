package org.dangcat.business.service.impl;

import org.dangcat.business.exceptions.BusinessException;
import org.dangcat.business.service.DataFilter;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.entity.*;
import org.dangcat.persistence.event.EntityEventAdapter;
import org.dangcat.persistence.exception.EntityException;

class BusinessServiceSave<Q extends EntityBase, V extends EntityBase, F extends DataFilter> extends BusinessServiceInvoker<Q, V, F>
{
    BusinessServiceSave(BusinessServiceBase<Q, V, F> businessServiceBase)
    {
        super(businessServiceBase);
    }

    /**
     * 触发存储后事件。
     * @param saveContext 操作上下文。
     */
    private void afterSave(SaveContext<V> saveContext)
    {
        if (this.isExtendEventEnabled())
        {
            if (saveContext.isInsert())
                this.businessServiceBase.afterInsert(saveContext);
            else
                this.businessServiceBase.afterSave(saveContext);
        }
    }

    protected V execute(V entity) throws ServiceException
    {
        if (logger.isDebugEnabled())
            logger.debug("Begin save the entity: " + entity.getClass());

        long beginTime = DateUtils.currentTimeMillis();
        try
        {
            EntityUtils.resetEntityBase(entity);
            EntityManager entityManager = this.getEntityManager();

            SaveEntityContext saveEntityContext = new SaveEntityContext();
            saveEntityContext.setEntityManager(entityManager);

            V originalData = saveEntityContext.loadOriginal(entity);
            final SaveContext<V> saveContext = new SaveContext<V>(this.businessServiceBase, entity, originalData);
            saveContext.setClassType(entity.getClass());
            saveContext.setEntityManager(entityManager);

            if (this.isExtendEventEnabled())
            {
                saveEntityContext.setEntityEventAdapter(new EntityEventAdapter()
                {
                    public boolean beforeSave(SaveEntityContext saveEntityContext) throws EntityException
                    {
                        return saveContext.beforeSave(saveEntityContext);
                    }
                });
            }
            entityManager.save(saveEntityContext, entity);

            if (!entity.hasError())
            {
                entityManager.refresh(entity);
                EntityUtils.resetDataState(entity);
                this.afterSave(saveContext);
            }
            EntityCalculator.calculate(entity);
            EntityUtils.sortRelation(entity);
        }
        catch (EntityException e)
        {
            logger.error(this, e);
            throw new BusinessException(BusinessException.SAVE_ERROR);
        }
        finally
        {
            if (logger.isDebugEnabled())
                logger.debug("End save entity, cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
        }
        return entity;
    }
}

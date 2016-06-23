package org.dangcat.business.service.impl;

import org.dangcat.business.exceptions.BusinessException;
import org.dangcat.business.service.DataFilter;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.entity.*;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.tablename.TableName;

import java.util.List;

class BusinessServiceView<Q extends EntityBase, V extends EntityBase, F extends DataFilter> extends BusinessServiceInvoker<Q, V, F> {
    BusinessServiceView(BusinessServiceBase<Q, V, F> businessServiceBase) {
        super(businessServiceBase);
    }

    /**
     * 触发加载数据后事件。
     *
     * @param loadContext 操作上下文。
     */
    private void afterLoad(LoadContext<V> loadContext) {
        if (this.isExtendEventEnabled())
            this.businessServiceBase.afterLoad(loadContext);
    }

    /**
     * 触发加载数据前事件。
     *
     * @param loadContext 操作上下文。
     */
    private void beforeLoad(LoadContext<V> loadContext) throws ServiceException {
        if (this.isExtendEventEnabled())
            this.businessServiceBase.beforeLoad(loadContext);
    }

    /**
     * 新建数据。
     */
    @SuppressWarnings("unchecked")
    private V createNew() throws ServiceException {
        Class<V> classType = this.getViewEntityClass();

        long beginTime = DateUtils.currentTimeMillis();
        if (this.logger.isDebugEnabled())
            this.logger.debug("Begin create the entity: " + classType);

        V entity = null;
        try {
            entity = (V) ReflectUtils.newInstance(classType);
            if (entity == null)
                throw new BusinessException(BusinessException.CREATE_ERROR);

            this.businessServiceBase.onCreate(entity);
        } catch (EntityException e) {
            this.logger.error(this, e);
            throw new BusinessException(BusinessException.CREATE_ERROR);
        } finally {
            if (this.logger.isDebugEnabled())
                this.logger.debug("End create entity, cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
        }
        return entity;
    }

    protected V execute(Object id) throws ServiceException {
        return this.execute(null, null, id);
    }

    protected V execute(TableName tableName, String sqlName, Object... primaryKeyValues) throws ServiceException {
        if (!this.isValid(primaryKeyValues))
            return this.createNew();

        Class<V> classType = this.getViewEntityClass();

        if (this.logger.isDebugEnabled())
            this.logger.debug("Begin load the entity: " + classType);

        LoadContext<V> loadContext = new LoadContext<V>(primaryKeyValues);
        loadContext.setClassType(classType);

        V entity = null;
        long beginTime = DateUtils.currentTimeMillis();
        try {
            EntityManager entityManager = this.getEntityManager();
            loadContext.setEntityManager(entityManager);

            this.beforeLoad(loadContext);

            LoadEntityContext loadEntityContext = new LoadEntityContext(classType, primaryKeyValues);
            loadEntityContext.setTableName(tableName);
            loadEntityContext.setSqlName(sqlName);
            List<V> dataList = entityManager.load(loadEntityContext);
            if (dataList == null || dataList.size() == 0)
                throw new BusinessException(BusinessException.DATA_NOTFOUND);
            entity = dataList.get(0);

            loadContext.setData(entity);
            EntityCalculator.calculate(entity);
            EntityUtils.resetDataState(entity);
            EntityUtils.sortRelation(entity);
            this.afterLoad(loadContext);
        } catch (EntityException e) {
            this.logger.error(this, e);
            throw new BusinessException(BusinessException.LOAD_ERROR, classType);
        } finally {
            if (this.logger.isDebugEnabled())
                this.logger.debug("End load entity, cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
        }
        return entity;
    }

    private boolean isValid(Object[] primaryKeyValues) {
        if (primaryKeyValues == null)
            return false;
        for (Object value : primaryKeyValues) {
            if (value == null)
                return false;
        }
        return true;
    }
}
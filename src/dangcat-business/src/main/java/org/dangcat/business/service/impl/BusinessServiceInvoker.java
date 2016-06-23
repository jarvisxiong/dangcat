package org.dangcat.business.service.impl;

import org.apache.log4j.Logger;
import org.dangcat.business.service.DataFilter;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.entity.EntityManager;

class BusinessServiceInvoker<Q extends EntityBase, V extends EntityBase, F extends DataFilter>
{
    protected BusinessServiceBase<Q, V, F> businessServiceBase = null;
    protected Logger logger = null;

    BusinessServiceInvoker(BusinessServiceBase<Q, V, F> businessServiceBase)
    {
        this.businessServiceBase = businessServiceBase;
        this.logger = businessServiceBase.logger;
    }

    protected String getDatabaseName()
    {
        return this.businessServiceBase.getDatabaseName();
    }

    protected EntityManager getEntityManager()
    {
        return this.businessServiceBase.getEntityManager();
    }

    @SuppressWarnings("unchecked")
    protected Class<Q> getQueryEntityClass() throws ServiceException
    {
        return (Class<Q>) this.businessServiceBase.getEntityClass(BusinessServiceBase.QUERY_TABLENAME);
    }

    @SuppressWarnings("unchecked")
    protected Class<V> getViewEntityClass() throws ServiceException
    {
        return (Class<V>) this.businessServiceBase.getEntityClass(BusinessServiceBase.VIEW_TABLENAME);
    }

    protected boolean isExtendEventEnabled()
    {
        return System.getProperty("business.extendevent.enabled") != "false";
    }
}

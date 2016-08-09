package org.dangcat.business.service.impl;

import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.entity.EntityUtils;
import org.dangcat.persistence.entity.SaveEntityContext;
import org.dangcat.persistence.model.DataState;

/**
 * 实体操作上下文。
 *
 * @param <T>
 * @author dangcat
 */
public class SaveContext<T extends EntityBase> extends LoadContext<T> {
    private BusinessServiceBase<?, T, ?> businessServiceBase = null;
    private T originalData = null;
    private SaveEntityContext saveEntityContext = null;
    private ServiceException serviceException = null;

    public SaveContext(BusinessServiceBase<?, T, ?> businessServiceBase, T entity, T originalData) {
        super(entity);
        this.businessServiceBase = businessServiceBase;
        this.originalData = originalData;
    }

    public boolean beforeSave(SaveEntityContext saveEntityContext) {
        this.saveEntityContext = saveEntityContext;
        try {
            if (this.isInsert())
                this.businessServiceBase.beforeInsert(this);
            else
                this.businessServiceBase.beforeSave(this);
        } catch (ServiceException e) {
            this.serviceException = e;
        }
        return this.serviceException == null && !this.getData().hasError();
    }

    public T getOriginalData() {
        return originalData;
    }

    public SaveEntityContext getSaveEntityContext() {
        return saveEntityContext;
    }

    public ServiceException getServiceException() {
        return serviceException;
    }

    public boolean isInsert() {
        return DataState.Insert.equals(EntityUtils.checkDataState(this.getData()));
    }
}

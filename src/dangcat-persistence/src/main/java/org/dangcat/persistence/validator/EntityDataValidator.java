package org.dangcat.persistence.validator;

import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.entity.EntityPending;
import org.dangcat.persistence.entity.SaveEntityContext;
import org.dangcat.persistence.validator.exception.DataValidateException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Entity数据基本校验。
 *
 * @author dangcat
 */
public class EntityDataValidator {
    private void findExceptions(Collection<Object> entityCollection, List<DataValidateException> dataValidateExceptionList) throws DataValidateException {
        if (entityCollection != null) {
            for (Object entity : entityCollection) {
                if (entity instanceof EntityBase) {
                    EntityBase entityBase = (EntityBase) entity;
                    List<ServiceException> serviceExceptionList = entityBase.getServiceExceptionList();
                    if (serviceExceptionList != null) {
                        for (ServiceException serviceException : serviceExceptionList) {
                            if (serviceException instanceof DataValidateException)
                                dataValidateExceptionList.add((DataValidateException) serviceException);
                        }
                    }
                }
            }
        }
    }

    public void validate(SaveEntityContext saveEntityContext) throws DataValidateException {
        List<DataValidateException> dataValidateExceptionList = new ArrayList<DataValidateException>();
        for (EntityPending entityPending : saveEntityContext.getEntityPendingQueue()) {
            Collection<Object> deletedCollection = entityPending.getDeletedCollection();
            if (deletedCollection != null) {
                this.validateDeleteCollection(saveEntityContext, deletedCollection);
                this.findExceptions(deletedCollection, dataValidateExceptionList);
            }

            Collection<Object> modifyCollection = entityPending.getModifiedCollection();
            if (modifyCollection != null) {
                this.validateModifyCollection(saveEntityContext, modifyCollection);
                this.findExceptions(modifyCollection, dataValidateExceptionList);
            }

            Collection<Object> insertCollection = entityPending.getInsertCollection();
            if (insertCollection != null) {
                this.validateInsertCollection(saveEntityContext, insertCollection);
                this.findExceptions(insertCollection, dataValidateExceptionList);
            }
        }

        if (dataValidateExceptionList.size() > 0)
            throw dataValidateExceptionList.get(0);
    }

    protected void validateDelete(SaveEntityContext saveEntityContext, Object entity) throws DataValidateException {
    }

    protected void validateDeleteCollection(SaveEntityContext saveEntityContext, Collection<Object> deleteCollection) throws DataValidateException {
        if (deleteCollection != null) {
            for (Object entity : deleteCollection)
                this.validateInsert(saveEntityContext, entity);
        }
    }

    protected void validateInsert(SaveEntityContext saveEntityContext, Object entity) throws DataValidateException {
    }

    protected void validateInsertCollection(SaveEntityContext saveEntityContext, Collection<Object> insertCollection) throws DataValidateException {
        if (insertCollection != null) {
            for (Object entity : insertCollection)
                this.validateInsert(saveEntityContext, entity);
        }
    }

    protected void validateModify(SaveEntityContext saveEntityContext, Object entity) throws DataValidateException {
    }

    protected void validateModifyCollection(SaveEntityContext saveEntityContext, Collection<Object> modifyCollection) throws DataValidateException {
        if (modifyCollection != null) {
            for (Object entity : modifyCollection)
                this.validateModify(saveEntityContext, entity);
        }
    }
}

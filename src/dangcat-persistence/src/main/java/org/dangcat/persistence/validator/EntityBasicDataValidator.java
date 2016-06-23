package org.dangcat.persistence.validator;

import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.entity.SaveEntityContext;
import org.dangcat.persistence.validator.exception.DataValidateException;
import org.dangcat.persistence.validator.impl.NotNullValidator;

/**
 * 实体数据基本校验。
 * @author dangcat
 * 
 */
public class EntityBasicDataValidator extends EntityDataValidator
{
    private DataValidator[] getDataValidators(Object entity)
    {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
        return entityMetaData.getTable().getColumns().getDataValidators();
    }

    @Override
    protected void validateInsert(SaveEntityContext saveEntityContext, Object entity) throws DataValidateException
    {
        DataValidator[] dataValidators = this.getDataValidators(entity);
        if (dataValidators != null)
        {
            for (DataValidator dataValidator : dataValidators)
            {
                if (dataValidator instanceof NotNullValidator)
                {
                    if (dataValidator.getColumn().isAutoIncrement() || dataValidator.getColumn().isAssociate())
                        continue;
                }
                try
                {
                    dataValidator.validate(entity);
                }
                catch (DataValidateException e)
                {
                    if (entity instanceof EntityBase)
                        ((EntityBase) entity).addServiceException(e);
                    else
                        throw e;
                }
            }
        }
    }

    @Override
    protected void validateModify(SaveEntityContext saveEntityContext, Object entity) throws DataValidateException
    {
        DataValidator[] dataValidators = this.getDataValidators(entity);
        if (dataValidators != null)
        {
            for (DataValidator dataValidator : dataValidators)
            {
                try
                {
                    dataValidator.validate(entity);
                }
                catch (DataValidateException e)
                {
                    if (entity instanceof EntityBase)
                        ((EntityBase) entity).addServiceException(e);
                    else
                        throw e;
                }
            }
        }
    }
}

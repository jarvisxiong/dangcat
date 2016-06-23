package org.dangcat.persistence.validator.impl;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.validator.DataValidator;
import org.dangcat.persistence.validator.exception.DataValidateException;

/**
 * 不可为空校验。
 *
 * @author dangcat
 */
public class NotNullValidator extends DataValidator {
    public NotNullValidator(Class<?> classType, Column column) {
        super(classType, column);
    }

    public NotNullValidator(Column column) {
        super(null, column);
    }

    private boolean isInvalid(Object value) {
        if (value == null)
            return true;

        if (value instanceof String)
            return ValueUtils.isEmpty(value);
        else if (value instanceof char[])
            return ((char[]) value).length == 0;
        else if (value instanceof Character[])
            return ((Character[]) value).length == 0;
        else if (value instanceof byte[])
            return ((byte[]) value).length == 0;
        else if (value instanceof Byte[])
            return ((Byte[]) value).length == 0;

        return false;
    }

    public void validate(Object instance) throws DataValidateException {
        Object value = this.getValue(instance);
        if (this.isInvalid(value))
            this.throwDataValidateException(DataValidateException.INVALIDATE_NOTNULL);
    }
}

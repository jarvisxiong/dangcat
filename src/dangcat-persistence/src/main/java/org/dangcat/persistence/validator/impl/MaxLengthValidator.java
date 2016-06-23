package org.dangcat.persistence.validator.impl;

import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.validator.DataValidator;
import org.dangcat.persistence.validator.exception.DataValidateException;

/**
 * 最大长度校验。
 * @author dangcat
 * 
 */
public class MaxLengthValidator extends DataValidator
{
    private int maxLength = 0;

    public MaxLengthValidator(Class<?> classType, Column column)
    {
        super(classType, column);
        this.maxLength = column.getDisplaySize();
    }

    public int getMaxLength()
    {
        return maxLength;
    }

    private boolean isInvalid(Object value)
    {
        int length = -1;
        if (value instanceof String)
            length = ((String) value).length();
        else if (value instanceof char[])
            length = ((char[]) value).length;
        else if (value instanceof Character[])
            length = ((Character[]) value).length;
        else if (value instanceof byte[])
            length = ((byte[]) value).length;
        else if (value instanceof Byte[])
            length = ((Byte[]) value).length;
        return length != -1 && length > this.getMaxLength();
    }

    public void validate(Object instance) throws DataValidateException
    {
        Object value = this.getValue(instance);
        if (this.isInvalid(value))
            this.throwDataValidateException(DataValidateException.INVALIDATE_MAXLENTH, this.getMaxLength());
    }
}

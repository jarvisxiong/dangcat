package org.dangcat.persistence.validator.impl;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.validator.DataValidator;
import org.dangcat.persistence.validator.exception.DataValidateException;

/**
 * 数值范围校验。
 * @author dangcat
 * 
 */
public class RangeValidator extends DataValidator
{
    private Object maxValue = null;
    private Object minValue = null;

    public RangeValidator(Class<?> classType, Column column)
    {
        super(classType, column);
    }

    public Object getMaxValue()
    {
        return maxValue;
    }

    public Object getMinValue()
    {
        return minValue;
    }

    private boolean isInvalid(Object value)
    {
        if (this.minValue != null && ValueUtils.compare(value, this.minValue) < 0)
            return true;

        if (this.maxValue != null && ValueUtils.compare(value, this.maxValue) > 0)
            return true;

        return false;
    }

    public void setMaxValue(Object maxValue)
    {
        this.maxValue = maxValue;
    }

    public void setMinValue(Object minValue)
    {
        this.minValue = minValue;
    }

    public void validate(Object instance) throws DataValidateException
    {
        Object value = this.getValue(instance);
        if (this.isInvalid(value))
        {
            if (this.getMinValue() != null && this.getMaxValue() != null)
                this.throwDataValidateException(DataValidateException.INVALIDATE_RANGE, this.getMinValue(), this.getMaxValue());

            if (this.getMinValue() != null)
                this.throwDataValidateException(DataValidateException.INVALIDATE_MINVALUE, this.getMinValue());

            if (this.getMaxValue() != null)
                this.throwDataValidateException(DataValidateException.INVALIDATE_MAXVALUE, this.getMaxValue());
        }
    }
}

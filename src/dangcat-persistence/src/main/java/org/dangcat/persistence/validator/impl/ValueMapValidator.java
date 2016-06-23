package org.dangcat.persistence.validator.impl;

import java.util.Map;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.validator.DataValidator;
import org.dangcat.persistence.validator.exception.DataValidateException;

/**
 * 数值范围校验。
 * @author dangcat
 * 
 */
public class ValueMapValidator extends DataValidator
{
    private Map<Integer, String> valueMap = null;

    public ValueMapValidator(Class<?> classType, Column column, Map<Integer, String> valueMap)
    {
        super(classType, column);
        this.valueMap = valueMap;
    }

    public Map<Integer, String> getValueMap()
    {
        return valueMap;
    }

    private boolean isInvalid(Object value)
    {
        if (this.valueMap != null && this.valueMap.size() > 0)
        {
            for (Object key : this.valueMap.keySet())
            {
                if (ValueUtils.compare(value, key) == 0)
                    return false;
            }
            return true;
        }
        return false;
    }

    public void validate(Object instance) throws DataValidateException
    {
        Object value = this.getValue(instance);
        if (this.isInvalid(value))
            this.throwDataValidateException(DataValidateException.INVALIDATE_OPTIONS);
    }
}

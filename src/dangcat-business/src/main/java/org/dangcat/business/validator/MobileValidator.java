package org.dangcat.business.validator;

import org.dangcat.business.validator.exceptions.LogicValidatorException;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.validator.LogicValidator;
import org.dangcat.persistence.validator.exception.DataValidateException;

/**
 * ÓÊ¼þÐ£ÑéÆ÷¡£
 * @author dangcat
 * 
 */
public class MobileValidator extends LogicValidator
{
    private static final String VALIDATOR_NAME = "Mobile";

    public MobileValidator(Class<?> classType, Column column)
    {
        super(classType, column);
    }

    @Override
    public String getName()
    {
        return VALIDATOR_NAME;
    }

    @Override
    public void validateValue(Object value) throws DataValidateException
    {
        String mobile = (String) value;
        if (!ValueUtils.isEmpty(mobile))
        {
            for (int index = 0; index < mobile.length(); index++)
            {
                char charValue = mobile.charAt(index);
                if (Character.isDigit(charValue) || charValue == '-')
                    continue;
                throw new LogicValidatorException(this.getClassType(), this.getColumn().getName(), LogicValidatorException.INVALIDATE_MOBILE);
            }
        }
    }
}

package org.dangcat.business.validator;

import org.dangcat.business.validator.exceptions.LogicValidatorException;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.validator.LogicValidator;
import org.dangcat.persistence.validator.exception.DataValidateException;

/**
 * ÕËºÅÐ£ÑéÆ÷¡£
 * @author dangcat
 * 
 */
public class NoValidator extends LogicValidator
{
    private static final String VALIDATOR_NAME = "No";

    public NoValidator(Class<?> classType, Column column)
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
        String no = (String) value;
        if (!ValueUtils.isEmpty(no))
        {
            for (int index = 0; index < no.length(); index++)
            {
                char charValue = no.charAt(index);
                if (Character.isDigit(charValue) || Character.isLetter(charValue) || charValue == '_' || charValue == '.')
                    continue;
                throw new LogicValidatorException(this.getClassType(), this.getColumn().getName(), LogicValidatorException.INVALIDATE_NO);
            }
        }
    }
}

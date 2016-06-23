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
public class TelValidator extends LogicValidator
{
    private static final String VALIDATOR_NAME = "Tel";

    public TelValidator(Class<?> classType, Column column)
    {
        super(classType, column);
    }

    @Override
    public String getName()
    {
        return VALIDATOR_NAME;
    }

    @Override
    protected void throwDataValidateException(Integer messageId, Object... params) throws DataValidateException
    {
        throw new LogicValidatorException(this.getClassType(), this.getColumn().getName(), messageId, params);
    }

    @Override
    public void validateValue(Object value) throws DataValidateException
    {
        String tel = (String) value;
        if (!ValueUtils.isEmpty(tel))
        {
            int countBracketsLeft = 0;
            int countBracketsRight = 0;
            int countPlusSign = 0;
            for (int index = 0; index < tel.length(); index++)
            {
                char charValue = tel.charAt(index);
                if (Character.isDigit(charValue) || charValue == '-')
                    continue;
                if (charValue == '+')
                {
                    countPlusSign++;
                    continue;
                }
                if (charValue == '(')
                {
                    countBracketsLeft++;
                    continue;
                }
                if (charValue == ')')
                {
                    countBracketsRight++;
                    continue;
                }
                this.throwDataValidateException(LogicValidatorException.INVALIDATE_TEL);
            }
            if (countPlusSign > 1 || countBracketsLeft != countBracketsRight || countBracketsRight > 1)
                this.throwDataValidateException(LogicValidatorException.INVALIDATE_TEL);
        }
    }
}

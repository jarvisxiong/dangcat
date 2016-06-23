package org.dangcat.business.validator;

import org.dangcat.business.validator.exceptions.LogicValidatorException;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.validator.LogicValidator;
import org.dangcat.persistence.validator.exception.DataValidateException;

public class Ipv4Validator extends LogicValidator
{
    private static final String VALIDATOR_NAME = "ipv4";

    public Ipv4Validator(Class<?> classType, Column column)
    {
        super(classType, column);
    }

    @Override
    public String getName()
    {
        return VALIDATOR_NAME;
    }

    @Override
    protected void validateValue(Object value) throws DataValidateException
    {
        String ipv4 = (String) value;
        if (!ValueUtils.isEmpty(ipv4))
        {
            String[] ipv4Section = ipv4.split(".");
            if (ipv4Section.length != 4)
                throw new LogicValidatorException(this.getClassType(), this.getColumn().getName(), LogicValidatorException.INVALIDATE_IPV4);

            for (int index = 0; index < ipv4.length(); index++)
            {
                char charValue = ipv4.charAt(index);
                if (Character.isDigit(charValue) || charValue == '.')
                    continue;
                throw new LogicValidatorException(this.getClassType(), this.getColumn().getName(), LogicValidatorException.INVALIDATE_IPV4);
            }

            for (String str : ipv4Section)
            {
                if (Integer.valueOf(str) > 0 && Integer.valueOf(str) < 255)
                    continue;
                throw new LogicValidatorException(this.getClassType(), this.getColumn().getName(), LogicValidatorException.INVALIDATE_IPV4);
            }
        }
    }
}

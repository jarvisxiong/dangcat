package org.dangcat.business.validator;

import org.dangcat.business.validator.exceptions.LogicValidatorException;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.validator.LogicValidator;
import org.dangcat.persistence.validator.exception.DataValidateException;

/**
 * 邮件校验器。
 *
 * @author dangcat
 */
public class EmailValidator extends LogicValidator {
    private static final String VALIDATOR_NAME = "Email";

    public EmailValidator(Class<?> classType, Column column) {
        super(classType, column);
    }

    @Override
    public String getName() {
        return VALIDATOR_NAME;
    }

    @Override
    public void validateValue(Object value) throws DataValidateException {
        String email = (String) value;
        if (!ValueUtils.isEmpty(email)) {
            if (email.charAt(0) == '.' || email.charAt(0) == '@' || email.indexOf('@', 0) == -1 || email.indexOf('.', 0) == -1 || email.lastIndexOf("@") == email.length() - 1
                    || email.lastIndexOf(".") == email.length() - 1)
                throw new LogicValidatorException(this.getClassType(), this.getColumn().getName(), LogicValidatorException.INVALIDATE_EMAIL);
        }
    }
}

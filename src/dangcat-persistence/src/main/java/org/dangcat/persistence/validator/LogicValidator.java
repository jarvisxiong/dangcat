package org.dangcat.persistence.validator;

import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.validator.exception.DataValidateException;

/**
 * 逻辑校验接口。
 *
 * @author dangcat
 */
public abstract class LogicValidator extends DataValidator {
    private LogicValidator extendlogicValidator = null;

    public LogicValidator(Class<?> classType, Column column) {
        super(classType, column);
    }

    public LogicValidator getExtendLogicValidator() {
        if (this.extendlogicValidator == null)
            this.extendlogicValidator = LogicValidatorUtils.getExtendLogicValidator(this.getClass(), this.getClassType(), this.getColumn());
        if (this.extendlogicValidator == null)
            this.extendlogicValidator = this;
        return this.extendlogicValidator;
    }

    public abstract String getName();

    @Override
    public void validate(Object instance) throws DataValidateException {
        if (LogicValidatorUtils.isEnabled()) {
            LogicValidator extendLogicValidator = this.getExtendLogicValidator();
            if (extendLogicValidator != null && extendLogicValidator != this)
                extendLogicValidator.validateValue(this.getValue(instance));
            else
                this.validateValue(this.getValue(instance));
        }
    }

    protected abstract void validateValue(Object value) throws DataValidateException;
}

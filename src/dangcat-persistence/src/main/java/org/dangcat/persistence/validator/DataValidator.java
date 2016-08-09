package org.dangcat.persistence.validator;

import org.dangcat.persistence.filter.FilterUtils;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.validator.exception.DataValidateException;

/**
 * 数据校验接口。
 *
 * @author dangcat
 */
public abstract class DataValidator {
    private Class<?> classType = null;
    private Column column = null;

    public DataValidator() {
    }

    public DataValidator(Class<?> classType, Column column) {
        this.classType = classType;
        this.column = column;
    }

    public Class<?> getClassType() {
        return classType;
    }

    public Column getColumn() {
        return column;
    }

    public Object getValue(Object instance) {
        return FilterUtils.getValue(instance, this.getColumn().getName());
    }

    protected void throwDataValidateException(Integer messageId, Object... params) throws DataValidateException {
        throw new DataValidateException(this.getClassType(), this.getColumn().getName(), messageId, params);
    }

    public abstract void validate(Object instance) throws DataValidateException;
}

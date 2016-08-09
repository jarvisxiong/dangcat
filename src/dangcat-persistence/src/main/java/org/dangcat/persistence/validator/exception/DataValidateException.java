package org.dangcat.persistence.validator.exception;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.entity.EntityUtils;

/**
 * 数据校验异常。
 *
 * @author dangcat
 */
public class DataValidateException extends ServiceException {
    /**
     * 最大长度校验。
     */
    public static final Integer INVALIDATE_MAXLENTH = 2;
    /**
     * 大于最大值。
     */
    public static final Integer INVALIDATE_MAXVALUE = 4;
    /**
     * 小于最小值。
     */
    public static final Integer INVALIDATE_MINVALUE = 3;
    /**
     * 不可为空。
     */
    public static final Integer INVALIDATE_NOTNULL = 1;
    /**
     * 无效选项。
     */
    public static final Integer INVALIDATE_OPTIONS = 6;
    /**
     * 数据范围不符合要求。
     */
    public static final Integer INVALIDATE_RANGE = 5;
    private static final String EXCEPTION_ENTITYTITLE = "{entityTitle}";
    private static final String EXCEPTION_FIELDTITLE = "{fieldTitle}";
    private static final long serialVersionUID = 1L;
    /**
     * 实体类型
     */
    private Class<?> classType = null;
    /**
     * 检验的字段名。
     */
    private String fieldName = null;

    public DataValidateException(Class<?> classType, String fieldName, Integer messageId, Object... params) {
        super(messageId, params);
        this.classType = classType;
        this.fieldName = fieldName;
    }

    public DataValidateException(Integer messageId, Object... params) {
        super(messageId, params);
    }

    @Override
    protected String format(String message) {
        String entityTitle = EntityUtils.getTitle(this.getClassType(), "title");
        String messageText = message.replace(EXCEPTION_ENTITYTITLE, ValueUtils.isEmpty(entityTitle) ? "" : entityTitle);
        String fieldTitle = this.getFieldTitle();
        messageText = messageText.replace(EXCEPTION_FIELDTITLE, ValueUtils.isEmpty(fieldTitle) ? "" : fieldTitle);
        return super.format(messageText);
    }

    public Class<?> getClassType() {
        return classType;
    }

    public String getFieldName() {
        return fieldName;
    }

    private String getFieldTitle() {
        if (this.getClassType() == null || this.getFieldName() == null)
            return null;
        return EntityUtils.getTitle(this.getClassType(), this.getFieldName());
    }
}

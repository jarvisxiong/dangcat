package org.dangcat.persistence.validator.exception;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.exception.ServiceInformation;
import org.dangcat.persistence.entity.EntityUtils;

/**
 * 数据校验提示。
 *
 * @author dangcat
 */
public class DataValidateInformation extends ServiceInformation {
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

    public DataValidateInformation(Class<?> classType, String fieldName, Integer messageId, Object... params) {
        super(messageId, params);
        this.classType = classType;
        this.fieldName = fieldName;
    }

    public DataValidateInformation(Integer messageId, Object... params) {
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

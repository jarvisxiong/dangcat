package org.dangcat.net.rfc.exceptions;

import org.dangcat.framework.exception.ServiceException;
import org.dangcat.net.rfc.attribute.AttributeData;

/**
 * 报文验证异常。
 *
 * @author dangcat
 */
public class ProtocolValidateException extends ServiceException {
    public static final Integer ATTRIBUTE_ERROR = 0;
    public static final Integer ATTRIBUTE_INVALID_FIXLENGTH = 9;
    public static final Integer ATTRIBUTE_INVALID_LENGTH = 1;
    public static final Integer ATTRIBUTE_INVALID_MAXVALUE = 2;
    public static final Integer ATTRIBUTE_INVALID_MINVALUE = 3;
    public static final Integer ATTRIBUTE_INVALID_OPTION = 4;
    public static final Integer ATTRIBUTE_ISNULL = 5;
    public static final Integer RULE_EXACTLYONEPRESENT = 6;
    public static final Integer RULE_MUSTNOTPRESENT = 7;
    public static final Integer RULE_ZEROORONEPRESENT = 8;
    private static final long serialVersionUID = 1L;
    /**
     * 检验的字段对象。
     */
    private AttributeData attributeData = null;
    /**
     * 检验的字段名。
     */
    private String fieldName = null;

    public ProtocolValidateException(Integer messageId, Object... params) {
        super(messageId, params);
    }

    public ProtocolValidateException(String messageKey, Object... params) {
        super(messageKey, params);
    }

    public ProtocolValidateException(Throwable cause) {
        super(cause, ATTRIBUTE_ERROR);
    }

    public AttributeData getAttributeData() {
        return attributeData;
    }

    public void setAttributeData(AttributeData attributeData) {
        this.attributeData = attributeData;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}

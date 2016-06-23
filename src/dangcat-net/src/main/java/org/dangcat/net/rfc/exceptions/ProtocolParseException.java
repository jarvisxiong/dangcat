package org.dangcat.net.rfc.exceptions;

import org.dangcat.framework.exception.ServiceException;

/**
 * 报文解析异常。
 * @author dangcat
 * 
 */
public class ProtocolParseException extends ServiceException
{
    public static final Integer ATTRIBUTE_ERROR = 7;
    public static final Integer ATTRIBUTE_INVALID_CLASSTYPE = 5;
    public static final Integer ATTRIBUTE_NOTSUPPORT = 6;
    public static final Integer ATTRIBUTE_TYPEERROR = 8;
    public static final Integer PACKET_ERROR = 1;
    public static final Integer PACKET_INVALID_CODE = 2;
    public static final Integer PACKET_INVALID_LENGTH = 3;
    public static final Integer PACKET_INVALID_MINLENGTH = 4;
    private static final long serialVersionUID = 1L;
    /** 检验的字段名。 */
    private String fieldName = null;

    public ProtocolParseException(Integer messageId, Object... params)
    {
        super(messageId, params);
    }

    public ProtocolParseException(String messageKey, Object... params)
    {
        super(messageKey, params);
    }

    public ProtocolParseException(Throwable cause)
    {
        super(cause, PACKET_ERROR);
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }
}

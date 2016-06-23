package org.dangcat.business.validator.exceptions;

import org.dangcat.persistence.validator.exception.DataValidateException;

/**
 * 逻辑校验异常。
 * @author dangcat
 * 
 */
public class LogicValidatorException extends DataValidateException
{
    /** 邮件输入不正确。 */
    public static final Integer INVALIDATE_EMAIL = 101;
    /** wrong ipv4 format */
    public static final Integer INVALIDATE_IPV4 = 104;
    /** wrong ipv6 format */
    public static final Integer INVALIDATE_IPV6 = 105;
    /** 手机输入不正确。 */
    public static final Integer INVALIDATE_MOBILE = 103;
    /** 账号输入不正确。 */
    public static final Integer INVALIDATE_NO = 100;
    /** 电话输入不正确。 */
    public static final Integer INVALIDATE_TEL = 102;
    private static final long serialVersionUID = 1L;

    public LogicValidatorException(Class<?> classType, String fieldName, Integer messageId, Object... params)
    {
        super(classType, fieldName, messageId, params);
    }
}

package org.dangcat.boot.security.exceptions;

import org.dangcat.framework.exception.ServiceException;

public class SecurityLoginException extends ServiceException {
    public static final String FIELDNAME_LICENSE = "license";
    public static final String FIELDNAME_NO = "no";
    public static final String FIELDNAME_PASSWORD = "password";

    /**
     * 请先登录系统。
     */
    public static final Integer INVALID_LOGIN = 405;
    /**
     * 密码不正确。
     */
    public static final Integer INVALID_PASSWORD = 402;
    /**
     * 登陆角色不存在。
     */
    public static final Integer INVALID_ROLE = 403;
    /**
     * license未授权或已过期
     */
    public static final Integer LICENSE_INVALIDATE = 408;
    /**
     * 用户不能为空。
     */
    public static final Integer NO_EMPTY = 401;
    /**
     * 账户已过期。
     */
    public static final Integer NO_EXPIRYTIME = 407;
    /**
     * 账户不存在。
     */
    public static final Integer NO_NOT_EXISTS = 404;
    /**
     * 账户已停用。
     */
    public static final Integer NO_USEABLE_FALSE = 406;
    /**
     * 密码不能为空。
     */
    public static final Integer PASSWORD_EMPTY = 400;
    private static final long serialVersionUID = 1L;
    private String fieldName = null;

    public SecurityLoginException(Integer messageId, Object... params) {
        super(messageId, params);
    }

    public SecurityLoginException(String fieldName, Integer messageId, Object... params) {
        super(messageId, params);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return this.fieldName;
    }
}

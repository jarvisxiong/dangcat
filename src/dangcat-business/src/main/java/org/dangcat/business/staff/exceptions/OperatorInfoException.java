package org.dangcat.business.staff.exceptions;

import org.dangcat.business.exceptions.BusinessException;
import org.dangcat.business.staff.domain.OperatorInfoCreate;

/**
 * The service exception for Operator.
 *@author dangcat
 * 
 */
public class OperatorInfoException extends BusinessException
{
    /** 只能新增本组和子组成员信息。 */
    public static final Integer INSERT_VALIDATEGROUP = 103;
    /** 操作员不能删除自己的账号。 */
    public static final Integer KILL_HIMSELFUL = 100;
    /** 只能删除本组和子组成员账号。 */
    public static final Integer KILL_VALIDATEGROUP = 101;
    /** 不能修改操作员账号。 */
    public static final Integer MODIFY_NO_DENY = 106;
    /** 只能修改本组和子组成员信息。 */
    public static final Integer MODIFY_VALIDATEGROUP = 102;
    /** 密码无效。 */
    public static final Integer PASSWORD_INVALID = 105;
    /** 两次输入的密码不一致。 */
    public static final Integer PASSWORD_NOTEQUALS = 104;

    private static final long serialVersionUID = 1L;

    public OperatorInfoException(Integer messageId, Object... params)
    {
        super(messageId, params);
    }

    public OperatorInfoException(String fieldName, Integer messageId, Object... params)
    {
        super(OperatorInfoCreate.class, fieldName, messageId, params);
    }
}

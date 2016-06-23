package org.dangcat.business.staff.exceptions;

import org.dangcat.business.exceptions.BusinessException;
import org.dangcat.business.staff.domain.RoleInfo;

/**
 * The service exception for Role.
 *@author dangcat
 * 
 */
public class RoleInfoException extends BusinessException
{
    /** 已经绑定操作员的角色不能删除。 */
    public static final Integer CHILE_OPERATOR_EXISTS = 100;
    private static final long serialVersionUID = 1L;

    public RoleInfoException(Integer messageId, Object... params)
    {
        super(messageId, params);
    }

    public RoleInfoException(String fieldName, Integer messageId, Object... params)
    {
        super(RoleInfo.class, fieldName, messageId, params);
    }
}

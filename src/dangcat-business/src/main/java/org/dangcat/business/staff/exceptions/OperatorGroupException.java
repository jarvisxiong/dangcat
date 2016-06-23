package org.dangcat.business.staff.exceptions;

import org.dangcat.business.exceptions.BusinessException;
import org.dangcat.business.staff.domain.OperatorGroup;

/**
 * The service exception for OperatorGroup.
 *@author dangcat
 * 
 */
public class OperatorGroupException extends BusinessException
{
    /** 所属操作员组不能循环绑定。 */
    public static final Integer BIND_CYCLING = 100;
    /** 已经绑定操作员的组不能删除。 */
    public static final Integer CHILE_OPERATOR_EXISTS = 101;
    /** 不能删除父操作员组。 */
    public static final Integer DELETE_DENY_FOR_ISPARENT = 103;
    /** 只能删除子成员组。 */
    public static final Integer DELETE_DENY_FOR_NOTMEMBER = 102;
    /** 只能修改本组或子成员组。 */
    public static final Integer MODIFY_DENY_FOR_NOTMEMBER = 104;
    private static final long serialVersionUID = 1L;

    public OperatorGroupException(Integer messageId, Object... params)
    {
        super(messageId, params);
    }

    public OperatorGroupException(String fieldName, Integer messageId, Object... params)
    {
        super(OperatorGroup.class, fieldName, messageId, params);
    }
}

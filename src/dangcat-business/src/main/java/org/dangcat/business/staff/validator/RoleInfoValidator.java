package org.dangcat.business.staff.validator;

import org.dangcat.business.staff.domain.OperatorInfo;
import org.dangcat.business.staff.domain.RoleInfo;
import org.dangcat.business.staff.exceptions.RoleInfoException;
import org.dangcat.business.validator.BusinessValidator;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.exception.ServiceException;

public class RoleInfoValidator extends BusinessValidator<RoleInfo>
{
    @Override
    public void beforeDelete(RoleInfo roleInfo) throws ServiceException
    {
        // 已经绑定操作员的角色不能删除。
        if (this.exists(OperatorInfo.class, OperatorInfo.RoleId, roleInfo.getId()))
            throw new RoleInfoException(RoleInfoException.CHILE_OPERATOR_EXISTS);
    }

    @Override
    public void beforeSave(RoleInfo roleInfo) throws ServiceException
    {
        this.validateExists(roleInfo);
        if (!roleInfo.hasError())
            this.validateName(roleInfo);
    }

    private void validateExists(RoleInfo roleInfo)
    {
        if (roleInfo.getId() != null)
        {
            // 角色必须存在。
            if (!this.exists(RoleInfo.class, RoleInfo.Id, roleInfo.getId()))
                roleInfo.addServiceException(new RoleInfoException(RoleInfo.Name, RoleInfoException.DATA_NOTEXISTS));
        }
    }

    private void validateName(RoleInfo roleInfo)
    {
        if (!ValueUtils.isEmpty(roleInfo.getName()))
        {
            // 操作员组的名称不能重复。
            if (this.checkRepeat(RoleInfo.class, roleInfo, RoleInfo.Name, roleInfo.getName()))
                roleInfo.addServiceException(new RoleInfoException(RoleInfo.Name, RoleInfoException.DATA_REPEAT));
        }
    }
}

package org.dangcat.business.staff.validator;

import org.dangcat.boot.security.SecurityUtils;
import org.dangcat.business.staff.domain.OperatorGroup;
import org.dangcat.business.staff.domain.OperatorInfo;
import org.dangcat.business.staff.domain.OperatorInfoCreate;
import org.dangcat.business.staff.domain.RoleInfo;
import org.dangcat.business.staff.exceptions.OperatorInfoException;
import org.dangcat.business.staff.service.impl.OperatorGroupLoader;
import org.dangcat.business.staff.service.impl.OperatorInfoPermissionProvider;
import org.dangcat.business.validator.BusinessValidator;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.ServiceContext;

import java.util.Map;

public class OperatorInfoValidator extends BusinessValidator<OperatorInfo> {
    @Override
    public void beforeDelete(OperatorInfo operatorInfo) throws ServiceException {
        // 操作员不能删除自己的账号。
        if (this.validateOperatorInfo(operatorInfo))
            throw new OperatorInfoException(OperatorInfoException.KILL_HIMSELFUL);

        // 只能删除本组和子组成员账号。
        if (!this.validateOperatorGroup(operatorInfo))
            throw new OperatorInfoException(OperatorInfoException.KILL_VALIDATEGROUP);
    }

    @Override
    public void beforeSave(OperatorInfo operatorInfo) throws ServiceException {
        OperatorInfo oldOperatorInfo = null;
        if (operatorInfo.getId() != null)
            oldOperatorInfo = this.getEntityManager().load(OperatorInfo.class, operatorInfo.getId());
        this.validateNo(operatorInfo, oldOperatorInfo);
        this.validateExists(operatorInfo);

        if (operatorInfo.getId() == null) {
            // 只能新增本组和子组成员信息。
            if (!this.validateOperatorGroup(operatorInfo))
                operatorInfo.addServiceException(new OperatorInfoException(OperatorInfo.GroupId, OperatorInfoException.INSERT_VALIDATEGROUP));
            this.validatePassword(operatorInfo);
        } else {
            this.validateAdvenedModify(operatorInfo, oldOperatorInfo);
            // 只能修改本组和子组成员信息。
            if (!this.validateOperatorGroup(oldOperatorInfo))
                operatorInfo.addServiceException(new OperatorInfoException(OperatorInfo.GroupId, OperatorInfoException.MODIFY_VALIDATEGROUP));
        }
    }

    private OperatorGroupLoader getOperatorGroupLoader() {
        OperatorGroupLoader operatorGroupLoader = null;
        ServiceContext serviceContext = ServiceContext.getInstance();
        if (serviceContext != null) {
            operatorGroupLoader = serviceContext.getParam(OperatorGroupLoader.class);
            if (operatorGroupLoader == null) {
                operatorGroupLoader = new OperatorGroupLoader(this.getEntityManager());
                serviceContext.addParam(OperatorGroupLoader.class, operatorGroupLoader);
            }
        }
        return operatorGroupLoader;
    }

    /**
     * 特殊权限才能修改的栏位。
     */
    private void validateAdvenedModify(OperatorInfo operatorInfo, OperatorInfo oldOperatorInfo) {
        if (!this.hasPermission(OperatorInfoPermissionProvider.ADVANCEDMODIFY)) {
            // 操作员组、角色、可用否、过期时间必须有特殊权才可以修改。
            if (!operatorInfo.getGroupId().equals(oldOperatorInfo.getGroupId()))
                operatorInfo.addServiceException(new OperatorInfoException(OperatorInfo.GroupId, OperatorInfoException.ADVANCED_MODIFY));
            if (!operatorInfo.getRoleId().equals(oldOperatorInfo.getRoleId()))
                operatorInfo.addServiceException(new OperatorInfoException(OperatorInfo.RoleId, OperatorInfoException.ADVANCED_MODIFY));
            if (!operatorInfo.getUseAble().equals(oldOperatorInfo.getUseAble()))
                operatorInfo.addServiceException(new OperatorInfoException(OperatorInfo.UseAble, OperatorInfoException.ADVANCED_MODIFY));
            if (!operatorInfo.getExpiryTime().equals(oldOperatorInfo.getExpiryTime()))
                operatorInfo.addServiceException(new OperatorInfoException(OperatorInfo.ExpiryTime, OperatorInfoException.ADVANCED_MODIFY));
        }
    }

    private void validateExists(OperatorInfo operatorInfo) {
        // 操作员组不存在。
        if (!this.exists(OperatorGroup.class, OperatorGroup.Id, operatorInfo.getGroupId()))
            operatorInfo.addServiceException(new OperatorInfoException(OperatorInfo.GroupId, OperatorInfoException.FIELD_NOTEXISTS));
        // 角色不存在。
        if (!this.exists(RoleInfo.class, RoleInfo.Id, operatorInfo.getRoleId()))
            operatorInfo.addServiceException(new OperatorInfoException(OperatorInfo.RoleId, OperatorInfoException.FIELD_NOTEXISTS));
    }

    private void validateNo(OperatorInfo operatorInfo, OperatorInfo oldOperatorInfo) {
        if (oldOperatorInfo == null) {
            // 操作员的账号不能重复。
            if (this.checkRepeat(OperatorInfo.class, operatorInfo, OperatorInfo.No, operatorInfo.getNo()))
                operatorInfo.addServiceException(new OperatorInfoException(OperatorInfo.No, OperatorInfoException.DATA_REPEAT));
        } else {
            // 不能修改操作员账号。
            if (!oldOperatorInfo.getNo().equals(operatorInfo.getNo()))
                operatorInfo.addServiceException(new OperatorInfoException(OperatorInfo.No, OperatorInfoException.MODIFY_NO_DENY));
        }
    }

    private boolean validateOperatorGroup(OperatorInfo operatorInfo) {
        OperatorGroupLoader operatorGroupLoader = this.getOperatorGroupLoader();
        OperatorInfo loginOperatorInfo = operatorGroupLoader.getLoginOperatorInfo();
        if (loginOperatorInfo == null || operatorInfo.getGroupId().equals(loginOperatorInfo.getGroupId()))
            return true;

        Map<Integer, String> operatorGroupMap = operatorGroupLoader.loadMembers(null);
        return operatorGroupMap != null && operatorGroupMap.containsKey(operatorInfo.getGroupId());
    }

    private boolean validateOperatorInfo(OperatorInfo operatorInfo) {
        OperatorGroupLoader operatorGroupLoader = this.getOperatorGroupLoader();
        OperatorInfo loginOperatorInfo = operatorGroupLoader.getLoginOperatorInfo();
        if (loginOperatorInfo == null)
            return false;
        return operatorInfo.getId().equals(loginOperatorInfo.getId());
    }

    private void validatePassword(OperatorInfo operatorInfo) {
        if (operatorInfo instanceof OperatorInfoCreate) {
            OperatorInfoCreate operatorInfoCreate = (OperatorInfoCreate) operatorInfo;
            String password1 = SecurityUtils.decryptContent(operatorInfoCreate.getPassword1());
            String password2 = SecurityUtils.decryptContent(operatorInfoCreate.getPassword2());
            if (password1 == null)
                operatorInfo.addServiceException(new OperatorInfoException(OperatorInfoCreate.Password1, OperatorInfoException.PASSWORD_INVALID));
            else if (!password1.equals(password2))
                operatorInfo.addServiceException(new OperatorInfoException(OperatorInfoCreate.Password2, OperatorInfoException.PASSWORD_NOTEQUALS));
            else
                operatorInfoCreate.setPassword(password1);
        }
    }
}

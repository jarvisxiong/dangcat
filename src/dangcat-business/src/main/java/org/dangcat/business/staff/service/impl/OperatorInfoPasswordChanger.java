package org.dangcat.business.staff.service.impl;

import org.dangcat.boot.security.SecurityUtils;
import org.dangcat.business.staff.domain.OperatorInfo;
import org.dangcat.business.staff.domain.OperatorInfoCreate;
import org.dangcat.business.staff.exceptions.OperatorInfoException;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.persistence.entity.EntityManager;

import java.util.List;

class OperatorInfoPasswordChanger
{
    private EntityManager entityManager = null;

    OperatorInfoPasswordChanger(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    protected OperatorInfo execute(String orgPassword, String newPassword) throws OperatorInfoException
    {
        // 原始密码不能为空
        String password1 = SecurityUtils.decryptContent(orgPassword);
        if (ValueUtils.isEmpty(password1))
            throw new OperatorInfoException(OperatorInfoCreate.Password, OperatorInfoException.INVALIDATE_NOTNULL);

        String no = ServiceContext.getInstance().getServicePrincipal().getNo();
        OperatorInfoCreate operatorInfo = this.load(SecurityUtils.encryptContent(no), newPassword);

        // 原始密码匹配
        if (!SecurityUtils.isMatch(operatorInfo.getPassword(), password1))
            throw new OperatorInfoException(OperatorInfoCreate.Password, OperatorInfoException.PASSWORD_INVALID);

        String password2 = SecurityUtils.decryptContent(newPassword);
        operatorInfo.setPassword(password2);
        operatorInfo.setPassword1(password2);
        operatorInfo.setPassword2(password2);
        return operatorInfo;
    }

    private OperatorInfoCreate load(String no, String password) throws OperatorInfoException
    {
        // 账号不能为空。
        String operatorNo = SecurityUtils.decryptContent(no);
        if (ValueUtils.isEmpty(operatorNo))
            throw new OperatorInfoException(OperatorInfo.No, OperatorInfoException.INVALIDATE_NOTNULL);

        // 新密码不能为空
        String newPassword = SecurityUtils.decryptContent(password);
        if (ValueUtils.isEmpty(newPassword))
            throw new OperatorInfoException(OperatorInfoCreate.Password1, OperatorInfoException.INVALIDATE_NOTNULL);

        // 账号必须存在
        List<OperatorInfoCreate> operatorInfoList = this.entityManager.load(OperatorInfoCreate.class, new String[] { OperatorInfoCreate.No }, operatorNo);
        if (operatorInfoList == null || operatorInfoList.size() == 0)
            throw new OperatorInfoException(OperatorInfo.No, OperatorInfoException.DATA_NOTEXISTS);

        return operatorInfoList.get(0);
    }

    protected OperatorInfo reset(String no, String password) throws OperatorInfoException
    {
        OperatorInfoCreate operatorInfo = this.load(no, password);
        String newPassword = SecurityUtils.decryptContent(password);
        operatorInfo.setPassword(newPassword);
        operatorInfo.setPassword1(newPassword);
        operatorInfo.setPassword2(newPassword);
        return operatorInfo;
    }
}

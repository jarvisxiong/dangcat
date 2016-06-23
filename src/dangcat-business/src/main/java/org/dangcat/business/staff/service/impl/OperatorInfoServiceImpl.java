package org.dangcat.business.staff.service.impl;

import org.dangcat.business.annotation.BusinessValidator;
import org.dangcat.business.service.impl.BusinessServiceBase;
import org.dangcat.business.staff.config.StaffConfig;
import org.dangcat.business.staff.config.StaffSetup;
import org.dangcat.business.staff.domain.OperatorInfo;
import org.dangcat.business.staff.domain.OperatorInfoBase;
import org.dangcat.business.staff.domain.OperatorInfoCreate;
import org.dangcat.business.staff.exceptions.OperatorInfoException;
import org.dangcat.business.staff.filter.OperatorInfoFilter;
import org.dangcat.business.staff.service.OperatorInfoService;
import org.dangcat.business.staff.validator.OperatorInfoValidator;
import org.dangcat.commons.resource.Resources;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.annotation.ConfigProvider;
import org.dangcat.framework.service.annotation.PermissionProvider;

import java.util.Collection;
import java.util.Date;

/**
 * The service implements for Operator.
 *
 * @author dangcat
 */
@ConfigProvider(StaffConfig.class)
@Resources({OperatorInfoException.class})
@BusinessValidator(OperatorInfoValidator.class)
@PermissionProvider(OperatorInfoPermissionProvider.class)
public class OperatorInfoServiceImpl extends BusinessServiceBase<OperatorInfo, OperatorInfo, OperatorInfoFilter> implements OperatorInfoService {
    public OperatorInfoServiceImpl(ServiceProvider parent) {
        super(parent);
    }

    @Override
    public boolean changePassword(String orgPassword, String newPassword) throws ServiceException {
        OperatorInfoPasswordChanger operatorInfoPasswordChanger = new OperatorInfoPasswordChanger(this.getEntityManager());
        OperatorInfo operatorInfo = operatorInfoPasswordChanger.execute(orgPassword, newPassword);
        this.save(operatorInfo);
        return true;
    }

    @Override
    public StaffSetup config(StaffSetup staffSetup) throws ServiceException {
        return (StaffSetup) super.config(staffSetup);
    }

    @Override
    public OperatorInfo create(OperatorInfoCreate operatorInfo) throws ServiceException {
        operatorInfo.setRegisterTime(DateUtils.now());
        return super.create(operatorInfo);
    }

    @Override
    protected OperatorInfoFilter filter(OperatorInfoFilter operatorInfoFilter) throws ServiceException {
        OperatorGroupLoader operatorGroupLoader = new OperatorGroupLoader(this.getEntityManager());
        Integer[] groupIds = operatorGroupLoader.loadMemberIds();
        if (groupIds != null) {
            if (operatorInfoFilter == null)
                operatorInfoFilter = new OperatorInfoFilter();
            operatorInfoFilter.setGroupIds(groupIds);
        }
        return operatorInfoFilter;
    }

    @Override
    protected void onCreate(OperatorInfo operatorInfo) {
        StaffConfig operatorInfoConfig = StaffConfig.getInstance();
        operatorInfo.setUseAble(operatorInfoConfig.getDefaultUseAble());
        if (operatorInfoConfig.getValidDays() != null && operatorInfoConfig.getValidDays() > 0) {
            Date expiryTime = DateUtils.add(DateUtils.DAY, DateUtils.now(), operatorInfoConfig.getValidDays());
            operatorInfo.setExpiryTime(expiryTime);
        }
        super.onCreate(operatorInfo);
    }

    @Override
    public Collection<OperatorInfoBase> pick(OperatorInfoFilter operatorInfoFilter) throws ServiceException {
        return super.pick(OperatorInfoBase.class, operatorInfoFilter);
    }

    @Override
    public boolean resetPassword(String no, String password) throws ServiceException {
        OperatorInfoPasswordChanger operatorInfoPasswordChanger = new OperatorInfoPasswordChanger(this.getEntityManager());
        OperatorInfo operatorInfo = operatorInfoPasswordChanger.reset(no, password);
        this.save(operatorInfo);
        return true;
    }
}

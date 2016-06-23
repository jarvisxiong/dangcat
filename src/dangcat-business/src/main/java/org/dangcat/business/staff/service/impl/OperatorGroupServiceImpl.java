package org.dangcat.business.staff.service.impl;

import org.dangcat.business.annotation.BusinessValidator;
import org.dangcat.business.security.BusinessPermissionProvider;
import org.dangcat.business.service.impl.BusinessServiceBase;
import org.dangcat.business.staff.domain.OperatorGroup;
import org.dangcat.business.staff.domain.OperatorGroupBase;
import org.dangcat.business.staff.exceptions.OperatorGroupException;
import org.dangcat.business.staff.filter.OperatorGroupFilter;
import org.dangcat.business.staff.service.OperatorGroupService;
import org.dangcat.business.staff.validator.OperatorGroupValidator;
import org.dangcat.commons.resource.Resources;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.annotation.PermissionProvider;

import java.util.Collection;
import java.util.Map;

/**
 * The service implements for OperatorGroup.
 * @author dangcat
 * 
 */
@Resources( { OperatorGroupException.class })
@BusinessValidator(OperatorGroupValidator.class)
@PermissionProvider(BusinessPermissionProvider.class)
public class OperatorGroupServiceImpl extends BusinessServiceBase<OperatorGroup, OperatorGroup, OperatorGroupFilter> implements OperatorGroupService
{
    public OperatorGroupServiceImpl(ServiceProvider parent)
    {
        super(parent);
    }

    @Override
    protected OperatorGroupFilter filter(OperatorGroupFilter operatorGroupFilter) throws ServiceException
    {
        OperatorGroupLoader operatorGroupLoader = new OperatorGroupLoader(this.getEntityManager());
        Integer[] groupIds = operatorGroupLoader.loadMemberIds();
        if (groupIds != null)
        {
            if (operatorGroupFilter == null)
                operatorGroupFilter = new OperatorGroupFilter();
            operatorGroupFilter.setChildren(groupIds);
        }
        return operatorGroupFilter;
    }

    @Override
    public Map<Integer, String> loadMembers() throws ServiceException
    {
        return new OperatorGroupLoader(this.getEntityManager()).loadMembers(null);
    }

    @Override
    public Collection<OperatorGroupBase> pick(OperatorGroupFilter operatorGroupFilter) throws ServiceException
    {
        return super.pick(OperatorGroupBase.class, operatorGroupFilter);
    }
}

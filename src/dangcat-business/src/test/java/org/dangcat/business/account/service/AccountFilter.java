package org.dangcat.business.account.service;

import org.dangcat.business.domain.AccountInfo;
import org.dangcat.business.service.DataFilter;
import org.dangcat.commons.serialize.annotation.Serialize;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;

/**
 * 账户查询条件。
 * @author dangcat
 * 
 */
public class AccountFilter extends DataFilter
{
    @Column(index = 2, isNullable = false)
    private Integer groupId;

    @Column(index = 1, displaySize = 20)
    private String name = null;

    @Override
    @Serialize(ignore = true)
    public FilterExpress getFilterExpress()
    {
        FilterGroup filterGroup = new FilterGroup();
        if (this.getGroupId() != null)
            filterGroup.add(new FilterUnit(AccountInfo.GroupId, FilterType.eq, this.getGroupId()));
        if (!ValueUtils.isEmpty(this.getName()))
            filterGroup.add(new FilterUnit(AccountInfo.Name, FilterType.like, this.getName()));
        return this.getFilterExpress(filterGroup);
    }

    public Integer getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}

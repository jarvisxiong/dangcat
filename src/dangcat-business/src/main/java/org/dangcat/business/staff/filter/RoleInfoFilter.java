package org.dangcat.business.staff.filter;

import org.dangcat.business.service.DataFilter;
import org.dangcat.business.staff.domain.RoleBasic;
import org.dangcat.commons.serialize.annotation.Serialize;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;

/**
 * The service filter for Role.
 * @author dangcat
 * 
 */
@Table("RoleInfo")
public class RoleInfoFilter extends DataFilter
{
    @Column(index = 1, displaySize = 20)
    private String name = null;

    @Override
    @Serialize(ignore = true)
    public FilterExpress getFilterExpress()
    {
        FilterGroup filterGroup = new FilterGroup();
        if (!ValueUtils.isEmpty(this.getName()))
            filterGroup.add(new FilterUnit(RoleBasic.Name, FilterType.like, this.getName()));
        return this.getFilterExpress(filterGroup);
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

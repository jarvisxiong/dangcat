package org.dangcat.business.staff.filter;

import org.dangcat.business.service.DataFilter;
import org.dangcat.business.staff.domain.OperatorGroup;
import org.dangcat.business.staff.picker.OperatorGroupPicker;
import org.dangcat.commons.serialize.annotation.Serialize;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;
import org.dangcat.web.annotation.Picker;

/**
 * The service filter for OperatorGroup.
 * @author dangcat
 * 
 */
@Table("OperatorGroup")
public class OperatorGroupFilter extends DataFilter
{
    private Integer[] children = null;
    @Column(index = 0, displaySize = 20)
    @Picker(value = OperatorGroupPicker.class, valueField = "name")
    private String name = null;
    @Column(index = 3, displaySize = 20)
    @Picker(OperatorGroupPicker.class)
    private Integer parentId = null;

    @Serialize(ignore = true)
    public Integer[] getChildren()
    {
        return children;
    }

    @Override
    @Serialize(ignore = true)
    public FilterExpress getFilterExpress()
    {
        FilterGroup filterGroup = new FilterGroup();
        if (!ValueUtils.isEmpty(this.getName()))
            filterGroup.add(new FilterUnit(OperatorGroup.Name, FilterType.like, this.getName()));
        if (this.getParentId() != null)
            filterGroup.add(new FilterUnit(OperatorGroup.ParentId, FilterType.eq, this.getParentId()));
        if (this.getChildren() != null)
            filterGroup.add(new FilterUnit(OperatorGroup.Id, FilterType.eq, (Object[]) this.getChildren()));
        return this.getFilterExpress(filterGroup);
    }

    public String getName()
    {
        return name;
    }

    public Integer getParentId()
    {
        return parentId;
    }

    public void setChildren(Integer[] children)
    {
        this.children = children;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setParentId(Integer parentId)
    {
        this.parentId = parentId;
    }
}

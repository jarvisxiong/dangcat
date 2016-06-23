package org.dangcat.business.staff.filter;

import org.dangcat.business.service.DataFilter;
import org.dangcat.business.staff.domain.OperatorInfo;
import org.dangcat.business.staff.picker.OperatorGroupPicker;
import org.dangcat.business.staff.picker.OperatorInfoPicker;
import org.dangcat.commons.serialize.annotation.Serialize;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;
import org.dangcat.web.annotation.Picker;
import org.dangcat.web.annotation.ValueMap;

/**
 * The service filter for Operator.
 *
 * @author dangcat
 */
@Table("OperatorInfo")
public class OperateLogFilter extends DataFilter {
    @Column(index = 2, displaySize = 20)
    @Picker(OperatorGroupPicker.class)
    private Integer groupId = null;

    private Integer[] groupIds = null;

    @Column(index = 3, displaySize = 20, isNullable = false)
    @ValueMap(jndiName = "System/SystemInfo/loadParamMap?name=Month")
    private Integer month = null;

    @Column(index = 1, displaySize = 20)
    @Picker(value = OperatorInfoPicker.class, valueField = "name", canBeUnknownValue = true)
    private String name = null;

    @Column(index = 0, displaySize = 20)
    @Picker(value = OperatorInfoPicker.class, valueField = "name", displayField = "no", canBeUnknownValue = true)
    private String no = null;

    @Override
    @Serialize(ignore = true)
    public FilterExpress getFilterExpress() {
        FilterGroup filterGroup = new FilterGroup();
        if (!ValueUtils.isEmpty(this.getNo()))
            filterGroup.add(new FilterUnit(OperatorInfo.No, FilterType.like, this.getNo()));
        if (!ValueUtils.isEmpty(this.getName()))
            filterGroup.add(new FilterUnit("OperatorInfo." + OperatorInfo.Name, FilterType.like, this.getName()));
        if (this.getGroupId() != null)
            filterGroup.add(new FilterUnit(OperatorInfo.GroupId, FilterType.eq, this.getGroupId()));
        else if (this.getGroupIds() != null)
            filterGroup.add(new FilterUnit(OperatorInfo.GroupId, FilterType.eq, (Object[]) this.getGroupIds()));
        return this.getFilterExpress(filterGroup);
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Serialize(ignore = true)
    public Integer[] getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(Integer[] groupIds) {
        this.groupIds = groupIds;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }
}

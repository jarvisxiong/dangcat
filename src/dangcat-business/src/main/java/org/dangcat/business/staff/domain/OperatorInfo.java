package org.dangcat.business.staff.domain;

import java.util.Date;

import org.dangcat.business.staff.picker.OperatorGroupPicker;
import org.dangcat.business.staff.service.impl.OperatorInfoCalculator;
import org.dangcat.business.validator.MobileValidator;
import org.dangcat.commons.formator.DateType;
import org.dangcat.commons.formator.annotation.DateStyle;
import org.dangcat.persistence.annotation.Calculator;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.validator.annotation.LogicValidators;
import org.dangcat.web.annotation.Permission;
import org.dangcat.web.annotation.Picker;
import org.dangcat.web.annotation.ValueMap;

@Table
@Calculator(OperatorInfoCalculator.class)
public class OperatorInfo extends OperatorInfoBase
{
    public static final String Description = "Description";
    public static final String ExpiryTime = "ExpiryTime";
    public static final String GroupId = "GroupId";
    public static final String GroupName = "GroupName";
    public static final String Mobile = "Mobile";
    public static final String RegisterTime = "RegisterTime";
    public static final String RoleId = "RoleId";
    public static final String RoleName = "RoleName";
    private static final long serialVersionUID = 1L;
    public static final String UseAble = "UseAble";
    @Column(index = 5, displaySize = 200)
    private String description = null;

    @Column(index = 14)
    @Permission(value = "advencedModify", visible = true)
    @DateStyle
    private Date expiryTime = null;

    @Column(index = 6, displaySize = 20, isNullable = false)
    @Picker(OperatorGroupPicker.class)
    @Permission(value = "advencedModify", visible = true)
    private Integer groupId = null;

    @Column(index = 7, displaySize = 20, isCalculate = true, isReadonly = true)
    private String groupName = null;

    @Column(index = 11, displaySize = 20)
    @LogicValidators( { MobileValidator.class })
    private String mobile = null;

    @Column(index = 15, isReadonly = true)
    @DateStyle(DateType.Minute)
    private Date registerTime = null;

    @Column(index = 8, displaySize = 20, isNullable = false)
    @ValueMap(jndiName = "Staff/RoleInfo")
    @Permission(value = "advencedModify", visible = true)
    private Integer roleId = null;

    @Column(index = 9, displaySize = 20, isCalculate = true, isReadonly = true)
    private String roleName = null;

    @Column(index = 13, isNullable = false)
    @Permission(value = "advencedModify", visible = true)
    private Boolean useAble = null;

    public OperatorInfo()
    {
    }

    public String getDescription()
    {
        return this.description;
    }

    public Date getExpiryTime()
    {
        return this.expiryTime;
    }

    public Integer getGroupId()
    {
        return this.groupId;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public String getMobile()
    {
        return mobile;
    }

    public Date getRegisterTime()
    {
        return this.registerTime;
    }

    public Integer getRoleId()
    {
        return this.roleId;
    }

    public String getRoleName()
    {
        return roleName;
    }

    public Boolean getUseAble()
    {
        return this.useAble == null ? Boolean.FALSE : this.useAble;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setExpiryTime(Date expiryTime)
    {
        this.expiryTime = expiryTime;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public void setRegisterTime(Date registerTime)
    {
        this.registerTime = registerTime;
    }

    public void setRoleId(Integer roleId)
    {
        this.roleId = roleId;
    }

    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }

    public void setUseAble(Boolean useAble)
    {
        this.useAble = useAble;
    }
}
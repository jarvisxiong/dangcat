package org.dangcat.business.staff.domain;

import org.dangcat.business.staff.picker.OperatorGroupPicker;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.web.annotation.Picker;

@Table("OperatorGroup")
public class OperatorGroupBase extends EntityBase
{
    public static final String Id = "Id";
    public static final String Name = "Name";
    public static final String ParentId = "ParentId";
    private static final long serialVersionUID = 1L;

    @Column(isPrimaryKey = true, isAutoIncrement = true, index = 0)
    private Integer id = null;

    @Column(index = 1, displaySize = 20, isNullable = false)
    private String name = null;

    @Column(index = 3, displaySize = 20)
    @Picker(OperatorGroupPicker.class)
    private Integer parentId = null;

    public OperatorGroupBase()
    {
    }

    public Integer getId()
    {
        return this.id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getParentId()
    {
        return this.parentId;
    }

    public void setParentId(Integer parentId)
    {
        this.parentId = parentId;
    }
}
package org.dangcat.business.staff.domain;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.entity.EntityBase;

@Table("RoleInfo")
public class RoleBasic extends EntityBase
{
    public static final String Description = "Description";
    public static final String Id = "Id";
    public static final String Name = "Name";
    private static final long serialVersionUID = 1L;

    @Column(index = 2, displaySize = 200)
    private String description = null;

    @Column(isPrimaryKey = true, isAutoIncrement = true, index = 0)
    private Integer id = null;

    @Column(index = 1, displaySize = 20, isNullable = false)
    private String name = null;

    public RoleBasic()
    {
    }

    public String getDescription()
    {
        return this.description;
    }

    public Integer getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
package org.dangcat.business.systeminfo;

import org.dangcat.boot.service.impl.ServiceCalculator;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;

@Table
public class PermissionInfo implements Comparable<PermissionInfo>
{
    @Column(isPrimaryKey = true)
    private Integer id = null;
    @Column
    private String name = null;
    @Column
    private Integer parentId = null;
    @Column
    private Integer type = null;

    public PermissionInfo()
    {
    }

    public PermissionInfo(Integer id)
    {
        this(id, null);
    }

    public PermissionInfo(Integer id, String name)
    {
        this.id = id;
        this.parentId = ServiceCalculator.getParentId(id);
        this.type = ServiceCalculator.getType(id);
        this.name = name;
    }

    @Override
    public int compareTo(PermissionInfo permissionInfo)
    {
        Integer srcId = ServiceCalculator.getSortNumber(this.getId());
        Integer dstId = ServiceCalculator.getSortNumber(permissionInfo.getId());
        return srcId.intValue() - dstId.intValue();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PermissionInfo other = (PermissionInfo) obj;
        if (id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        return true;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getParentId()
    {
        return parentId;
    }

    public void setParentId(Integer parentId)
    {
        this.parentId = parentId;
    }

    public Integer getType()
    {
        return type;
    }

    public void setType(Integer type)
    {
        this.type = type;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
}

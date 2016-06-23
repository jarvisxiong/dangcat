package org.dangcat.persistence.domain;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.JoinColumn;
import org.dangcat.persistence.annotation.JoinTable;
import org.dangcat.persistence.annotation.JoinTables;
import org.dangcat.persistence.annotation.Table;

@Table("AccountInfo")
@JoinTables( { @JoinTable(tableName = "GroupInfo", tableAlias = "G", joinColumns = { @JoinColumn(name = "Id", joinName = "GroupId") }),
        @JoinTable(tableName = "ServiceInfo", tableAlias = "S", joinColumns = { @JoinColumn(name = "Id", joinName = "ServiceId") }) })
public class AccountInfoJoinAlias
{
    public static final String GroupDescription = "GroupDescription";
    public static final String GroupId = "GroupId";
    public static final String GroupName = "GroupName";
    public static final String Id = "Id";
    public static final String Name = "Name";
    public static final String ServiceDescription = "ServiceDescription";
    public static final String ServiceId = "ServiceId";
    public static final String ServieName = "ServieName";

    @Column(fieldName = "G.Description", index = 4)
    private String groupDescription;

    @Column(index = 2)
    private Integer groupId;

    @Column(fieldName = "G.Name", index = 3)
    private String groupName;

    @Column(isPrimaryKey = true, isAutoIncrement = true, index = 0)
    private Integer id;

    @Column(index = 1)
    private String name;

    @Column(fieldName = "S.Description", index = 7)
    private String serviceDescription;

    @Column(index = 5)
    private Integer serviceId;

    @Column(fieldName = "S.Name", index = 6)
    private String serviceName;

    public String getGroupDescription()
    {
        return groupDescription;
    }

    public Integer getGroupId()
    {
        return groupId;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public Integer getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getServiceDescription()
    {
        return serviceDescription;
    }

    public Integer getServiceId()
    {
        return serviceId;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public void setGroupDescription(String groupDescription)
    {
        this.groupDescription = groupDescription;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setServiceDescription(String serviceDescription)
    {
        this.serviceDescription = serviceDescription;
    }

    public void setServiceId(Integer serviceId)
    {
        this.serviceId = serviceId;
    }

    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }
}

package org.dangcat.persistence.domain;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.JoinColumn;
import org.dangcat.persistence.annotation.JoinTable;
import org.dangcat.persistence.annotation.Table;

@Table
public class AccountInfo
{
    public static final String GroupDescription = "GroupDescription";
    public static final String GroupId = "GroupId";
    public static final String GroupName = "GroupName";
    public static final String Id = "Id";
    public static final String Name = "Name";
    public static final String ServiceDescription = "ServiceDescription";
    public static final String ServiceId = "ServiceId";
    public static final String ServieName = "ServieName";

    @Column(fieldName = "Description", index = 4)
    @JoinTable(tableName = "GroupInfo", joinColumns = { @JoinColumn(name = Id, joinName = GroupId) })
    private String groupDescription;

    @Column(index = 2)
    private Integer groupId;

    @Column(fieldName = "Name", index = 3)
    @JoinTable(tableName = "GroupInfo", joinColumns = { @JoinColumn(name = Id, joinName = GroupId) })
    private String groupName;

    @Column(isPrimaryKey = true, isAutoIncrement = true, index = 0)
    private Integer id;

    @Column(index = 1)
    private String name;

    @Column(fieldName = "Description", index = 7)
    @JoinTable(tableName = "ServiceInfo", joinColumns = { @JoinColumn(name = Id, joinName = ServiceId) })
    private String serviceDescription;

    @Column(index = 5)
    private Integer serviceId;

    @Column(fieldName = "Name", index = 6)
    @JoinTable(tableName = "ServiceInfo", joinColumns = { @JoinColumn(name = Id, joinName = ServiceId) })
    private String serviceName;

    public String getGroupDescription()
    {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription)
    {
        this.groupDescription = groupDescription;
    }

    public Integer getGroupId()
    {
        return groupId;
    }

    public void setGroupId(Integer groupId)
    {
        this.groupId = groupId;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
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

    public String getServiceDescription()
    {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription)
    {
        this.serviceDescription = serviceDescription;
    }

    public Integer getServiceId()
    {
        return serviceId;
    }

    public void setServiceId(Integer serviceId)
    {
        this.serviceId = serviceId;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }
}

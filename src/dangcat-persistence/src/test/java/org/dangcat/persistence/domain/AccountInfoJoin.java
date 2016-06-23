package org.dangcat.persistence.domain;

import org.dangcat.persistence.annotation.*;

@Table("AccountInfo")
@JoinTables( { @JoinTable(tableName = "GroupInfo", joinColumns = { @JoinColumn(name = "Id", joinName = "GroupId") }),
        @JoinTable(tableName = "ServiceInfo", joinColumns = { @JoinColumn(name = "Id", joinName = "ServiceId") }) })
public class AccountInfoJoin
{
    public static final String GroupDescription = "GroupDescription";
    public static final String GroupId = "GroupId";
    public static final String GroupName = "GroupName";
    public static final String Id = "Id";
    public static final String Name = "Name";
    public static final String ServiceDescription = "ServiceDescription";
    public static final String ServiceId = "ServiceId";
    public static final String ServieName = "ServieName";

    @Column(fieldName = "GroupInfo.Description", index = 4)
    private String groupDescription;

    @Column(index = 2)
    private Integer groupId;

    @Column(fieldName = "GroupInfo.Name", index = 3)
    private String groupName;

    @Column(isPrimaryKey = true, isAutoIncrement = true, index = 0)
    private Integer id;

    @Column(index = 1)
    private String name;

    @Column(fieldName = "ServiceInfo.Description", index = 7)
    private String serviceDescription;

    @Column(index = 5)
    private Integer serviceId;

    @Column(fieldName = "ServiceInfo.Name", index = 6)
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

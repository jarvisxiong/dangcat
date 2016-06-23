package org.dangcat.business.domain;

import java.util.Date;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.JoinColumn;
import org.dangcat.persistence.annotation.JoinTable;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.validator.annotation.ValueMapValidator;

@Table("AccountInfo")
public class AccountBasic extends EntityBase
{
    public static final String Email = "Email";
    public static final String GroupId = "GroupId";
    public static final String GroupName = "GroupName";
    public static final String Id = "Id";
    public static final String Name = "Name";
    public static final String RegisterTime = "RegisterTime";
    private static final long serialVersionUID = 1L;
    public static final String Sex = "Sex";
    public static final String Tel = "Tel";

    @Column(index = 5, displaySize = 40)
    private String email = null;

    @Column(index = 2, isNullable = false)
    private Integer groupId;

    @Column(fieldName = Name, index = 3)
    @JoinTable(tableName = "GroupInfo", joinColumns = { @JoinColumn(name = Id, joinName = "GroupId") })
    private String groupName;

    @Column(isPrimaryKey = true, isAutoIncrement = true, index = 0)
    private Integer id;

    @Column(index = 1, displaySize = 20)
    private String name = null;

    @Column(index = 6)
    private Date registerTime = null;

    @Column
    @ValueMapValidator
    private Integer sex = null;

    @Column(index = 4, displaySize = 40)
    private String tel = null;

    public String getEmail()
    {
        return email;
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

    public Date getRegisterTime()
    {
        return registerTime;
    }

    public Integer getSex()
    {
        return sex;
    }

    public String getTel()
    {
        return tel;
    }

    public void setEmail(String email)
    {
        this.email = email;
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

    public void setRegisterTime(Date registerTime)
    {
        this.registerTime = registerTime;
    }

    public void setSex(Integer sex)
    {
        this.sex = sex;
    }

    public void setTel(String tel)
    {
        this.tel = tel;
    }
}

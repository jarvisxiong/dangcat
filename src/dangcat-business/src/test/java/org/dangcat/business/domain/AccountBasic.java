package org.dangcat.business.domain;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.JoinColumn;
import org.dangcat.persistence.annotation.JoinTable;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.validator.annotation.ValueMapValidator;

import java.util.Date;

@Table("AccountInfo")
public class AccountBasic extends EntityBase
{
    public static final String Email = "Email";
    public static final String GroupId = "GroupId";
    public static final String GroupName = "GroupName";
    public static final String Id = "Id";
    public static final String Name = "Name";
    public static final String RegisterTime = "RegisterTime";
    public static final String Sex = "Sex";
    public static final String Tel = "Tel";
    private static final long serialVersionUID = 1L;
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

    public void setEmail(String email)
    {
        this.email = email;
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

    public Date getRegisterTime()
    {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime)
    {
        this.registerTime = registerTime;
    }

    public Integer getSex()
    {
        return sex;
    }

    public void setSex(Integer sex)
    {
        this.sex = sex;
    }

    public String getTel()
    {
        return tel;
    }

    public void setTel(String tel)
    {
        this.tel = tel;
    }
}

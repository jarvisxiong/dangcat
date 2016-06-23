package org.dangcat.persistence.entity.domain;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.entity.EntityBase;

import java.util.Date;

public class PersonInfo extends EntityBase
{
    public static final String Description = "Description";
    public static final String Email = "Email";
    public static final String ExpireTime = "ExpireTime";
    public static final String Id = "Id";
    public static final String Mobile = "Mobile";
    public static final String Name = "Name";
    public static final String No = "No";
    public static final String RegisterTime = "RegisterTime";
    public static final String Tel = "Tel";
    public static final String UseAble = "UseAble";
    private static final long serialVersionUID = 1L;
    @Column(index = 2, displaySize = 128)
    private String description = null;

    @Column(index = 4, displaySize = 30)
    private String email = null;

    @Column(index = 6)
    private Date expireTime = null;

    @Column(isPrimaryKey = true, isAutoIncrement = true, index = 0)
    private Integer id = null;

    @Column(index = 11, displaySize = 20)
    private String mobile = null;

    @Column(index = 1, displaySize = 20, isNullable = false)
    private String name = null;

    @Column(index = 5, isNullable = false)
    private Date registerTime = null;

    @Column(index = 3, displaySize = 20)
    private String tel = null;

    public PersonInfo()
    {
    }

    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getEmail()
    {
        return this.email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public Date getExpireTime()
    {
        return this.expireTime;
    }

    public void setExpireTime(Date expireTime)
    {
        this.expireTime = expireTime;
    }

    public Integer getId()
    {
        return this.id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Date getRegisterTime()
    {
        return this.registerTime;
    }

    public void setRegisterTime(Date registerTime)
    {
        this.registerTime = registerTime;
    }

    public String getTel()
    {
        return this.tel;
    }

    public void setTel(String tel)
    {
        this.tel = tel;
    }
}

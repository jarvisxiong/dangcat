package org.dangcat.persistence.index;

import java.util.Date;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.entity.EntityBase;

@Table("UserInfoIndex")
public class UserInfo extends EntityBase
{
    public static final String Address = "Address";
    public static final String Age = "Age";
    public static final String Id = "Id";
    public static final String Name = "Name";
    public static final String RegisterTime = "RegisterTime";
    private static final long serialVersionUID = 1L;

    @Column(displaySize = 120)
    private String address;

    @Column
    private Integer age;

    @Column(isPrimaryKey = true, isAutoIncrement = true)
    private Integer id;

    @Column(displaySize = 40)
    private String name;

    @Column
    private Date registerTime;

    public String getAddress()
    {
        return address;
    }

    public Integer getAge()
    {
        return age;
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

    public void setAddress(String address)
    {
        this.address = address;
    }

    public void setAge(Integer age)
    {
        this.age = age;
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
}

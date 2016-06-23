package org.dangcat.business.staff.domain;

import org.dangcat.business.validator.EmailValidator;
import org.dangcat.business.validator.NoValidator;
import org.dangcat.business.validator.TelValidator;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.model.DataState;
import org.dangcat.persistence.validator.annotation.LogicValidators;
import org.dangcat.web.annotation.EditDataStates;

@Table("OperatorInfo")
public class OperatorInfoBase extends EntityBase
{
    public static final String Email = "Email";
    public static final String Id = "Id";
    public static final String Name = "Name";
    public static final String No = "No";
    public static final String Tel = "Tel";
    private static final long serialVersionUID = 1L;
    @Column(index = 12, displaySize = 30)
    @LogicValidators( { EmailValidator.class })
    private String email = null;

    @Column(index = 0, isPrimaryKey = true, isAutoIncrement = true)
    private Integer id = null;

    @Column(index = 2, displaySize = 20, isNullable = false)
    private String name = null;

    @Column(index = 1, displaySize = 20, isNullable = false)
    @EditDataStates(DataState.Insert)
    @LogicValidators( { NoValidator.class })
    private String no = null;

    @Column(index = 10, displaySize = 20)
    @LogicValidators( { TelValidator.class })
    private String tel = null;

    public OperatorInfoBase()
    {
    }

    public String getEmail()
    {
        return this.email;
    }

    public void setEmail(String email)
    {
        this.email = email;
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

    public String getNo()
    {
        return this.no;
    }

    public void setNo(String no)
    {
        this.no = no;
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
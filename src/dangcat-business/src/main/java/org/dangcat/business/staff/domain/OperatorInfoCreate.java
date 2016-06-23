package org.dangcat.business.staff.domain;

import org.dangcat.commons.serialize.annotation.Serialize;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.model.DataState;
import org.dangcat.web.annotation.Password;
import org.dangcat.web.annotation.VisibleDataStates;

@Table("OperatorInfo")
public class OperatorInfoCreate extends OperatorInfo
{
    public static final String Password = "Password";
    public static final String Password1 = "Password1";
    public static final String Password2 = "Password2";
    private static final long serialVersionUID = 1L;

    @Column(index = 3, displaySize = 32, visible = false)
    private String password = null;

    @Password(No)
    @Column(index = 3, displaySize = 32, isCalculate = true, isNullable = false)
    @VisibleDataStates( { DataState.Insert })
    private String password1 = null;

    @Password(No)
    @Column(index = 4, displaySize = 32, isCalculate = true, isNullable = false)
    @VisibleDataStates( { DataState.Insert })
    private String password2 = null;

    @Serialize(ignore = true)
    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword1()
    {
        return password1;
    }

    public void setPassword1(String password1)
    {
        this.password1 = password1;
    }

    public String getPassword2()
    {
        return password2;
    }

    public void setPassword2(String password2)
    {
        this.password2 = password2;
    }
}
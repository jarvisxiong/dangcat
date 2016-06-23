package org.dangcat.business.staff.security;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

import org.dangcat.business.staff.domain.RolePermission;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.JoinColumn;
import org.dangcat.persistence.annotation.JoinTable;
import org.dangcat.persistence.annotation.Relation;
import org.dangcat.persistence.annotation.Table;

@Table("OperatorInfo")
public class LoginOperator
{
    public static final String ExpiryTime = "ExpiryTime";
    public static final String Id = "Id";
    public static final String Name = "Name";
    public static final String No = "No";
    public static final String Password = "Password";
    public static final String RoleId = "RoleId";
    public static final String RoleName = "RoleName";
    private static final long serialVersionUID = 1L;
    public static final String UseAble = "UseAble";

    public static String getRoleid()
    {
        return RoleId;
    }

    @Column
    private Date expiryTime = null;

    @Column(isPrimaryKey = true)
    private Integer id = null;

    @Column
    private String name = null;

    @Column
    private String no = null;

    @Column
    private String password = null;

    @Column
    private Integer roleId = null;

    @Column(fieldName = Name)
    @JoinTable(tableName = "RoleInfo", joinColumns = { @JoinColumn(name = Id, joinName = RoleId) })
    private String roleName = null;

    @Relation(parentFieldNames = { RoleId }, childFieldNames = { RolePermission.RoleId })
    private Collection<RolePermission> rolePermissions = new LinkedHashSet<RolePermission>();

    @Column
    private Boolean useAble = null;

    public LoginOperator()
    {
    }

    public Date getExpiryTime()
    {
        return expiryTime;
    }

    public Integer getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getNo()
    {
        return no;
    }

    public String getPassword()
    {
        return password;
    }

    public Integer getRoleId()
    {
        return roleId;
    }

    public String getRoleName()
    {
        return roleName;
    }

    public Collection<RolePermission> getRolePermissions()
    {
        return rolePermissions;
    }

    public Boolean getUseAble()
    {
        return useAble;
    }

    public void setExpiryTime(Date expiryTime)
    {
        this.expiryTime = expiryTime;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setNo(String no)
    {
        this.no = no;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setRoleId(Integer roleId)
    {
        this.roleId = roleId;
    }

    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }

    public void setUseAble(Boolean useAble)
    {
        this.useAble = useAble;
    }
}
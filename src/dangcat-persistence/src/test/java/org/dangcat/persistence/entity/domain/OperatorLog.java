package org.dangcat.persistence.entity.domain;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Index;
import org.dangcat.persistence.annotation.Indexes;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.tablename.DateTimeTableName;

import java.util.Date;

@Table(tableName = DateTimeTableName.class)
@Indexes( { @Index("DateTime, OperatorId"), @Index("DateTime, PermissionId, OperatorId") })
public class OperatorLog extends EntityBase
{
    public static final String DateTime = "DateTime";
    public static final String Id = "Id";
    public static final String OperatorId = "OperatorId";
    public static final String PermissionId = "PermissionId";
    public static final String Remark = "Remark";
    public static final String Result = "Result";
    private static final long serialVersionUID = 1L;

    @Column(index = 1, isNullable = false)
    private Date dateTime = null;

    @Column(isPrimaryKey = true, isAutoIncrement = true, index = 0)
    private Integer id = null;

    @Column(index = 2)
    private Integer operatorId = null;

    @Column(index = 3)
    private Integer permissionId = null;

    @Column(index = 5, displaySize = 128)
    private String remark = null;

    @Column(index = 4)
    private Integer result = null;

    public OperatorLog()
    {
    }

    public Date getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(Date dateTime)
    {
        this.dateTime = dateTime;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getOperatorId()
    {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId)
    {
        this.operatorId = operatorId;
    }

    public Integer getPermissionId()
    {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId)
    {
        this.permissionId = permissionId;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Integer getResult()
    {
        return result;
    }

    public void setResult(Integer result)
    {
        this.result = result;
    }
}

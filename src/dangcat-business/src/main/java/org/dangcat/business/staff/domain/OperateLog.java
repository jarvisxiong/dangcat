package org.dangcat.business.staff.domain;

import java.util.Date;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Index;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.tablename.DateTimeTableName;

@Table(tableName = DateTimeTableName.class)
@Index("OperatorId")
public class OperateLog extends EntityBase
{
    public static final String DateTime = "DateTime";
    public static final String ErrorCode = "ErrorCode";
    public static final String Id = "Id";
    public static final String IpAddress = "IpAddress";
    public static final String MethodId = "MethodId";
    public static final String OperatorId = "OperatorId";
    public static final String Remark = "Remark";
    private static final long serialVersionUID = 1L;
    public static final Integer SUCCESS = 0;

    @Column(index = 2)
    private Date dateTime = DateUtils.now();

    @Column(index = 9, isNullable = false)
    private Integer errorCode = null;

    @Column(index = 0, isPrimaryKey = true, isAutoIncrement = true, visible = false)
    private Integer id = null;

    @Column(index = 3, displaySize = 24)
    private String ipAddress = null;

    @Column(index = 4, isNullable = false, visible = false)
    private Integer methodId = null;

    @Column(index = 1, visible = false)
    private Integer operatorId = null;

    @Column(index = 11, displaySize = 200)
    private String remark = null;

    public Date getDateTime()
    {
        return dateTime;
    }

    public Integer getErrorCode()
    {
        return errorCode;
    }

    public Integer getId()
    {
        return id;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public Integer getMethodId()
    {
        return methodId;
    }

    public Integer getOperatorId()
    {
        return operatorId;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setDateTime(Date dateTime)
    {
        this.dateTime = dateTime;
    }

    public void setErrorCode(Integer errorCode)
    {
        this.errorCode = errorCode;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public void setMethodId(Integer methodId)
    {
        this.methodId = methodId;
    }

    public void setOperatorId(Integer operatorId)
    {
        this.operatorId = operatorId;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }
}
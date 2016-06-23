package org.dangcat.business.staff.domain;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Index;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.tablename.DateTimeTableName;

import java.util.Date;

@Table(tableName = DateTimeTableName.class)
@Index("OperatorId")
public class OperateLog extends EntityBase {
    public static final String DateTime = "DateTime";
    public static final String ErrorCode = "ErrorCode";
    public static final String Id = "Id";
    public static final String IpAddress = "IpAddress";
    public static final String MethodId = "MethodId";
    public static final String OperatorId = "OperatorId";
    public static final String Remark = "Remark";
    public static final Integer SUCCESS = 0;
    private static final long serialVersionUID = 1L;
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

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
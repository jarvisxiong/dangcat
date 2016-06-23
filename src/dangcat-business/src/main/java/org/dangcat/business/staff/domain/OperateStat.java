package org.dangcat.business.staff.domain;

import org.dangcat.business.settle.SettleEntity;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.SqlXml;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.tablename.DateTimeTableName;

/**
 * ²Ù×÷Í³¼Æ¡£
 * @author dangcat
 * 
 */
@Table(tableName = DateTimeTableName.class)
@SqlXml
public class OperateStat extends SettleEntity
{
    public static final String Failure = "Failure";
    public static final String GroupName = "GroupName";
    public static final String OperatorId = "OperatorId";
    public static final String OperatorName = "OperatorName";
    public static final String OperatorNo = "OperatorNo";
    public static final String Success = "Success";
    private static final long serialVersionUID = 1L;
    @Column(index = 5, isNullable = false)
    private Integer failure = 0;

    @Column(index = 3, displaySize = 20, isCalculate = true)
    private String groupName = null;

    @Column(index = 0, isPrimaryKey = true, visible = false)
    private Integer operatorId = null;

    @Column(index = 2, displaySize = 20, isCalculate = true)
    private String operatorName = null;

    @Column(index = 1, displaySize = 20, isCalculate = true)
    private String operatorNo = null;

    @Column(index = 4, isNullable = false)
    private Integer success = 0;

    public Integer getFailure()
    {
        return failure;
    }

    public void setFailure(Integer failure)
    {
        this.failure = failure;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public Integer getOperatorId()
    {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId)
    {
        this.operatorId = operatorId;
    }

    public String getOperatorName()
    {
        return operatorName;
    }

    public void setOperatorName(String operatorName)
    {
        this.operatorName = operatorName;
    }

    public String getOperatorNo()
    {
        return operatorNo;
    }

    public void setOperatorNo(String operatorNo)
    {
        this.operatorNo = operatorNo;
    }

    public Integer getSuccess()
    {
        return success;
    }

    public void setSuccess(Integer success)
    {
        this.success = success;
    }
}
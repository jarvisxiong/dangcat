package org.dangcat.business.server;

import org.dangcat.boot.server.domain.ServerInfo;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.annotation.Calculator;
import org.dangcat.persistence.annotation.Column;

@Calculator(ServiceInfoCalculator.class)
public class ServerInfoQuery extends ServerInfo
{
    private static final long serialVersionUID = 1L;
    public static final String StatusName = "StatusName";
    public static final String TypeName = "TypeName";

    @Column(index = 5, displaySize = 32, isCalculate = true)
    private String statusName = null;

    @Column(index = 5, displaySize = 10, isCalculate = true)
    private String typeName = null;

    @Column(index = 5, isCalculate = true, isReadonly = true, logic = "connect")
    public Boolean getIsLive()
    {
        return this.getLastResponseTime() != null && Math.abs(DateUtils.diff(DateUtils.MINUTE, this.getLastResponseTime(), DateUtils.now())) <= 5;
    }

    public String getStatusName()
    {
        return statusName;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setStatusName(String statusName)
    {
        this.statusName = statusName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }
}
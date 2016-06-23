package org.dangcat.business.domain;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.entity.EntityBase;

@Table
public class AccountServiceBind extends EntityBase
{
    public static final String AccountId = "AccountId";
    private static final long serialVersionUID = 1L;
    public static final String ServiceId = "ServiceId";

    @Column(isPrimaryKey = true)
    private Integer accountId;

    @Column(isPrimaryKey = true)
    private Integer serviceId;

    public Integer getAccountId()
    {
        return accountId;
    }

    public Integer getServiceId()
    {
        return serviceId;
    }

    public void setAccountId(Integer accountId)
    {
        this.accountId = accountId;
    }

    public void setServiceId(Integer serviceId)
    {
        this.serviceId = serviceId;
    }
}

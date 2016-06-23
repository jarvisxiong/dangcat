package org.dangcat.persistence.domain;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.model.DataStatus;

@Table
public class UserService extends EntityBase implements DataStatus
{
    public static final String Account = "Account";
    public static final String BindTime = "BindTime";
    private static final long serialVersionUID = 1L;
    public static final String ServiceId = "ServiceId";

    @Column(isPrimaryKey = true, displaySize = 128)
    private String account;

    @Column(displaySize = 20)
    private String bindTime;

    @Column(isPrimaryKey = true)
    private Integer serviceId;

    public String getAccount()
    {
        return account;
    }

    public String getBindTime()
    {
        return bindTime;
    }

    public Integer getServiceId()
    {
        return serviceId;
    }

    public void setAccount(String account)
    {
        this.account = account;
    }

    public void setBindTime(String bindTime)
    {
        this.bindTime = bindTime;
    }

    public void setServiceId(Integer serviceId)
    {
        this.serviceId = serviceId;
    }
}
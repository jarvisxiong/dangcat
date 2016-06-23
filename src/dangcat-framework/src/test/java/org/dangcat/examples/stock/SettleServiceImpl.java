package org.dangcat.examples.stock;

import java.util.Date;

import org.dangcat.framework.EntityResource;
import org.dangcat.framework.EntityResourceManager;
import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.annotation.Service;

public class SettleServiceImpl extends ServiceBase implements SettleService
{
    private Integer count = null;
    private Date dateTime = null;
    @EntityResource
    private EntityResourceManager entityManager;
    @EntityResource("Hsqldb")
    private EntityResourceManager hsqldbEntityManager;
    @EntityResource("MySql")
    private EntityResourceManager mySqlEntityManager;
    private String name = null;
    @EntityResource("Oracle")
    private EntityResourceManager oracleEntityManager;

    private boolean result;

    @EntityResource("SqlServer")
    private EntityResourceManager sqlServerEntityManager;

    private Double total = null;

    @Service
    private UserInfoService userInfoService;

    public SettleServiceImpl(ServiceProvider parent)
    {
        super(parent);
    }

    public Integer getCount()
    {
        return count;
    }

    public Date getDateTime()
    {
        return dateTime;
    }

    public EntityResourceManager getEntityManager()
    {
        return entityManager;
    }

    public EntityResourceManager getHsqldbEntityManager()
    {
        return hsqldbEntityManager;
    }

    public EntityResourceManager getMySqlEntityManager()
    {
        return mySqlEntityManager;
    }

    public String getName()
    {
        return name;
    }

    public EntityResourceManager getOracleEntityManager()
    {
        return oracleEntityManager;
    }

    public EntityResourceManager getSqlServerEntityManager()
    {
        return sqlServerEntityManager;
    }

    public Double getTotal()
    {
        return total;
    }

    public UserInfoService getUserInfoService()
    {
        return userInfoService;
    }

    public boolean isResult()
    {
        return result;
    }

    public void setCount(Integer count)
    {
        this.count = count;
    }

    public void setDateTime(Date dateTime)
    {
        this.dateTime = dateTime;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setResult(boolean result)
    {
        this.result = result;
    }

    public void setTotal(Double total)
    {
        this.total = total;
    }
}

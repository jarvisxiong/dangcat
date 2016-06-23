package org.dangcat.business.domain;

import java.util.Date;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.entity.EntityBase;

@Table
public class AccountBill extends EntityBase
{
    public static final String AccountId = "AccountId";
    public static final String Amount = "Amount";
    public static final String DateTime = "DateTime";
    public static final String Description = "Description";
    private static final long serialVersionUID = 1L;

    @Column
    private Integer accountId;

    @Column
    private Double amount;

    @Column
    private Date dateTime;

    @Column(displaySize = 60)
    private String description;

    @Column(isPrimaryKey = true, isAutoIncrement = true)
    private Integer id;

    public Integer getAccountId()
    {
        return accountId;
    }

    public Double getAmount()
    {
        return amount;
    }

    public Date getDateTime()
    {
        return dateTime;
    }

    public String getDescription()
    {
        return description;
    }

    public Integer getId()
    {
        return id;
    }

    public void setAccountId(Integer accountId)
    {
        this.accountId = accountId;
    }

    public void setAmount(Double amount)
    {
        this.amount = amount;
    }

    public void setDateTime(Date dateTime)
    {
        this.dateTime = dateTime;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }
}

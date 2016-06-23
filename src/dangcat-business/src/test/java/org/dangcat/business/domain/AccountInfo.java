package org.dangcat.business.domain;

import java.util.LinkedHashSet;
import java.util.Set;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Relation;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.validator.annotation.RangeValidator;

@Table
public class AccountInfo extends AccountBasic
{
    public static final String AccountBills = "AccountBills";
    public static final String AccountServiceBinds = "AccountServiceBinds";
    public static final String Address = "Address";
    public static final String Balance = "Balance";
    public static final String MaxBalance = "MaxBalance";
    public static final String MinBalance = "MinBalance";
    private static final long serialVersionUID = 1L;

    @Relation(parentFieldNames = { Id }, childFieldNames = { AccountBill.AccountId })
    private Set<AccountBill> accountBills = new LinkedHashSet<AccountBill>();

    @Relation(parentFieldNames = { Id }, childFieldNames = { AccountBill.AccountId })
    private Set<AccountServiceBind> accountServiceBinds = new LinkedHashSet<AccountServiceBind>();

    @Column(displaySize = 60)
    private String address = null;

    @Column
    @RangeValidator(minValue = "50", maxValue = "100")
    private Double balance = null;

    @Column
    @RangeValidator(maxValue = "200")
    private Double maxBalance = null;

    @Column
    @RangeValidator(minValue = "10")
    private Double minBalance = null;

    public Set<AccountBill> getAccountBills()
    {
        return accountBills;
    }

    public Set<AccountServiceBind> getAccountServiceBinds()
    {
        return accountServiceBinds;
    }

    public String getAddress()
    {
        return address;
    }

    public Double getBalance()
    {
        return balance;
    }

    public Double getMaxBalance()
    {
        return maxBalance;
    }

    public Double getMinBalance()
    {
        return minBalance;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public void setBalance(Double balance)
    {
        this.balance = balance;
    }

    public void setMaxBalance(Double maxBalance)
    {
        this.maxBalance = maxBalance;
    }

    public void setMinBalance(Double minBalance)
    {
        this.minBalance = minBalance;
    }
}

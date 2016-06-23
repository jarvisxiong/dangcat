package org.dangcat.business.account.service;

import java.util.Date;

public class Person
{
    private int age;
    private double balance;
    private Date borthDay;
    private String name;

    public int getAge()
    {
        return age;
    }

    public double getBalance()
    {
        return balance;
    }

    public Date getBorthDay()
    {
        return borthDay;
    }

    public String getName()
    {
        return name;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public void setBalance(double balance)
    {
        this.balance = balance;
    }

    public void setBorthDay(Date borthDay)
    {
        this.borthDay = borthDay;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}

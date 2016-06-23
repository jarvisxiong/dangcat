package org.dangcat.business.account.service.impl;

import org.dangcat.business.account.service.AccountFilter;
import org.dangcat.business.account.service.AccountService;
import org.dangcat.business.account.service.Person;
import org.dangcat.business.domain.AccountBasic;
import org.dangcat.business.domain.AccountInfo;
import org.dangcat.business.service.impl.BusinessServiceBase;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.ServiceProvider;

import java.util.Date;

/**
 * 账户管理服务接口。
 * @author dangcat
 * 
 */
public class AccountServiceImpl extends BusinessServiceBase<AccountBasic, AccountInfo, AccountFilter> implements AccountService
{
    public AccountServiceImpl(ServiceProvider parent)
    {
        super(parent);
    }

    @Override
    public Person createPerson(String name, int age, double balance, Date borthDay) throws ServiceException
    {
        Person person = new Person();
        person.setName(name);
        person.setAge(age);
        person.setBalance(balance);
        person.setBorthDay(borthDay);
        return person;
    }

    @Override
    protected void onCreate(AccountInfo accountInfo)
    {
        accountInfo.setName("Monkey hou");
        accountInfo.setAddress("中国深圳");
        accountInfo.setEmail("houxx@h3c.com.cn");
        accountInfo.setBalance(100.0);
        accountInfo.setGroupId(0);
        accountInfo.setRegisterTime(DateUtils.clear(DateUtils.SECOND, DateUtils.now()));
        accountInfo.setTel("0123456789");

        super.onCreate(accountInfo);
    }

    @Override
    public String printPerson(Person person) throws ServiceException
    {
        StringBuilder info = new StringBuilder();
        info.append("Name : " + person.getName() + "\r\n");
        info.append("Age : " + person.getAge() + "\r\n");
        info.append("Balance : " + person.getBalance() + "\r\n");
        info.append("BorthDay : " + person.getBorthDay());
        return info.toString();
    }
}

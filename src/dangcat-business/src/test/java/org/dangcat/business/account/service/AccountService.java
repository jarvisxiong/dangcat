package org.dangcat.business.account.service;

import java.util.Date;
import java.util.Map;

import org.dangcat.business.domain.AccountBasic;
import org.dangcat.business.domain.AccountInfo;
import org.dangcat.business.service.QueryResult;
import org.dangcat.commons.reflect.Parameter;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.annotation.JndiName;

/**
 * 账户管理服务接口。
 * @author dangcat
 * 
 */
@JndiName(module = "Account", name = "AccountInfo")
public interface AccountService
{
    /**
     * 新增实体数据。
     * @param entity 实体对象。
     * @return 运行结果。
     */
    public AccountInfo create(@Parameter(name = "accountInfo") AccountInfo accountInfo) throws ServiceException;

    /**
     * 测试多个基本参数接口。
     * @param id 主键值。
     * @return 查询结果。
     */
    public Person createPerson(@Parameter(name = "name") String name, @Parameter(name = "age") int age, @Parameter(name = "balance") double balance, @Parameter(name = "borthDay") Date borthDay)
            throws ServiceException;

    /**
     * 删除指定条件的数据。
     * @param id 主键。
     * @return 执行结果。
     */
    public boolean delete(@Parameter(name = "id") Integer id) throws ServiceException;

    /**
     * 测试多个基本参数接口。
     * @param id 主键值。
     * @return 查询结果。
     */
    public String printPerson(@Parameter(name = "person") Person person) throws ServiceException;

    /**
     * 查询指定条件的数据。
     * @param dataFilter 查询范围。
     * @return 查询结果。
     */
    public QueryResult<AccountBasic> query(@Parameter(name = "accountFilter") AccountFilter accountFilter) throws ServiceException;

    /**
     * 保存实体数据。
     * @param entity 实体对象。
     * @return 运行结果。
     */
    public AccountInfo save(@Parameter(name = "accountInfo") AccountInfo accountInfo) throws ServiceException;

    /**
     * 查询指定条件的列表。
     * @param dataFilter 查询范围。
     * @return 查询结果。
     */
    public Map<Integer, String> select(@Parameter(name = "accountFilter") AccountFilter accountFilter) throws ServiceException;

    /**
     * 查看指定主键的数据。
     * @param id 主键值。
     * @return 查询结果。
     */
    public AccountInfo view(@Parameter(name = "id") Integer id) throws ServiceException;
}

package org.dangcat.business.staff.service;

import org.dangcat.business.service.QueryResult;
import org.dangcat.business.staff.config.StaffSetup;
import org.dangcat.business.staff.domain.OperatorInfo;
import org.dangcat.business.staff.domain.OperatorInfoBase;
import org.dangcat.business.staff.domain.OperatorInfoCreate;
import org.dangcat.business.staff.filter.OperatorInfoFilter;
import org.dangcat.commons.reflect.Parameter;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.annotation.JndiName;

import java.util.Collection;
import java.util.Map;

/**
 * The service interface for Operator.
 * @author dangcat
 * 
 */
@JndiName(module = "Staff", name = "OperatorInfo")
public interface OperatorInfoService
{
    /**
     * 修改指定账号的密码。
     * @param orgPassword 原始密码。
     * @param newPassword 新密码。
     * @return 执行结果。
     */
    boolean changePassword(@Parameter(name = "orgPassword") String orgPassword, @Parameter(name = "newPassword") String newPassword) throws ServiceException;

    /**
     * 保存参数配置。
     */
    StaffSetup config(@Parameter(name = "config") StaffSetup staffSetup) throws ServiceException;

    /**
     * 新增实体数据。
     * @param operatorInfo 实体对象。
     * @return 运行结果。
     */
    OperatorInfo create(@Parameter(name = "operatorInfo") OperatorInfoCreate operatorInfo) throws ServiceException;

    /**
     * 删除指定条件的数据。
     * @param id 主键。
     * @return 执行结果。
     */
    boolean delete(@Parameter(name = "id") Integer id) throws ServiceException;

    /**
     * 查询指定条件的基本操作员信息。
     * @param operatorInfoFilter 查询条件。
     * @return 查询结果。
     */
    Collection<OperatorInfoBase> pick(OperatorInfoFilter operatorInfoFilter) throws ServiceException;

    /**
     * 查询指定条件的数据。
     * @param operatorInfoFilter 查询条件。
     * @return 查询结果。
     */
    QueryResult<OperatorInfo> query(@Parameter(name = "operatorInfoFilter") OperatorInfoFilter operatorInfoFilter) throws ServiceException;

    /**
     * 重置指定账号的密码。
     * @param no 操作员账号。
     * @param password 新密码。
     * @return 执行结果。
     */
    boolean resetPassword(@Parameter(name = "no") String no, @Parameter(name = "password") String password) throws ServiceException;

    /**
     * 保存实体数据。
     * @param operatorInfo 实体对象。
     * @return 运行结果。
     */
    OperatorInfo save(@Parameter(name = "operatorInfo") OperatorInfo operatorInfo) throws ServiceException;

    /**
     * 查询指定条件的列表。
     * @param operatorInfoFilter 查询条件。
     * @return 查询结果。
     */
    Map<Integer, String> select(@Parameter(name = "operatorInfoFilter") OperatorInfoFilter operatorInfoFilter) throws ServiceException;

    /**
     * 查看指定主键的数据。
     * @param id 主键值。
     * @return 查看结果。
     */
    OperatorInfo view(@Parameter(name = "id") Integer id) throws ServiceException;
}

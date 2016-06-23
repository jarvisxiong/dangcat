package org.dangcat.business.staff.service;

import org.dangcat.business.service.QueryResult;
import org.dangcat.business.staff.domain.RoleBasic;
import org.dangcat.business.staff.domain.RoleInfo;
import org.dangcat.business.staff.filter.RoleInfoFilter;
import org.dangcat.commons.reflect.Parameter;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.annotation.JndiName;

import java.util.Map;

/**
 * The service interface for Role.
 * @author dangcat
 * 
 */
@JndiName(module = "Staff", name = "RoleInfo")
public interface RoleInfoService
{
    /**
     * 新增实体数据。
     * @param roleInfo 实体对象。
     * @return 运行结果。
     */
    RoleInfo create(@Parameter(name = "roleInfo") RoleInfo roleInfo) throws ServiceException;

    /**
     * 删除指定条件的数据。
     * @param id 主键。
     * @return 执行结果。
     */
    boolean delete(@Parameter(name = "id") Integer id) throws ServiceException;

    /**
     * 查询指定条件的数据。
     * @param roleInfoFilter 查询条件。
     * @return 查询结果。
     */
    QueryResult<RoleBasic> query(@Parameter(name = "roleInfoFilter") RoleInfoFilter roleInfoFilter) throws ServiceException;

    /**
     * 保存实体数据。
     * @param roleInfo 实体对象。
     * @return 运行结果。
     */
    RoleInfo save(@Parameter(name = "roleInfo") RoleInfo roleInfo) throws ServiceException;

    /**
     * 查询指定条件的列表。
     * @param roleInfoFilter 查询条件。
     * @return 查询结果。
     */
    Map<Integer, String> select(@Parameter(name = "roleInfoFilter") RoleInfoFilter roleInfoFilter) throws ServiceException;

    /**
     * 查看指定主键的数据。
     * @param id 主键值。
     * @return 查看结果。
     */
    RoleInfo view(@Parameter(name = "id") Integer id) throws ServiceException;
}

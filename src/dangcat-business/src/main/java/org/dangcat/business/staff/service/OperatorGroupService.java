package org.dangcat.business.staff.service;

import org.dangcat.business.service.QueryResult;
import org.dangcat.business.staff.domain.OperatorGroup;
import org.dangcat.business.staff.domain.OperatorGroupBase;
import org.dangcat.business.staff.filter.OperatorGroupFilter;
import org.dangcat.commons.reflect.Parameter;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.annotation.JndiName;

import java.util.Collection;
import java.util.Map;

/**
 * The service interface for OperatorGroup.
 *
 * @author dangcat
 */
@JndiName(module = "Staff", name = "OperatorGroup")
public interface OperatorGroupService {
    /**
     * 新增实体数据。
     *
     * @param operatorGroup 实体对象。
     * @return 运行结果。
     */
    OperatorGroup create(@Parameter(name = "operatorGroup") OperatorGroup operatorGroup) throws ServiceException;

    /**
     * 删除指定条件的数据。
     *
     * @param id 主键。
     * @return 执行结果。
     */
    boolean delete(@Parameter(name = "id") Integer id) throws ServiceException;

    /**
     * 查看当前用户所属的操作员组的子组列表。
     *
     * @return 操作员组映射表。
     */
    Map<Integer, String> loadMembers() throws ServiceException;

    /**
     * 查询指定条件的列表。
     *
     * @param operatorGroupFilter 查询条件。
     * @return 查询结果。
     */
    Collection<OperatorGroupBase> pick(@Parameter(name = "operatorGroupFilter") OperatorGroupFilter operatorGroupFilter) throws ServiceException;

    /**
     * 查询指定条件的数据。
     *
     * @param operatorGroupFilter 查询条件。
     * @return 查询结果。
     */
    QueryResult<OperatorGroup> query(@Parameter(name = "operatorGroupFilter") OperatorGroupFilter operatorGroupFilter) throws ServiceException;

    /**
     * 保存实体数据。
     *
     * @param operatorGroup 实体对象。
     * @return 运行结果。
     */
    OperatorGroup save(@Parameter(name = "operatorGroup") OperatorGroup operatorGroup) throws ServiceException;

    /**
     * 查询指定条件的列表。
     *
     * @param operatorGroupFilter 查询条件。
     * @return 查询结果。
     */
    Map<Integer, String> select(@Parameter(name = "operatorGroupFilter") OperatorGroupFilter operatorGroupFilter) throws ServiceException;

    /**
     * 查看指定主键的数据。
     *
     * @param id 主键值。
     * @return 查看结果。
     */
    OperatorGroup view(@Parameter(name = "id") Integer id) throws ServiceException;
}

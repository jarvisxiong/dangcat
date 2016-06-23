package org.dangcat.business.server;

import org.dangcat.business.service.QueryResult;
import org.dangcat.chart.TimeData;
import org.dangcat.chart.TimeRange;
import org.dangcat.commons.reflect.Parameter;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.annotation.JndiName;

import java.util.Date;

/**
 * 服务器管理。
 *
 * @author
 */
@JndiName(module = "Device", name = "ServerInfo")
public interface ServerInfoService {
    /**
     * 删除指定条件的数据。
     *
     * @param Id 服务器号。
     * @return 删除结果。
     */
    boolean delete(@Parameter(name = "id") Integer id) throws ServiceException;

    /**
     * 查询指定服务器的总内存大小。
     *
     * @param Id 服务器号。
     * @return 系统总内存。
     */
    long getTotalPhysicalMemory(@Parameter(name = "id") Integer id);

    /**
     * 载入服务器资源状态数据。
     *
     * @param id        服务器号。
     * @param timeRange 时间范围。
     * @param baseTime  基准时间。
     * @param lastTime  最后数据起始时间。
     * @return 服务资器源状态数据。
     */
    TimeData<ServerResourceLog> loadServerResourceLogs(@Parameter(name = "id") Integer id, @Parameter(name = "timeRange") TimeRange timeRange, @Parameter(name = "lastTime") Date lastTime);

    /**
     * 查询指定条件的数据。
     *
     * @param serverInfoFilter 查询范围。
     * @return 查询结果。
     */
    QueryResult<ServerInfoQuery> query(@Parameter(name = "serverInfoFilter") ServerInfoFilter serverInfoFilter) throws ServiceException;

    /**
     * 查看服务器信息。
     *
     * @param Id 服务器号。
     * @return 查询结果。
     */
    ServerInfoQuery view(@Parameter(name = "id") Integer id) throws ServiceException;
}

package org.dangcat.net.process.service;

import org.dangcat.framework.exception.ServiceException;

/**
 * 数据处理前流程服务接口。
 * @author dangcat
 * 
 * @param <T>
 */
public interface AfterDataProcess<T>
{
    /**
     * 处理数据接口。
     * @param data 数据对象。
     * @throws ServiceException 处理异常。
     */
    void afterProcess(T data) throws ServiceException;
}

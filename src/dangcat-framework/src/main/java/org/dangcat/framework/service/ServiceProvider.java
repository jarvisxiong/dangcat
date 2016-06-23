package org.dangcat.framework.service;

/**
 * 服务提供者。
 * @author dangcat
 * 
 */
public interface ServiceProvider
{
    /**
     * 根据类型获取服务对象。
     * @param classType 服务类型。
     * @return 服务对象。
     */
    <T> T getService(Class<T> classType);
}

package org.dangcat.framework.service;

import org.dangcat.framework.service.impl.ServiceInfo;

/**
 * 服务Bean的提供者。
 * @author dangcat
 * 
 */
public interface ServiceLocator extends ServiceProvider
{
    /**
     * 根据绑定名读取服务信息。
     * @param <T>
     * @param jndiName 绑定名。
     * @return 服务信息。
     */
    ServiceInfo getServiceInfo(String jndiName);

    /**
     * 根据绑定名读取服务实例。
     * @param <T>
     * @param jndiName 绑定名。
     * @return 服务实例。
     */
    <T> T lookup(String jndiName);
}

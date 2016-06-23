package org.dangcat.net.process.service.impl;

import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.net.rfc.PacketSession;

/**
 * 报文处理服务基础类。
 * @author dangcat
 * 
 */
public class PacketSessionProcessServiceImpl<T extends PacketSession<?>> extends SessionProcessServiceBase<T>
{
    /**
     * 构建服务。
     * @param parent
     */
    public PacketSessionProcessServiceImpl(ServiceProvider parent)
    {
        super(parent);
    }
}

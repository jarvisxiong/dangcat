package org.dangcat.net.service;

import java.io.IOException;
import java.net.InetAddress;

public interface NetSendService
{
    /**
     * 向指定的地址和端口发送报文。
     * @param remoteAddress 目标地址。
     * @param remotePort 目标端口。
     * @param dataBuffer 发送数据。
     * @throws IOException
     */
    void send(InetAddress remoteAddress, Integer remotePort, byte[] dataBuffer) throws IOException;

}

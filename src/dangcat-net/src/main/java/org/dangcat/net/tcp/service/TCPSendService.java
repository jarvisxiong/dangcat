package org.dangcat.net.tcp.service;

import org.dangcat.net.service.NetSendService;

import java.io.IOException;
import java.net.InetAddress;

public interface TCPSendService extends NetSendService {
    /**
     * 向指定的地址和端口发送报文。
     *
     * @param remoteAddress 目标地址。
     * @param remotePort    目标端口。
     * @param dataBuffer    发送数据。
     * @param tryTimes      尝试接收回应次数。
     * @param timeout       回应等待时间。
     * @return 是否成功。
     * @throws IOException
     */
    void send(InetAddress remoteAddress, Integer remotePort, byte[] dataBuffer, int tryTimes) throws IOException;
}

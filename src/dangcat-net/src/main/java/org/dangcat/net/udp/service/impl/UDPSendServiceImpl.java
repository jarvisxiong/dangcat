package org.dangcat.net.udp.service.impl;

import org.dangcat.framework.pool.ObjectPool;
import org.dangcat.net.event.DatagramEvent;
import org.dangcat.net.udp.service.UDPSendService;
import org.dangcat.net.udp.service.UDPSender;

import java.io.IOException;
import java.net.InetAddress;

/**
 * UDP报文发送服务。
 *
 * @author fanst174766
 */
public class UDPSendServiceImpl extends ObjectPool<UDPSender> implements UDPSendService {
    @Override
    protected void close(UDPSender udpSender) {
        udpSender.close();
    }

    @Override
    protected UDPSender create() {
        return new UDPSender();
    }

    /**
     * 向指定的地址和端口发送报文。
     *
     * @param remoteAddress 目标地址。
     * @param remotePort    目标端口。
     * @param dataBuffer    发送数据。
     * @return 是否成功。
     * @throws IOException
     */
    public void send(InetAddress remoteAddress, Integer remotePort, byte[] dataBuffer) throws IOException {
        this.send(remoteAddress, remotePort, dataBuffer, 2, 0);
    }

    /**
     * 向指定的地址和端口发送报文，并接收回应报文。
     *
     * @param remoteAddress 目标地址。
     * @param remotePort    目标端口。
     * @param dataBuffer    发送数据。
     * @param tryTimes      尝试接收回应次数。
     * @return 是否成功。
     * @throws IOException
     */
    public DatagramEvent send(InetAddress remoteAddress, Integer remotePort, byte[] dataBuffer, int tryTimes, int timeout) throws IOException {
        DatagramEvent replyEvent = null;
        UDPSender udpSender = this.poll();
        if (udpSender != null) {
            try {
                if (tryTimes > 0)
                    replyEvent = udpSender.send(remoteAddress, remotePort, dataBuffer, tryTimes, timeout);
                else
                    udpSender.send(remoteAddress, remotePort, dataBuffer);
            } finally {
                this.release(udpSender);
            }
        }
        return replyEvent;
    }
}

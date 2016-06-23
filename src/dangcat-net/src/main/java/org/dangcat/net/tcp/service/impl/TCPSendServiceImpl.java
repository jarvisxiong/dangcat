package org.dangcat.net.tcp.service.impl;

import org.dangcat.framework.pool.ObjectPool;
import org.dangcat.net.tcp.service.TCPSendService;
import org.dangcat.net.tcp.service.TCPSender;

import java.io.IOException;
import java.net.InetAddress;

/**
 * UDP报文发送服务。
 * @author fanst174766
 * 
 */
public class TCPSendServiceImpl extends ObjectPool<TCPSender> implements TCPSendService
{
    @Override
    protected void close(TCPSender tcpSender)
    {
        tcpSender.close();
    }

    @Override
    protected TCPSender create()
    {
        return new TCPSender(false);
    }

    /**
     * 向指定的地址和端口发送报文。
     * @param remoteAddress 目标地址。
     * @param remotePort 目标端口。
     * @param dataBuffer 发送数据。
     * @return 是否成功。
     * @throws IOException
     */
    public void send(InetAddress remoteAddress, Integer remotePort, byte[] dataBuffer) throws IOException
    {
        this.send(remoteAddress, remotePort, dataBuffer, 2);
    }

    /**
     * 向指定的地址和端口发送报文，并接收回应报文。
     * @param remoteAddress 目标地址。
     * @param remotePort 目标端口。
     * @param dataBuffer 发送数据。
     * @param tryTimes 尝试接收回应次数。
     * @return 是否成功。
     * @throws IOException
     */
    public void send(InetAddress remoteAddress, Integer remotePort, byte[] dataBuffer, int tryTimes) throws IOException
    {
        TCPSender tcpSender = this.poll();
        if (tcpSender != null)
        {
            try
            {
                if (tryTimes > 0)
                    tcpSender.send(remoteAddress, remotePort, dataBuffer, tryTimes);
                else
                    tcpSender.send(remoteAddress, remotePort, dataBuffer);
            }
            finally
            {
                this.release(tcpSender);
            }
        }
    }
}

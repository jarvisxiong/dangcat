package org.dangcat.net.rfc.service.impl;

import org.dangcat.boot.service.DataProcessService;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.net.event.DatagramEvent;
import org.dangcat.net.rfc.PacketSession;
import org.dangcat.net.udp.conf.ListenerConfig;
import org.dangcat.net.udp.service.UDPSendService;
import org.dangcat.net.udp.service.UDPSender;

import java.io.IOException;
import java.net.InetAddress;

/**
 * 回应报文服务。
 * @author dangcat
 * 
 * @param <T>
 */
public class ReplyPacketServiceImpl extends ServiceBase implements DataProcessService<PacketSession<?>>
{
    private UDPSender udpSender = null;

    public ReplyPacketServiceImpl(ServiceProvider parent)
    {
        super(parent);
    }

    @Override
    public void process(PacketSession<?> packetSession) throws ServiceException
    {
        if (packetSession.getResponsePacket() != null)
        {
            packetSession.createResponseEvent();
            if (packetSession.getResponseEvent() != null)
            {
                DatagramEvent replyEvent = this.send(packetSession.getResponseEvent(), packetSession);
                if (replyEvent != null)
                    packetSession.setReplyEvent(replyEvent);
            }
        }
    }

    private synchronized DatagramEvent send(DatagramEvent datagramEvent, ListenerConfig listenerConfig, boolean isNeedReply) throws IOException
    {
        if (this.udpSender == null)
            this.udpSender = new UDPSender(listenerConfig.getReplyPort());
        return this.send(this.udpSender, datagramEvent, listenerConfig, isNeedReply);
    }

    /**
     * 发送回应报文。
     * @param datagramEvent 报文对象。
     * @throws ServiceException
     */
    private DatagramEvent send(DatagramEvent datagramEvent, PacketSession<?> packetSession) throws ServiceException
    {
        DatagramEvent replyDatagramEvent = null;
        if (datagramEvent != null)
        {
            try
            {
                InetAddress remoteAddress = datagramEvent.getRemoteAddress();
                Integer remotePort = datagramEvent.getRemotePort();
                if (remoteAddress != null && remotePort != null)
                {
                    ListenerConfig listenerConfig = packetSession.getListenerConfig();
                    if (listenerConfig.getReplyPort() != null)
                    {
                        if (listenerConfig.getReplyPort().equals(listenerConfig.getPort()))
                            packetSession.getRequestEvent().reply(datagramEvent.getDataBuffer());
                        else
                            replyDatagramEvent = this.send(datagramEvent, listenerConfig, packetSession.isNeedReply());
                    }
                    else
                    {
                        UDPSendService udpSendService = this.getService(UDPSendService.class);
                        replyDatagramEvent = this.send(udpSendService, datagramEvent, listenerConfig, packetSession.isNeedReply());
                    }
                }
            }
            catch (IOException e)
            {
                throw new ServiceException(e);
            }
        }
        return replyDatagramEvent;
    }

    private DatagramEvent send(UDPSendService udpSendService, DatagramEvent datagramEvent, ListenerConfig listenerConfig, boolean isNeedReply) throws IOException
    {
        DatagramEvent replyDatagramEvent = null;
        InetAddress remoteAddress = datagramEvent.getRemoteAddress();
        Integer remotePort = datagramEvent.getRemotePort();
        byte[] dataBuffer = datagramEvent.getDataBuffer();
        if (isNeedReply)
        {
            int tryTimes = listenerConfig.getTryTimes();
            int timeout = listenerConfig.getTimeout();
            replyDatagramEvent = udpSendService.send(remoteAddress, remotePort, dataBuffer, tryTimes, timeout);
        }
        else
            udpSendService.send(remoteAddress, remotePort, dataBuffer);
        return replyDatagramEvent;
    }
}

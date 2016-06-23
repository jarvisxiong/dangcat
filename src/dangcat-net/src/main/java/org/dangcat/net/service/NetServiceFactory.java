package org.dangcat.net.service;

import org.dangcat.net.event.DatagramReceiveListener;
import org.dangcat.net.tcp.service.TCPListener;
import org.dangcat.net.tcp.service.TCPSender;
import org.dangcat.net.tcp.service.impl.TCPSendServiceImpl;
import org.dangcat.net.udp.service.UDPListener;
import org.dangcat.net.udp.service.UDPSender;
import org.dangcat.net.udp.service.impl.UDPSendServiceImpl;

public class NetServiceFactory
{
    public static NetListener createNetListener(NetType netType, String serviceName, Integer port, DatagramReceiveListener datagramReceiveListener)
    {
        NetListener netListener = null;
        if (NetType.TCP.equals(netType))
            netListener = new TCPListener(serviceName, port, datagramReceiveListener);
        else if (NetType.UDP.equals(netType))
            netListener = new UDPListener(serviceName, port, datagramReceiveListener);
        return netListener;
    }

    public static NetSender createNetSender(NetType netType)
    {
        NetSender netSender = null;
        if (NetType.TCP.equals(netType))
            netSender = new TCPSender();
        else if (NetType.UDP.equals(netType))
            netSender = new UDPSender();
        return netSender;
    }

    public static NetSendService createNetSendService(NetType netType)
    {
        NetSendService netSendService = null;
        if (NetType.TCP.equals(netType))
            netSendService = new TCPSendServiceImpl();
        else if (NetType.UDP.equals(netType))
            netSendService = new UDPSendServiceImpl();
        return netSendService;
    }
}

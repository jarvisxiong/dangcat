package org.dangcat.net.udp.service;

import org.dangcat.net.event.DatagramEvent;
import org.dangcat.net.service.NetSender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 * UDP报文发送器。
 * @author fanst174766
 * 
 */
public class UDPSender extends NetSender implements UDPSendService
{
    private byte[] buffer = new byte[1024];
    private DatagramSocket datagramSocket;
    private boolean needReply = false;
    private DatagramEvent replyEvent = null;
    private int timeout = 0;

    public UDPSender()
    {
    }

    public UDPSender(Integer remotePort)
    {
        this.setRemotePort(remotePort);
    }

    @Override
    public void close()
    {
        if (this.datagramSocket != null)
        {
            this.datagramSocket.close();
            this.datagramSocket = null;
        }
    }

    private DatagramSocket getDatagramSocket() throws SocketException
    {
        if (this.datagramSocket == null)
        {
            try
            {
                if (this.getLocalPort() == null)
                    this.datagramSocket = new DatagramSocket();
                else
                    this.datagramSocket = new DatagramSocket(this.getLocalPort());
            }
            catch (SocketException e)
            {
                // 关闭套接口
                if (this.datagramSocket != null)
                    this.datagramSocket.close();
                throw e;
            }
        }
        if (this.timeout > 0)
            this.datagramSocket.setSoTimeout(this.timeout);
        return this.datagramSocket;
    }

    private void receivePacket(DatagramSocket datagramSocket) throws IOException
    {
        DatagramPacket receivePacket = new DatagramPacket(this.buffer, this.buffer.length);
        Arrays.fill(this.buffer, (byte) 0);
        receivePacket.setLength(this.buffer.length);
        datagramSocket.receive(receivePacket);
        if (receivePacket.getLength() > 0)
        {
            byte[] receivedBytes = new byte[receivePacket.getLength()];
            System.arraycopy(receivePacket.getData(), 0, receivedBytes, 0, receivedBytes.length);

            InetAddress localAddress = this.datagramSocket.getLocalAddress();
            int localPort = this.datagramSocket.getLocalPort();
            this.replyEvent = new DatagramEvent(receivePacket.getAddress(), receivePacket.getPort(), localAddress, localPort, receivedBytes);
        }
    }

    @Override
    public void send(byte[] dataBuffer) throws IOException
    {
        this.replyEvent = null;

        try
        {
            DatagramSocket datagramSocket = this.getDatagramSocket();
            if (datagramSocket != null)
            {
                DatagramPacket sendPacket = new DatagramPacket(dataBuffer, dataBuffer.length, this.getRemoteAddress(), this.getRemotePort());
                datagramSocket.send(sendPacket);

                if (this.needReply)
                    this.receivePacket(datagramSocket);
            }
        }
        finally
        {
            if (this.isAutoClose())
                this.close();
        }
    }

    /**
     * 向指定地址和端口发送报文。
     * @throws IOException
     */
    @Override
    public void send(InetAddress remoteAddress, Integer remotePort, byte[] dataBuffer) throws IOException
    {
        this.timeout = 0;
        this.needReply = false;
        super.send(remoteAddress, remotePort, dataBuffer);
    }

    /**
     * 等待接收回应报文。
     * @throws IOException
     */
    public DatagramEvent send(InetAddress remoteAddress, Integer remotePort, byte[] dataBuffer, int tryTimes, int timeout) throws IOException
    {
        this.timeout = timeout;
        this.needReply = true;
        this.send(remoteAddress, remotePort, dataBuffer, tryTimes);
        return this.replyEvent;
    }
}

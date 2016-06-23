package org.dangcat.net.udp.service;

import org.dangcat.net.event.DatagramEvent;
import org.dangcat.net.event.DatagramReceiveListener;
import org.dangcat.net.service.NetListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * UDP报文端口侦听。
 * @author fanst174766
 * 
 */
public class UDPListener extends NetListener
{
    private int bufferSize = 90000;
    private DatagramSocket datagramSocket;

    public UDPListener(String serviceName, Integer port, DatagramReceiveListener datagramReceiveListener)
    {
        super(serviceName, port, datagramReceiveListener);
    }

    public int getBufferSize()
    {
        return this.bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    protected void innerRun()
    {
    }

    /**
     * 启动侦听线程。
     * @throws IOException
     */
    @Override
    protected void innerStartListener() throws IOException
    {
        DatagramSocket datagramSocket = null;
        try
        {
            InetAddress bindAddress = this.getBindAddress();
            if (bindAddress != null)
                datagramSocket = new DatagramSocket(this.getPort(), bindAddress);
            else
                datagramSocket = new DatagramSocket(this.getPort());
            datagramSocket.setSoTimeout(this.getSoTimeout());
            this.datagramSocket = datagramSocket;
        }
        catch (IOException e)
        {
            // 关闭套接口
            if (datagramSocket != null)
                datagramSocket.close();
            throw e;
        }
    }

    /**
     * 停止侦听线程。
     */
    @Override
    protected void innerStopListener()
    {
        if (this.datagramSocket != null)
            this.datagramSocket.close();
        this.datagramSocket = null;
    }

    @Override
    public void run()
    {
        byte[] buffer = new byte[this.getBufferSize()];
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);

        InetAddress localAddress = this.datagramSocket.getLocalAddress();
        int localPort = this.datagramSocket.getLocalPort();
        while (this.isRunning())
        {
            try
            {
                // 接收，阻塞
                this.datagramSocket.receive(datagramPacket);
                if (datagramPacket.getLength() > 0)
                {
                    // 根据接收到的字节数组生成相应的字符串
                    byte[] receivedBytes = new byte[datagramPacket.getLength()];
                    System.arraycopy(datagramPacket.getData(), 0, receivedBytes, 0, receivedBytes.length);

                    this.getDatagramReceiveListener().onReceive(new DatagramEvent(datagramPacket.getAddress(), datagramPacket.getPort(), localAddress, localPort, receivedBytes, this.datagramSocket));
                }
            }
            catch (SocketTimeoutException e)
            {
            }
            catch (IOException e)
            {
                this.logger.error("receive at " + this.getPort(), e);
            }
        }
    }
}

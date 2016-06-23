package org.dangcat.net.service;

import org.dangcat.commons.utils.ValueUtils;

import java.io.IOException;
import java.net.InetAddress;

public abstract class NetSender
{
    private boolean autoClose = true;
    private InetAddress localAddress = null;
    private Integer localPort = null;
    private InetAddress remoteAddress = null;
    private Integer remotePort = null;

    public abstract void close();

    public InetAddress getLocalAddress()
    {
        return this.localAddress;
    }

    public void setLocalAddress(InetAddress localAddress) {
        if (ValueUtils.compare(this.localAddress, localAddress) != 0)
            this.close();
        this.localAddress = localAddress;
    }

    public Integer getLocalPort()
    {
        return this.localPort;
    }

    public void setLocalPort(Integer localPort) {
        if (ValueUtils.compare(this.localPort, localPort) != 0)
            this.close();
        this.localPort = localPort;
    }

    public InetAddress getRemoteAddress()
    {
        return this.remoteAddress;
    }

    public void setRemoteAddress(InetAddress remoteAddress) {
        if (ValueUtils.compare(this.remoteAddress, remoteAddress) != 0)
            this.close();
        this.remoteAddress = remoteAddress;
    }

    public Integer getRemotePort()
    {
        return this.remotePort;
    }

    public void setRemotePort(Integer remotePort) {
        if (ValueUtils.compare(this.remotePort, remotePort) != 0)
            this.close();
        this.remotePort = remotePort;
    }

    public boolean isAutoClose()
    {
        return this.autoClose;
    }

    public void setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
    }

    public abstract void send(byte[] dataBuffer) throws IOException;

    public void send(byte[] dataBuffer, int tryTimes) throws IOException
    {
        IOException ioException = null;
        boolean success = false;
        while (tryTimes > 0 && !success)
        {
            try
            {
                this.send(dataBuffer);
                success = true;
            }
            catch (IOException e)
            {
                ioException = e;
                this.close();
            }
            finally
            {
                tryTimes--;
            }
        }
        if (!success && ioException != null)
            throw ioException;
    }

    /**
     * 向指定地址和端口发送报文。
     * @throws IOException
     */
    public void send(InetAddress remoteAddress, Integer remotePort, byte[] dataBuffer) throws IOException
    {
        this.setRemoteAddress(remoteAddress);
        this.setRemotePort(remotePort);
        this.send(dataBuffer);
    }

    /**
     * 等待接收回应报文。
     * @throws IOException
     */
    public void send(InetAddress remoteAddress, Integer remotePort, byte[] dataBuffer, int tryTimes) throws IOException
    {
        this.setRemoteAddress(remoteAddress);
        this.setRemotePort(remotePort);
        this.send(dataBuffer, tryTimes);
    }
}

package org.dangcat.net.tcp.service;

import org.dangcat.net.service.NetSender;

import java.io.IOException;
import java.net.Socket;

/**
 * UDP±¨ÎÄ·¢ËÍÆ÷¡£
 * @author fanst174766
 * 
 */
public class TCPSender extends NetSender implements TCPSendService
{
    private SocketProcess socketProcess = null;

    public TCPSender()
    {
    }

    public TCPSender(boolean autoClose)
    {
        this.setAutoClose(autoClose);
    }

    @Override
    public void close()
    {
        if (this.socketProcess != null)
            this.socketProcess.close();
        this.socketProcess = null;
    }

    private SocketProcess getSocketProcess() throws IOException
    {
        if (this.socketProcess == null)
        {
            Socket socket = null;
            if (this.getLocalAddress() != null && this.getLocalPort() != null)
                socket = new Socket(this.getRemoteAddress(), this.getRemotePort(), this.getLocalAddress(), this.getLocalPort());
            else
                socket = new Socket(this.getRemoteAddress(), this.getRemotePort());
            this.socketProcess = new SocketProcess(socket);
        }
        return this.socketProcess;
    }

    @Override
    public void send(byte[] dataBuffer) throws IOException
    {
        try
        {
            SocketProcess socketProcess = this.getSocketProcess();
            if (socketProcess != null)
                socketProcess.sendData(dataBuffer);
        }
        finally
        {
            if (this.isAutoClose())
                this.close();
        }
    }
}

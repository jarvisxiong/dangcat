package org.dangcat.net.tcp.service;

import org.dangcat.net.event.DatagramReceiveListener;
import org.dangcat.net.service.NetListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * UDP报文端口侦听。
 * @author fanst174766
 * 
 */
public class TCPListener extends NetListener
{
    private ServerSocket serverSocket;

    public TCPListener(String serviceName, Integer port, DatagramReceiveListener datagramReceiveListener)
    {
        super(serviceName, port, datagramReceiveListener);
    }

    private SocketAddress getServerBindAddress()
    {
        SocketAddress socketAddress = null;
        if (this.getBindAddress() != null)
            socketAddress = new InetSocketAddress(this.getBindAddress(), this.getPort());
        return socketAddress;
    }

    @Override
    protected void innerRun() throws IOException
    {
        Socket socket = this.serverSocket.accept();
        new SocketListenHandler(socket, this.getDatagramReceiveListener()).start();
    }

    @Override
    public void innerStartListener() throws IOException
    {
        ServerSocket serverSocket = null;
        try
        {
            serverSocket = new ServerSocket(this.getPort());
            SocketAddress serverBindAddress = this.getServerBindAddress();
            if (serverBindAddress != null)
                serverSocket.bind(serverBindAddress);
            serverSocket.setSoTimeout(this.getSoTimeout());
            this.serverSocket = serverSocket;
        }
        catch (IOException e)
        {
            try
            {
                if (serverSocket != null)
                    serverSocket.close();
            }
            catch (IOException e1)
            {
            }
            throw e;
        }
    }

    /**
     * 停止侦听线程。
     */
    @Override
    protected void innerStopListener()
    {
        try
        {
            if (this.serverSocket != null)
                this.serverSocket.close();
        }
        catch (IOException e)
        {
        }
        this.serverSocket = null;
    }
}

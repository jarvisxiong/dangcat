package org.dangcat.net.service;

import org.apache.log4j.Logger;
import org.dangcat.net.event.DatagramReceiveListener;

import java.io.IOException;
import java.net.InetAddress;

public abstract class NetListener extends Thread
{
    private static final String LISTENER_NAMPREFIX = "-Listener-";
    protected Logger logger = Logger.getLogger(this.getClass());
    protected String serviceName = null;
    private InetAddress bindAddress = null;
    private DatagramReceiveListener datagramReceiveListener = null;
    private boolean isRunning = false;
    private Integer port = null;
    private int soTimeout = 1000;

    public NetListener(String serviceName, Integer port, DatagramReceiveListener datagramReceiveListener)
    {
        super(serviceName + LISTENER_NAMPREFIX + port);
        this.serviceName = serviceName;
        this.datagramReceiveListener = datagramReceiveListener;
        this.port = port;
    }

    public InetAddress getBindAddress()
    {
        return this.bindAddress;
    }

    public void setBindAddress(InetAddress bindAddress) {
        this.bindAddress = bindAddress;
    }

    public DatagramReceiveListener getDatagramReceiveListener()
    {
        return this.datagramReceiveListener;
    }

    public Integer getPort()
    {
        return this.port;
    }

    public String getServiceName()
    {
        return this.serviceName;
    }

    public int getSoTimeout()
    {
        return this.soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    protected abstract void innerRun() throws IOException;

    protected abstract void innerStartListener() throws IOException;

    protected abstract void innerStopListener();

    public boolean isRunning()
    {
        return this.isRunning;
    }

    /**
     * 启动侦听线程。
     */
    @Override
    public void run()
    {
        while (this.isRunning())
        {
            try
            {
                this.innerRun();
            }
            catch (IOException ex)
            {
            }
        }
    }

    public boolean startListener()
    {
        if (this.isRunning())
            return true;

        InetAddress bindAddress = this.getBindAddress();
        try
        {
            this.innerStartListener();
            this.isRunning = true;
            this.logger.info(this.getServiceName() + " start listener port at " + this.getPort() + (bindAddress == null ? "" : " bind address " + bindAddress));

            this.start();
        }
        catch (IOException e)
        {
            this.isRunning = false;
            this.logger.error(this.getServiceName() + " start listener port at " + this.getPort(), e);
        }
        return this.isRunning();
    }

    /**
     * 停止侦听线程。
     */
    public void stopListener()
    {
        if (!this.isRunning())
            return;

        this.isRunning = false;
        try
        {
            this.join();
            this.logger.info(this.serviceName + " stop listener port at " + this.port + ".");
        }
        catch (InterruptedException e)
        {
            this.logger.error(this.serviceName + " stop listener port at " + this.port, e);
        }
        finally
        {
            this.innerStopListener();
        }
    }
}

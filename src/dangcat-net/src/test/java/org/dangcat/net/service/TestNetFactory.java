package org.dangcat.net.service;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.net.event.DatagramEvent;
import org.dangcat.net.event.DatagramReceiveListener;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;

public class TestNetFactory implements DatagramReceiveListener
{
    private static final int PORT = 1541;
    private static final String SERVICE_NAME = "TESTNET";
    private NetListener netListener = null;
    private NetSender netSender = null;
    private byte[] receiveData = null;

    @After
    public void afterTest()
    {
        if (this.netSender != null)
            this.netSender.close();

        if (this.netListener != null)
            this.netListener.stopListener();
    }

    private byte[] createTestData(int length)
    {
        byte[] buffer = new byte[length];
        for (int i = 0; i < length; i++)
            buffer[i] = (byte) i;
        return buffer;
    }

    @Override
    public void onReceive(DatagramEvent datagramEvent)
    {
        this.receiveData = datagramEvent.getDataBuffer();
    }

    private void testNet(NetType netType, int length) throws Exception
    {
        this.netListener = NetServiceFactory.createNetListener(netType, SERVICE_NAME, PORT, this);
        this.netListener.startListener();

        this.netSender = NetServiceFactory.createNetSender(netType);
        this.netSender.setRemoteAddress(InetAddress.getLocalHost());
        this.netSender.setRemotePort(PORT);

        byte[] sendData = this.createTestData(length);
        this.netSender.send(sendData);

        while (this.receiveData == null)
            Thread.sleep(100l);

        Assert.assertNotNull(this.receiveData);
        Assert.assertTrue(ValueUtils.compare(sendData, this.receiveData) == 0);
    }

    @Test
    public void testTCP() throws Exception
    {
        this.testNet(NetType.TCP, 60 * 1024 * 1024);
    }

    @Test
    public void testUDP() throws Exception
    {
        this.testNet(NetType.UDP, 60 * 1024);
    }
}

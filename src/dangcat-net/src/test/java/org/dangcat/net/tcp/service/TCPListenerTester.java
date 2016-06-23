package org.dangcat.net.tcp.service;

import org.dangcat.commons.io.FileUtils;
import org.dangcat.net.event.DatagramEvent;
import org.dangcat.net.event.DatagramReceiveListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class TCPListenerTester implements DatagramReceiveListener
{
    private static final int PORT = 1541;
    private static String outputFile = "E:\\Setup\\database\\Oracle\\Tools\\sqldeveloper-2.1.0.63.73-no-jre";
    private int count = 0;

    public static void main(String[] args)
    {
        new TCPListenerTester().run();
    }

    @Override
    public void onReceive(DatagramEvent datagramEvent)
    {
        BufferedOutputStream outputStream = null;
        try
        {
            byte[] data = datagramEvent.getDataBuffer();
            if (data != null && data.length > 0)
            {
                outputStream = new BufferedOutputStream(new FileOutputStream(new File(outputFile + (this.count++) + ".zip")));
                outputStream.write(data);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            FileUtils.close(outputStream);
        }
    }

    private void run()
    {
        TCPListener tcpListener = new TCPListener("TCP", PORT, this);
        tcpListener.startListener();
    }
}

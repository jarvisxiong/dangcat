package org.dangcat.net.udp.service;

import org.dangcat.net.event.DatagramEvent;
import org.dangcat.net.event.DatagramReceiveListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UDPListenerTester implements DatagramReceiveListener
{
    private static final int PORT = 1541;
    private static File outputFile = new File("E:\\Setup\\database\\Oracle\\Tools\\sqldeveloper-2.1.0.63.73-no-jre2.zip");

    public static void main(String[] args)
    {
        new UDPListenerTester().run();
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
                outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
                outputStream.write(data);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                outputStream.close();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void run()
    {
        UDPListener udpListener = new UDPListener("UDP", PORT, this);
        udpListener.startListener();
    }
}

package org.dangcat.net.udp.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;

public class UDPSenderTester
{
    private static final int PORT = 1541;
    private static File inputFile = new File("E:\\Setup\\database\\dbeaver\\dbeaver-2.3.5-linux.gtk.x86.zip");

    public static void main(String[] args) throws Exception
    {
        new UDPSenderTester().run();
    }

    private byte[] getData()
    {
        byte[] data = null;
        // new byte[64 * 1024 - 100];
        // for (int i = 0; i < data.length; i++)
        // data[i] = (byte) i;

        try
        {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(inputFile));
            int length = bufferedInputStream.available();
            if (length > 0)
            {
                data = new byte[length];
                bufferedInputStream.read(data);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return data;
    }

    private void run() throws Exception
    {
        byte[] dataBuffer = this.getData();
        if (dataBuffer != null && dataBuffer.length > 0)
        {
            UDPSender udpSender = new UDPSender();
            InetAddress inetAddress = InetAddress.getLocalHost();
            udpSender.send(inetAddress, PORT, dataBuffer);
        }
    }
}

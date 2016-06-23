package org.dangcat.net.tcp.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;

public class TCPSenderTester
{
    private static File inputFile = new File("E:\\Setup\\database\\Oracle\\Tools\\sqldeveloper-2.1.0.63.73-no-jre.zip");
    private static final int PORT = 1541;

    public static void main(String[] args) throws Exception
    {
        new TCPSenderTester().run();
    }

    private byte[] getData()
    {
        byte[] data = null;
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
            TCPSender tcpSender = new TCPSender();
            InetAddress inetAddress = InetAddress.getLocalHost();
            tcpSender.send(inetAddress, PORT, dataBuffer);
            tcpSender.send(inetAddress, PORT, dataBuffer);
        }
    }
}

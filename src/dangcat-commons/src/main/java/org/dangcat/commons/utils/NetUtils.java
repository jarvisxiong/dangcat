package org.dangcat.commons.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetUtils
{
    public static void checkPortValid(String ip, Integer port) throws IOException
    {
        if (port != null)
        {
            Socket socket = new Socket();
            socket.bind(new InetSocketAddress(ip, port));
            socket.close();
        }
    }

    public static boolean isInetAddress(String ip)
    {
        return InetAddressUtils.isInetAddress(ip);
    }

    /**
     * 判断本地端口是否有效。
     * @param port 端口号。
     * @return 端口是否有效。
     */
    public static boolean isPortValid(Integer port)
    {
        return isPortValid("127.0.0.1", port) && isPortValid("0.0.0.0", port);
    }

    /**
     * 判断远程端口是否有效。
     * @param ip 远程地址。
     * @param port 端口号。
     * @return 端口是否有效。
     */
    public static boolean isPortValid(String ip, Integer port)
    {
        boolean result = false;
        try
        {
            if (port != null)
            {
                checkPortValid(ip, port);
                result = true;
            }
        }
        catch (IOException e)
        {
        }
        return result;
    }

    /**
     * 解析地址。
     * @param ip 地址。
     * @return 地址对象。
     */
    public static InetAddress toInetAddress(String ip) throws UnknownHostException
    {
        return InetAddressUtils.toInetAddress(ip);
    }
}

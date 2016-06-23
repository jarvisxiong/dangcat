package org.dangcat.commons.utils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import sun.net.util.IPAddressUtil;

public class InetAddressUtils
{
    private static Pattern IPV4_PATTERN = Pattern
            .compile("(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})");

    public static boolean isInet4Address(String ipAddress)
    {
        boolean result = false;
        try
        {
            InetAddress InetAddress = toInetAddress(ipAddress);
            result = InetAddress instanceof Inet4Address;
        }
        catch (UnknownHostException e)
        {
        }
        return result;
    }

    private static boolean isInet4AddressText(String ipAddress)
    {
        return IPV4_PATTERN.matcher(ipAddress).matches();
    }

    public static boolean isInet6Address(String host)
    {
        boolean result = false;
        try
        {
            InetAddress InetAddress = toInetAddress(host);
            result = InetAddress instanceof Inet6Address;
        }
        catch (UnknownHostException e)
        {
        }
        return result;
    }

    public static boolean isInetAddress(String ipAddress)
    {
        boolean result = false;
        try
        {
            result = toInetAddress(ipAddress) != null;
        }
        catch (UnknownHostException e)
        {
        }
        return result;
    }

    public static InetAddress toInetAddress(String ipAddress) throws UnknownHostException
    {
        InetAddress inetAddress = null;
        if (ValueUtils.isEmpty(ipAddress))
            return inetAddress;

        boolean ipv6Expected = false;
        if (ipAddress.charAt(0) == '[')
        {
            // This is supposed to be an IPv6 literal
            if (ipAddress.length() > 2 && ipAddress.charAt(ipAddress.length() - 1) == ']')
            {
                ipAddress = ipAddress.substring(1, ipAddress.length() - 1);
                ipv6Expected = true;
            }
            else
                // This was supposed to be a IPv6 address, but it's not!
                throw new UnknownHostException(ipAddress + ": invalid IPv6 address");
        }

        // if host is an IP address, we won't do further lookup
        if (Character.digit(ipAddress.charAt(0), 16) != -1 || (ipAddress.charAt(0) == ':'))
        {
            // see if it is IPv4 address
            byte[] addresBytes = null;
            if (isInet4AddressText(ipAddress))
                addresBytes = IPAddressUtil.textToNumericFormatV4(ipAddress);
            if (addresBytes == null)
                addresBytes = IPAddressUtil.textToNumericFormatV6(ipAddress);
            else if (ipv6Expected)
                // Means an IPv4 litteral between brackets!
                throw new UnknownHostException("[" + ipAddress + "]");
            if (addresBytes != null)
                inetAddress = InetAddress.getByAddress(ipAddress, addresBytes);
        }
        else if (ipv6Expected)
            // We were expecting an IPv6 Litteral, but got something else
            throw new UnknownHostException("[" + ipAddress + "]");
        return inetAddress;
    }
}

package org.dangcat.commons.utils;

public enum OSType
{
    Linux, Windows;

    public static OSType getOSType()
    {
        OSType osType = null;
        final String osName = System.getProperty("os.name");
        if (osName.contains(Linux.name()))
            osType = Linux;
        else if (osName.contains(Windows.name()))
            osType = Windows;
        return osType;
    }
}

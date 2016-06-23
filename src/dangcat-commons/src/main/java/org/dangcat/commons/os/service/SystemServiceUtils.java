package org.dangcat.commons.os.service;

import org.dangcat.commons.utils.OSType;

public class SystemServiceUtils {
    private static SystemService systemService = null;

    static {
        OSType osType = OSType.getOSType();
        if (OSType.Linux.equals(osType))
            systemService = new LinuxSystemService();
        else if (OSType.Windows.equals(osType))
            systemService = new WindowsSystemService();
    }

    public static boolean exists(String name) {
        return systemService.exists(name);
    }

    public static boolean isRunning(String name) {
        return systemService.isRunning(name);
    }

    public static boolean remove(String name) {
        return systemService.remove(name);
    }
}

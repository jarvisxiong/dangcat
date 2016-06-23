package org.dangcat.install.service;

import org.dangcat.commons.utils.OSType;
import org.dangcat.install.Installer;

import java.io.File;

public class ServiceInstaller extends Installer
{
    private static final String HOME = "home";

    public ServiceInstaller(String serviceName)
    {
        this.setServiceName(serviceName);
    }

    public String getHome()
    {
        Object home = this.getParams().get(HOME);
        if (home == null)
            home = ".";
        return home.toString();
    }

    public void setHome(String home) {
        this.getParams().put(HOME, home);
    }

    @Override
    protected File getServiceFile()
    {
        return new File(this.getHome() + File.separator + "bin" + File.separator + this.getServiceFileName());
    }

    private String getServiceFileName()
    {
        String serviceFileName = this.getServiceName();
        OSType osType = OSType.getOSType();
        if (OSType.Linux.equals(osType))
            serviceFileName += ".sh";
        else if (OSType.Windows.equals(osType))
            serviceFileName += ".bat";
        return serviceFileName;
    }
}

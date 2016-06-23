package org.dangcat.install.frame.demo;

import org.dangcat.install.frame.UninstallFrameBase;

import java.io.File;

public class UninstallerFrameDemo extends UninstallFrameBase
{
    private static final String DBSERVICE_DISPLAYNAME = "dangcat-radius-database";
    private static final String DBSERVICE_NAME = "radius-database";
    private static final long serialVersionUID = 1L;
    private static final String SERVICE_NAME = "radius-service";

    public static void main(final String[] args)
    {
        UninstallerFrameDemo ininstallerFrameDemo = new UninstallerFrameDemo();
        ininstallerFrameDemo.setCurrentPath(new File("E:/Share/temp/deepdata"));
        show(ininstallerFrameDemo);
    }

    @Override
    protected String getDatabaseServiceDisplayName()
    {
        return DBSERVICE_DISPLAYNAME;
    }

    @Override
    protected String getDatabaseServiceName()
    {
        return DBSERVICE_NAME;
    }

    @Override
    protected String getServiceName()
    {
        return SERVICE_NAME;
    }
}

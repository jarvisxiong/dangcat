package org.dangcat.install.frame.demo;

import java.io.File;

import org.dangcat.install.frame.InstallerFrameBase;

public class InstallerFrameDemo extends InstallerFrameBase
{
    private static final String CONFIG_FILENAME = "radius.server.properties";
    private static final String DATABASE_NAME = "radius";
    private static final String DBSERVICE_DISPLAYNAME = "dsngcat-radius-database";
    private static final String DBSERVICE_NAME = "radius-database";
    private static final Integer DEFAULT_DATABASE_PORT = 3706;
    private static final long serialVersionUID = 1L;
    private static final String SERVICE_NAME = "radius-service";

    public static void main(final String[] args)
    {
        InstallerFrameDemo installerFrameDemo = new InstallerFrameDemo();
        installerFrameDemo.setCurrentPath(new File("E:/Share/temp/deepdata-radius-1.0-SNAPSHOT"));
        show(installerFrameDemo);
    }

    @Override
    protected String getConfigFileName()
    {
        return CONFIG_FILENAME;
    }

    @Override
    protected String getDatabaseName()
    {
        return DATABASE_NAME;
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
    protected Integer getDefaultDatabasePort()
    {
        return DEFAULT_DATABASE_PORT;
    }

    @Override
    protected String getServiceName()
    {
        return SERVICE_NAME;
    }

    @Override
    public void initialize()
    {
        super.initialize();
        this.createFtpConfigModule("radius", this.getText("ftp.radius.title"));
    }
}

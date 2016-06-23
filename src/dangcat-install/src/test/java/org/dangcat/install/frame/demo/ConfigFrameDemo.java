package org.dangcat.install.frame.demo;

import java.io.File;

import org.dangcat.install.frame.ConfigFrameBase;


public class ConfigFrameDemo extends ConfigFrameBase
{
    private static final long serialVersionUID = 1L;

    public static void main(final String[] args)
    {
        show(new ConfigFrameDemo());
    }

    private File configFile = new File("./target/test-classes/META-INF/resource.properties");

    @Override
    protected File getConfigFile()
    {
        return this.configFile;
    }

    @Override
    public void initialize()
    {
        super.initialize();

        this.createDatabaseConfigModule("radius", 3706);
        this.createFtpConfigModule("radius", this.getText("ftp.radius.title"));
    }
}
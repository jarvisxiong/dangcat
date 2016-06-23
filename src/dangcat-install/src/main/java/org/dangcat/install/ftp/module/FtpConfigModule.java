package org.dangcat.install.ftp.module;

import org.dangcat.install.ftp.FtpParameter;
import org.dangcat.install.ftp.swing.FtpConfigPanel;
import org.dangcat.install.module.ProcessModuleBase;


public class FtpConfigModule extends ProcessModuleBase
{
    private FtpConfigAccess ftpConfigAccess = null;
    private FtpConfigPanel ftpConfigPanel = null;
    private FtpParameter ftpParameter = new FtpParameter();

    public FtpConfigModule(String name, String title)
    {
        super(name, title);
        this.ftpParameter.setName(name);
    }

    public void createFtpConfigAccess(String name, String title)
    {
        FtpConfigPanel ftpConfigPanel = this.createFtpConfigPanel(name, title);
        FtpConfigAccess ftpConfigAccess = new FtpConfigAccess();
        ftpConfigAccess.addConfigPanel(name, ftpConfigPanel);
        this.ftpConfigAccess = ftpConfigAccess;
    }

    private FtpConfigPanel createFtpConfigPanel(String name, String title)
    {
        FtpConfigPanel ftpConfigPanel = new FtpConfigPanel();
        ftpConfigPanel.setFtpParameter(this.getFtpParameter());
        ftpConfigPanel.setName(name);
        ftpConfigPanel.setTitle(title);
        ftpConfigPanel.initialize();
        this.addConfigPanel(ftpConfigPanel);
        this.ftpConfigPanel = ftpConfigPanel;
        return ftpConfigPanel;
    }

    public Integer getDefaultPort()
    {
        return this.getFtpParameter().getDefaultPort();
    }

    public void setDefaultPort(int port) {
        this.getFtpParameter().setDefaultPort(port);
    }

    public FtpConfigAccess getFtpConfigAccess()
    {
        return this.ftpConfigAccess;
    }

    public FtpConfigPanel getFtpConfigPanel()
    {
        return this.ftpConfigPanel;
    }

    public FtpParameter getFtpParameter()
    {
        return this.ftpParameter;
    }

    @Override
    public void initialize()
    {
    }

    public void setDefaultFtpName(String defaultName)
    {
        this.getFtpParameter().setDefaultName(defaultName);
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        this.ftpConfigAccess.setEnabled(enabled);
        super.setEnabled(enabled);
    }
}

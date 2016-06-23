package org.dangcat.install.ftp.module;

public abstract class FtpAccess
{
    protected static final String INITPATH = "initpath";
    protected static final String NAME = "name";
    protected static final String PASSWORD = "password";
    protected static final String PORT = "port";
    protected static final String SERVER = "server";
    protected static final String USERNAME = "username";
    private boolean enabled = true;

    protected String getFtpKey(String name, String key)
    {
        return "ftp." + name + "." + key;
    }

    protected String getFtpName(String name)
    {
        return this.getFtpKey(name, NAME);
    }

    protected String getInitPath(String name)
    {
        return this.getFtpKey(name, INITPATH);
    }

    protected String getPasswordKey(String name)
    {
        return this.getFtpKey(name, PASSWORD);
    }

    protected String getPortKey(String name)
    {
        return this.getFtpKey(name, PORT);
    }

    protected String getServerKey(String name)
    {
        return this.getFtpKey(name, SERVER);
    }

    protected String getUserKey(String name)
    {
        return this.getFtpKey(name, USERNAME);
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}

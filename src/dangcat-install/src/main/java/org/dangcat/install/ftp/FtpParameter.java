package org.dangcat.install.ftp;

import org.dangcat.commons.utils.ValueUtils;

public class FtpParameter
{
    private String defaultName = null;
    private Integer defaultPort = 21;
    private String defaultServer = "localhost";
    private String initPath = null;
    private String name = null;
    private String password = null;
    private Integer port = null;
    private String server = "localhost";
    private String user = null;

    public String getDefaultName()
    {
        return this.defaultName;
    }

    public Integer getDefaultPort()
    {
        return this.defaultPort;
    }

    public String getDefaultServer()
    {
        return this.defaultServer;
    }

    public String getInitPath()
    {
        return this.initPath;
    }

    public String getName()
    {
        return ValueUtils.isEmpty(this.name) ? this.getDefaultName() : this.name;
    }

    public String getPassword()
    {
        return this.password;
    }

    public Integer getPort()
    {
        return this.port == null ? this.getDefaultPort() : this.port;
    }

    public String getServer()
    {
        return this.server;
    }

    public String getUser()
    {
        return this.user;
    }

    public void setDefaultName(String defaultName)
    {
        this.defaultName = defaultName;
    }

    public void setDefaultPort(Integer defaultPort)
    {
        this.defaultPort = defaultPort;
    }

    public void setDefaultServer(String defaultServer)
    {
        this.defaultServer = defaultServer;
    }

    public void setInitPath(String initPath)
    {
        this.initPath = initPath;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public void setUser(String user)
    {
        this.user = user;
    }
}

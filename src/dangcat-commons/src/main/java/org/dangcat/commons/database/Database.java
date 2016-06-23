package org.dangcat.commons.database;

import org.dangcat.commons.utils.ValueUtils;

public abstract class Database
{
    private String defaultName = null;
    private Integer defaultPort = null;
    private String defaultServer = "localhost";
    private String defaultUser = null;
    private String name = null;
    private String password = null;
    private Integer port = null;
    private String server = "localhost";
    private boolean usePoolDriver = false;
    private String user = null;

    public abstract DatabaseType getDatabaseType();

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

    public String getDefaultUser()
    {
        return this.defaultUser;
    }

    public abstract String getDriver();

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

    public abstract String getUrl();

    public String getUser()
    {
        return this.user == null ? this.getDefaultUser() : this.user;
    }

    public boolean isUsePoolDriver()
    {
        return this.usePoolDriver;
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

    public void setDefaultUser(String defaultUser)
    {
        this.defaultUser = defaultUser;
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

    public void setUsePoolDriver(boolean usePoolDriver)
    {
        this.usePoolDriver = usePoolDriver;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    @Override
    public String toString()
    {
        return this.getDatabaseType().toString();
    }
}

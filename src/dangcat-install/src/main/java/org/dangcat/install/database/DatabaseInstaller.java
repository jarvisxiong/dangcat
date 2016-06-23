package org.dangcat.install.database;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.install.Installer;

import java.util.LinkedList;
import java.util.List;

public abstract class DatabaseInstaller extends Installer
{
    private static final String CHARACTERSET = "characterSet";
    private static final String DATABASENAME = "databaseName";
    private static final String DEFAULT_DATABASENAME = "default_databasename";
    private static final String DEFAULT_PORT = "default_port";
    private static final String DEFAULT_USER = "default_user";
    private static final String PASSWORD = "password";
    private static final String PORT = "port";
    private static final String SCRIPTS = "scripts";
    private static final String USER = "user";

    public abstract void config() throws Exception;

    public abstract void createDatabase() throws Exception;

    public String getCharacterSet()
    {
        return (String) this.getParams().get(CHARACTERSET);
    }

    public void setCharacterSet(String characterSet) {
        this.getParams().put(CHARACTERSET, characterSet);
    }

    public String getDatabaseName()
    {
        String value = (String) this.getParams().get(DATABASENAME);
        if (ValueUtils.isEmpty(value))
            value = this.getDefaultDatabaseName();
        return value;
    }

    public void setDatabaseName(String databaseName) {
        this.getParams().put(DATABASENAME, databaseName);
    }

    public String getDefaultDatabaseName()
    {
        return (String) this.getParams().get(DEFAULT_DATABASENAME);
    }

    public void setDefaultDatabaseName(String databaseName) {
        this.getParams().put(DEFAULT_DATABASENAME, databaseName);
    }

    public Integer getDefaultPort()
    {
        return (Integer) this.getParams().get(DEFAULT_PORT);
    }

    public void setDefaultPort(int port) {
        this.getParams().put(DEFAULT_PORT, port);
    }

    public String getDefaultUser()
    {
        return (String) this.getParams().get(DEFAULT_USER);
    }

    public void setDefaultUser(String user) {
        this.getParams().put(DEFAULT_USER, user);
    }

    public String getPassword()
    {
        return (String) this.getParams().get(PASSWORD);
    }

    public void setPassword(String password) {
        this.getParams().put(PASSWORD, password);
    }

    public Integer getPort()
    {
        Integer value = (Integer) this.getParams().get(PORT);
        if (value == null)
            value = this.getDefaultPort();
        return value;
    }

    public void setPort(int port) {
        this.getParams().put(PORT, port);
    }

    @SuppressWarnings("unchecked")
    public List<String> getScripts()
    {
        List<String> scripts = (List<String>) this.getParams().get(SCRIPTS);
        if (scripts == null)
        {
            scripts = new LinkedList<String>();
            this.getParams().put(SCRIPTS, scripts);
        }
        return scripts;
    }

    public String getUser()
    {
        String value = (String) this.getParams().get(USER);
        if (ValueUtils.isEmpty(value))
            value = this.getDefaultUser();
        return value;
    }

    public void setUser(String user)
    {
        this.getParams().put(USER, user);
    }

    protected void logDatabase(String key, Object... params)
    {
        this.log(key, this.getDatabaseName(), params);
    }
}
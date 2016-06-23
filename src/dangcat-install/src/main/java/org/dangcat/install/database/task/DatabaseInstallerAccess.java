package org.dangcat.install.database.task;

import org.dangcat.commons.crypto.CryptoUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.install.database.DatabaseInstaller;
import org.dangcat.install.task.ConfigureAccess;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class DatabaseInstallerAccess extends DatabaseAccess implements ConfigureAccess
{
    private Map<String, DatabaseInstaller> databaseInstalls = new LinkedHashMap<String, DatabaseInstaller>();

    public void addDatabaseInstaller(String name, DatabaseInstaller databaseInstaller)
    {
        this.databaseInstalls.put(name, databaseInstaller);
    }

    protected void load(DatabaseInstaller databaseInstaller, Properties properties)
    {
        String name = databaseInstaller.getDatabaseName();
        Integer port = ValueUtils.parseInt(properties.getProperty(this.getPortKey(name)), databaseInstaller.getDefaultPort());
        databaseInstaller.setPort(port);
        databaseInstaller.setDatabaseName(properties.getProperty(this.getDatabaseName(name)));
        String user = properties.getProperty(this.getUserKey(name));
        databaseInstaller.setUser(ValueUtils.isEmpty(user) ? databaseInstaller.getDefaultUser() : user);
        String password = properties.getProperty(this.getPasswordKey(name));
        if (!ValueUtils.isEmpty(password))
            password = CryptoUtils.decrypt(password);
        databaseInstaller.setPassword(password);
    }

    @Override
    public void load(Properties properties)
    {
        for (DatabaseInstaller databaseInstaller : this.databaseInstalls.values())
            this.load(databaseInstaller, properties);
    }

    protected void save(DatabaseInstaller databaseInstaller, Properties properties)
    {
        String name = databaseInstaller.getDatabaseName();
        properties.setProperty(this.getServerKey(name), "127.0.0.1");
        Integer port = databaseInstaller.getPort() == null ? databaseInstaller.getDefaultPort() : databaseInstaller.getPort();
        properties.setProperty(this.getPortKey(name), port.toString());
        properties.setProperty(this.getDatabaseName(name), databaseInstaller.getDatabaseName());
        String user = databaseInstaller.getUser() == null ? databaseInstaller.getDefaultUser() : databaseInstaller.getUser();
        properties.setProperty(this.getUserKey(name), user);
        String password = databaseInstaller.getPassword();
        if (!ValueUtils.isEmpty(password))
            password = CryptoUtils.encrypt(password);
        properties.setProperty(this.getPasswordKey(name), password == null ? "" : password);
    }

    @Override
    public void save(Properties properties)
    {
        for (DatabaseInstaller databaseInstaller : this.databaseInstalls.values())
            this.save(databaseInstaller, properties);
    }
}

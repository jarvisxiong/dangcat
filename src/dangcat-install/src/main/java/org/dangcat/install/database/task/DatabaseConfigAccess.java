package org.dangcat.install.database.task;

import org.dangcat.commons.crypto.CryptoUtils;
import org.dangcat.commons.database.Database;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.install.database.swing.DatabaseConfigPanel;
import org.dangcat.install.task.ConfigureAccess;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class DatabaseConfigAccess extends DatabaseAccess implements ConfigureAccess {
    private Map<String, DatabaseConfigPanel> databaseConfigPanels = new LinkedHashMap<String, DatabaseConfigPanel>();

    public void addDatabaseConfigPanel(String name, DatabaseConfigPanel databaseConfigPanel) {
        this.databaseConfigPanels.put(name, databaseConfigPanel);
    }

    private void load(DatabaseConfigPanel databaseConfigPanel, Properties properties) {
        Database database = databaseConfigPanel.getDatabase();
        String name = database.getName();
        String server = properties.getProperty(this.getServerKey(name));
        database.setServer(ValueUtils.isEmpty(server) ? database.getDefaultServer() : server);
        Integer port = ValueUtils.parseInt(properties.getProperty(this.getPortKey(name)), database.getDefaultPort());
        database.setPort(port);
        database.setName(properties.getProperty(this.getDatabaseName(name)));
        String user = properties.getProperty(this.getUserKey(name));
        database.setUser(ValueUtils.isEmpty(user) ? database.getDefaultUser() : user);
        String password = properties.getProperty(this.getPasswordKey(name));
        if (!ValueUtils.isEmpty(password))
            password = CryptoUtils.decrypt(password);
        database.setPassword(password);
        databaseConfigPanel.update();
    }

    @Override
    public void load(Properties properties) {
        for (DatabaseConfigPanel databaseConfigPanel : this.databaseConfigPanels.values())
            this.load(databaseConfigPanel, properties);
    }

    private void save(DatabaseConfigPanel databaseConfigPanel, Properties properties) {
        Database database = databaseConfigPanel.getDatabase();
        String name = database.getName();
        String server = database.getServer() == null ? database.getDefaultServer() : database.getServer();
        properties.setProperty(this.getServerKey(name), server);
        Integer port = database.getPort() == null ? database.getDefaultPort() : database.getPort();
        properties.setProperty(this.getPortKey(name), port.toString());
        properties.setProperty(this.getDatabaseName(name), database.getName());
        String user = database.getUser() == null ? database.getDefaultUser() : database.getUser();
        properties.setProperty(this.getUserKey(name), user);
        String password = database.getPassword();
        if (!ValueUtils.isEmpty(password))
            password = CryptoUtils.encrypt(password);
        properties.setProperty(this.getPasswordKey(name), password == null ? "" : password);
    }

    @Override
    public void save(Properties properties) {
        for (DatabaseConfigPanel databaseConfigPanel : this.databaseConfigPanels.values())
            this.save(databaseConfigPanel, properties);
    }
}

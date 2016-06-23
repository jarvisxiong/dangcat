package org.dangcat.install.database.task;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.dangcat.install.database.swing.DatabaseInstallPanel;
import org.dangcat.install.task.ConfigureAccess;


public class DatabaseInstallPanelAccess extends DatabaseInstallerAccess implements ConfigureAccess
{
    private Map<String, DatabaseInstallPanel> databaseInstallPanels = new LinkedHashMap<String, DatabaseInstallPanel>();

    public void addDatabaseInstallPanel(String name, DatabaseInstallPanel databaseInstallPanel)
    {
        this.databaseInstallPanels.put(name, databaseInstallPanel);
        this.addDatabaseInstaller(name, databaseInstallPanel.getDatabaseInstaller());
    }

    protected void load(DatabaseInstallPanel databaseInstallPanel, Properties properties)
    {
        this.load(databaseInstallPanel.getDatabaseInstaller(), properties);
        databaseInstallPanel.update();
    }

    @Override
    public void load(Properties properties)
    {
        for (DatabaseInstallPanel databaseInstallPanel : this.databaseInstallPanels.values())
            this.load(databaseInstallPanel, properties);
    }

    @Override
    public void save(Properties properties)
    {
        for (DatabaseInstallPanel databaseInstallPanel : this.databaseInstallPanels.values())
            this.save(databaseInstallPanel.getDatabaseInstaller(), properties);
    }
}

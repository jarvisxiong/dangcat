package org.dangcat.install.database.module;

import org.dangcat.commons.database.Database;
import org.dangcat.commons.database.MySqlDatabase;
import org.dangcat.install.database.swing.DatabaseConfigPanel;
import org.dangcat.install.database.task.DatabaseConfigAccess;
import org.dangcat.install.module.ProcessModuleBase;

public class DatabaseConfigModule extends ProcessModuleBase {
    private Database database = new MySqlDatabase();
    private DatabaseConfigAccess databaseConfigAccess = null;
    private DatabaseConfigPanel databaseConfigPanel = null;

    public DatabaseConfigModule(String name, String title) {
        super(name, title);
    }

    public void createDatabaseConfigAccess(String name, String title) {
        DatabaseConfigPanel databaseConfigPanel = this.createDatabaseConfigPanel(name, title);
        DatabaseConfigAccess databaseConfigAccess = new DatabaseConfigAccess();
        databaseConfigAccess.addDatabaseConfigPanel(name, databaseConfigPanel);
        this.databaseConfigAccess = databaseConfigAccess;
    }

    private DatabaseConfigPanel createDatabaseConfigPanel(String name, String title) {
        DatabaseConfigPanel databaseConfigPanel = new DatabaseConfigPanel();
        databaseConfigPanel.setDatabase(this.getDatabase());
        databaseConfigPanel.setName(name);
        databaseConfigPanel.setTitle(title);
        databaseConfigPanel.initialize();
        this.addConfigPanel(databaseConfigPanel);
        this.databaseConfigPanel = databaseConfigPanel;
        return databaseConfigPanel;
    }

    public Database getDatabase() {
        return this.database;
    }

    public DatabaseConfigAccess getDatabaseConfigAccess() {
        return this.databaseConfigAccess;
    }

    public DatabaseConfigPanel getDatabaseConfigPanel() {
        return this.databaseConfigPanel;
    }

    public String getDefaultName() {
        return this.getDatabase().getDefaultName();
    }

    public Integer getDefaultPort() {
        return this.getDatabase().getDefaultPort();
    }

    public void setDefaultPort(int port) {
        this.getDatabase().setDefaultPort(port);
    }

    @Override
    public void initialize() {
    }

    public void setDefaultDatabaseName(String databaseName) {
        this.getDatabase().setDefaultName(databaseName);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.databaseConfigAccess.setEnabled(enabled);
        super.setEnabled(enabled);
    }
}

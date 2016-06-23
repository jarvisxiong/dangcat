package org.dangcat.install.database.module;

import org.dangcat.install.database.mysql.MySqlInstaller;
import org.dangcat.install.database.task.MySqlUninstallTask;
import org.dangcat.install.module.ProcessModuleBase;
import org.dangcat.install.task.ConfigureAccess;

public class MySqlUninstallModule extends ProcessModuleBase {
    private MySqlUninstallTask databaseUninstallTask = new MySqlUninstallTask();

    public MySqlUninstallModule(String name, String title) {
        super(name, title);
    }

    public ConfigureAccess getConfigureAccess() {
        return null;
    }

    public MySqlInstaller getMySqlInstaller() {
        return this.databaseUninstallTask;
    }

    @Override
    public void initialize() {
        this.databaseUninstallTask.setServiceName(this.getName());
        this.databaseUninstallTask.setServiceDisplayName(this.getDisplayName());
        this.addProcessTask(this.databaseUninstallTask);
    }

    @Override
    public void prepare() {
        this.databaseUninstallTask.setBaseDir(this.getCurrentPath().getAbsolutePath());
    }
}

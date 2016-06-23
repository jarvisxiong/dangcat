package org.dangcat.install.database.module;

import org.dangcat.install.database.DatabaseInstaller;
import org.dangcat.install.database.task.MySqlInstallTask;

import java.io.File;

public class MySqlInstallModule extends DatabaseInstallModule
{
    private MySqlInstallTask databaseInstaller = new MySqlInstallTask();

    public MySqlInstallModule(String name, String title)
    {
        super(name, title);
    }

    @Override
    public DatabaseInstaller getDatabaseInstaller()
    {
        return this.databaseInstaller;
    }

    @Override
    public void prepare()
    {
        this.clearCopyTasks();
        String currentPath = this.getCurrentPath().getAbsolutePath();

        File databaseInstallDir = new File(currentPath, this.databaseInstaller.getServiceName() + File.separator + this.getInstallShellDir());
        File baseDir = new File(this.getTargetDir(), this.getName());
        this.databaseInstaller.setBaseDir(baseDir.getAbsolutePath());
        this.addCopyTask(databaseInstallDir, baseDir);

        File databaseDataDir = new File(currentPath, this.databaseInstaller.getServiceName() + File.separator + this.getDataDir());
        File dataDir = new File(this.getTargetDir() + File.separator + this.getName() + File.separator + "data");
        this.databaseInstaller.setDataDir(dataDir.getAbsolutePath());
        this.addCopyTask(databaseDataDir, dataDir);
    }
}

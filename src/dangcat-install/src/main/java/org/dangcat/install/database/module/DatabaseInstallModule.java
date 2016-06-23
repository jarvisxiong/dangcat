package org.dangcat.install.database.module;

import org.dangcat.commons.io.FileNameFilter;
import org.dangcat.install.database.DatabaseInstaller;
import org.dangcat.install.database.swing.DatabaseInstallPanel;
import org.dangcat.install.database.task.DatabaseInstallPanelAccess;
import org.dangcat.install.module.InstallModuleBase;
import org.dangcat.install.task.FileCopyTask;
import org.dangcat.install.task.ProcessTask;

import java.io.File;
import java.io.FilenameFilter;

public abstract class DatabaseInstallModule extends InstallModuleBase {
    private DatabaseInstallPanelAccess databaseAccess = null;
    private String dataDir = "data";
    private FileCopyTask fileCopyTask = new FileCopyTask();
    private String installShellDir = "install";
    private String scriptsDir = "scripts";

    public DatabaseInstallModule(String name, String title) {
        super(name, title);
    }

    public void addCopyTask(File source, File dest) {
        this.fileCopyTask.addTask(source, dest);
    }

    public void clearCopyTasks() {
        this.fileCopyTask.clearTasks();
    }

    public void createDatabaseAccess(String name, String title) {
        DatabaseInstallPanelAccess databaseInstallPanelAccess = new DatabaseInstallPanelAccess();
        DatabaseInstallPanel databaseInstallPanel = this.createDatabaseInstallPanel(name, title);
        databaseInstallPanelAccess.addDatabaseInstallPanel(name, databaseInstallPanel);
        this.databaseAccess = databaseInstallPanelAccess;
    }

    private DatabaseInstallPanel createDatabaseInstallPanel(String name, String title) {
        DatabaseInstallPanel databaseInstallPanel = new DatabaseInstallPanel();
        databaseInstallPanel.setDatabaseInstaller(this.getDatabaseInstaller());
        databaseInstallPanel.setName(name);
        databaseInstallPanel.setTitle(title);
        databaseInstallPanel.initialize();
        this.addConfigPanel(databaseInstallPanel);
        return databaseInstallPanel;
    }

    public DatabaseInstallPanelAccess getDatabaseAccess() {
        return this.databaseAccess;
    }

    public abstract DatabaseInstaller getDatabaseInstaller();

    public String getDataDir() {
        return this.dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public String getDefaultDatabaseName() {
        return this.getDatabaseInstaller().getDefaultDatabaseName();
    }

    public void setDefaultDatabaseName(String databaseName) {
        this.getDatabaseInstaller().setDefaultDatabaseName(databaseName);
    }

    public Integer getDefaultPort() {
        return this.getDatabaseInstaller().getDefaultPort();
    }

    public void setDefaultPort(int port) {
        this.getDatabaseInstaller().setDefaultPort(port);
    }

    @Override
    public String getInstallShellDir() {
        return this.installShellDir;
    }

    public void setInstallShellDir(String installShellDir) {
        this.installShellDir = installShellDir;
    }

    public String getScriptsDir() {
        return this.scriptsDir;
    }

    public void setScriptsDir(String scriptsDir) {
        this.scriptsDir = scriptsDir;
    }

    @Override
    public void initialize() {
        DatabaseInstaller databaseInstaller = this.getDatabaseInstaller();
        databaseInstaller.setServiceName(this.getName());
        databaseInstaller.setServiceDisplayName(this.getDisplayName());
        this.readScripts(databaseInstaller);

        this.addProcessTask(this.fileCopyTask);
        if (databaseInstaller instanceof ProcessTask)
            this.addProcessTask((ProcessTask) databaseInstaller);
    }

    private void readScripts(DatabaseInstaller databaseInstaller) {
        databaseInstaller.getScripts().clear();
        String currentPath = this.getCurrentPath().getAbsolutePath();
        File scriptsDir = new File(currentPath, databaseInstaller.getServiceName() + File.separator + this.getScriptsDir());
        File[] scriptFiles = scriptsDir.listFiles((FilenameFilter) new FileNameFilter(".sql"));
        if (scriptFiles != null && scriptFiles.length > 0) {
            for (File file : scriptFiles)
                databaseInstaller.getScripts().add(file.getAbsolutePath().replace("\\", "/"));
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.databaseAccess.setEnabled(enabled);
        super.setEnabled(enabled);
    }
}

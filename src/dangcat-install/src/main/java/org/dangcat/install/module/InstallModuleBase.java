package org.dangcat.install.module;

import java.io.File;

public abstract class InstallModuleBase extends ProcessModuleBase {
    private String targetDir = null;

    public InstallModuleBase(String name, String title) {
        super(name, title);
    }

    public String getInstallShellDir() {
        return this.getTargetDir() + File.separator + this.getName();
    }

    public String getTargetDir() {
        return this.targetDir;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }
}

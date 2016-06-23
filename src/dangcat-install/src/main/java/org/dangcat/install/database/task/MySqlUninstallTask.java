package org.dangcat.install.database.task;

import org.apache.log4j.Logger;
import org.dangcat.install.database.mysql.MySqlInstaller;
import org.dangcat.install.task.ProcessTask;


public class MySqlUninstallTask extends MySqlInstaller implements ProcessTask {
    private static final int TOTALSIZE = 2;
    private boolean enabled = true;
    private long finishedSize = 0l;

    @Override
    public void execute(Logger logger) {
        this.setLogger(logger);
        this.stop();
        this.finishedSize++;
        this.uninstall();
        this.finishedSize++;
    }

    @Override
    public long getFinishedSize() {
        return this.finishedSize;
    }

    @Override
    public long getTaskSize() {
        return TOTALSIZE;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

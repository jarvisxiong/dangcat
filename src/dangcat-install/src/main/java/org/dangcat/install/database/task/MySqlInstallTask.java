package org.dangcat.install.database.task;

import org.apache.log4j.Logger;
import org.dangcat.install.database.mysql.MySqlInstaller;
import org.dangcat.install.task.ProcessTask;


public class MySqlInstallTask extends MySqlInstaller implements ProcessTask {
    private static final int TOTALSIZE = 4;
    private boolean enabled = true;
    private long finishedSize = 0l;

    @Override
    public void execute(Logger logger) {
        this.setLogger(logger);
        try {
            this.config();
            this.finishedSize++;
            this.install();
            this.finishedSize++;
            this.start();
            this.finishedSize++;
            this.createDatabase();
            this.finishedSize++;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
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

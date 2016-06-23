package org.dangcat.install.task;

import org.apache.log4j.Logger;
import org.dangcat.commons.log.LogCallback;
import org.dangcat.install.swing.panel.InstallProgressPanel;

public class ProcessTaskManager extends Thread implements LogCallback {
    private String appenderName = "InstallCallback";
    private Class<?> classType = null;
    private Exception exception = null;
    private Runnable executeCallback = null;
    private Logger logger = null;
    private ProcessTaskCollection processTasks = new ProcessTaskCollection();
    private InstallProgressPanel progressPanel = null;

    public void addTask(ProcessTask processTask) {
        this.processTasks.addTask(processTask);
    }

    public void cancel() {
        this.processTasks.cancel();
    }

    @Override
    public void debug(String message) {
        if (this.progressPanel != null) {
            this.progressPanel.debug(message);
            this.showProgress();
        }
    }

    @Override
    public void error(String message) {
        if (this.progressPanel != null) {
            this.progressPanel.error(message);
            this.showProgress();
        }
    }

    @Override
    public void fatal(String message) {
        if (this.progressPanel != null) {
            this.progressPanel.fatal(message);
            this.showProgress();
        }
    }

    public String getAppenderName() {
        return this.appenderName;
    }

    public void setAppenderName(String appenderName) {
        this.appenderName = appenderName;
    }

    public Class<?> getClassType() {
        return this.classType;
    }

    public void setClassType(Class<?> classType) {
        this.classType = classType;
    }

    public Exception getException() {
        return this.exception;
    }

    public Runnable getExecuteCallback() {
        return this.executeCallback;
    }

    public void setExecuteCallback(Runnable executeCallback) {
        this.executeCallback = executeCallback;
    }

    private Logger getLogger() {
        if (this.logger == null) {
            Logger logger = Logger.getLogger(this.getClassType());
            this.logger = new org.dangcat.commons.log.Logger(logger, this.getAppenderName(), this);
        }
        return this.logger;
    }

    public InstallProgressPanel getProgressPanel() {
        return this.progressPanel;
    }

    public void setProgressPanel(InstallProgressPanel progressPanel) {
        this.progressPanel = progressPanel;
    }

    @Override
    public void info(String message) {
        if (this.progressPanel != null) {
            this.progressPanel.info(message);
            this.showProgress();
        }
    }

    public boolean isCancel() {
        return this.processTasks.isCancel();
    }

    public void prepare() {
        this.processTasks.prepare();
    }

    @Override
    public void run() {
        this.exception = null;
        Logger logger = this.getLogger();
        try {
            this.processTasks.execute(logger);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            this.exception = e;
        } finally {
            this.showProgress();
            if (this.executeCallback != null)
                this.executeCallback.run();
        }
    }

    public void showProgress() {
        if (this.progressPanel != null) {
            this.progressPanel.setMin(0);
            this.progressPanel.setValue((int) this.processTasks.getFinishedSize());
            this.progressPanel.setMax((int) this.processTasks.getTaskSize());
        }
    }

    @Override
    public void warn(String message) {
        if (this.progressPanel != null) {
            this.progressPanel.warn(message);
            this.showProgress();
        }
    }
}

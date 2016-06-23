package org.dangcat.commons.io.process;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * 文件处理进程。
 */
public abstract class FileProcess extends FileProcessBase {
    private Collection<File> prepareTasks = null;
    private Collection<File> tasks = new LinkedHashSet<File>();

    public void addTasks(File... files) {
        if (files != null) {
            for (File file : files) {
                if (file != null && file.exists())
                    this.tasks.add(file);
            }
        }
    }

    protected void afterProcess(File file) {
    }

    protected boolean beforeProcess(File file) {
        return true;
    }

    public void clearTasks() {
        this.tasks.clear();
    }

    public Collection<File> getTasks() {
        return this.tasks;
    }

    @Override
    protected void innerExecute() {
        if (this.prepareTasks != null) {
            for (File file : this.prepareTasks) {
                if (this.isCancel())
                    break;

                if (!this.beforeProcess(file))
                    continue;
                this.process(file);
                this.addResult(file);
                this.afterProcess(file);
            }
        }
    }

    @Override
    public void prepare() {
        super.prepare();
        Collection<File> prepareTasks = new LinkedHashSet<File>();
        for (File file : this.tasks)
            this.prepareFile(file, prepareTasks);
        this.prepareTasks = prepareTasks;
    }

    private void prepareFile(File file, Collection<File> prepareTasks) {
        this.increaseTotalSize(file);
        if (file.isFile())
            prepareTasks.add(file);
        else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File child : files)
                    this.prepareFile(child, prepareTasks);
            }
            prepareTasks.add(file);
        }
    }

    protected abstract void process(File file);
}

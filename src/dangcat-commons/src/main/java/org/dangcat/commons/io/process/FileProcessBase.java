package org.dangcat.commons.io.process;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

/**
 * 文件处理进程。
 * 
 */
public abstract class FileProcessBase implements Runnable
{
    private boolean cancel = false;
    private long finishedSize = 0l;
    private Logger logger = Logger.getLogger(this.getClass());
    private Collection<File> result = null;
    private long totalSize = 0l;

    public void addResult(File file)
    {
        if (this.result == null)
            this.result = new LinkedList<File>();
        this.result.add(file);
        this.increaseFinishedSize(file);
    }

    protected long getFileSize(File file)
    {
        if (file != null && file.isFile())
            return file.length();
        return 0l;
    }

    public int getFinishedPercent()
    {
        int finishedPercent = 0;
        if (this.totalSize > 0l)
            finishedPercent = (int) (this.finishedSize * 100.0 / this.totalSize);
        return finishedPercent;
    }

    public long getFinishedSize()
    {
        return this.finishedSize;
    }

    public Logger getLogger()
    {
        return this.logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Collection<File> getResult()
    {
        return this.result;
    }

    public long getTotalSize()
    {
        return this.totalSize;
    }

    protected void increaseFinishedSize(File file)
    {
        this.finishedSize += this.getFileSize(file);
    }

    protected void increaseTotalSize(File file)
    {
        this.totalSize += this.getFileSize(file);
    }

    protected abstract void innerExecute();

    public boolean isCancel()
    {
        return this.cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public void prepare()
    {
        this.cancel = false;
        this.finishedSize = 0l;
        this.totalSize = 0l;
        this.result = null;
    }

    @Override
    public void run()
    {
        this.prepare();
        this.innerExecute();
    }
}

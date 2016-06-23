package org.dangcat.install.task;

import org.apache.log4j.Logger;
import org.dangcat.commons.io.process.FileDeleteProcess;
import org.dangcat.commons.resource.ResourceUtils;

import java.io.File;

public class FileDeleteTask extends FileDeleteProcess implements ProcessTask
{
    private Logger currentLogger = null;
    private boolean enabled = true;

    @Override
    protected void afterProcess(File file)
    {
        super.afterProcess(file);
        File taskFile = null;
        for (File dirFile : this.getTasks())
        {
            if (file.getAbsolutePath().startsWith(dirFile.getAbsolutePath()))
            {
                taskFile = dirFile;
                break;
            }
        }

        String deleteFileName = file.getAbsolutePath().replace(taskFile.getAbsolutePath(), "");
        String message = null;
        if (file.isDirectory())
            message = ResourceUtils.getText(FileDeleteTask.class, FileCopyTask.class.getSimpleName() + ".removedir", deleteFileName);
        else if (file.isFile())
            message = ResourceUtils.getText(FileDeleteTask.class, FileCopyTask.class.getSimpleName() + ".deleteFile", deleteFileName);
        this.currentLogger.info(message);
    }

    @Override
    public void cancel()
    {
        this.setCancel(true);
    }

    @Override
    public void execute(Logger logger)
    {
        this.currentLogger = logger;
        this.innerExecute();
    }

    @Override
    public long getTaskSize()
    {
        return this.getTotalSize();
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}

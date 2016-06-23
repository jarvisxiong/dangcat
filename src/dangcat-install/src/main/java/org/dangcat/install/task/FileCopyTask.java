package org.dangcat.install.task;

import org.apache.log4j.Logger;
import org.dangcat.commons.io.process.FileCopyCallback;
import org.dangcat.commons.io.process.FileCopyProcess;
import org.dangcat.commons.resource.ResourceManager;
import org.dangcat.commons.resource.ResourceReader;

import java.io.File;
import java.util.Map.Entry;

public class FileCopyTask extends FileCopyProcess implements ProcessTask, FileCopyCallback
{
    private Logger currentLogger = null;
    private boolean enabled = true;
    private ResourceReader resourceReader = ResourceManager.getInstance().getResourceReader(this.getClass());

    public FileCopyTask()
    {
        super();
        this.setFileCopyCallback(this);
    }

    @Override
    public void afterCopy(File source, File dest)
    {
        File sourceDir = null;
        File destDir = null;
        for (Entry<File, File> entry : this.getTaskMap().entrySet())
        {
            if (source.getAbsolutePath().startsWith(entry.getKey().getAbsolutePath()))
            {
                sourceDir = entry.getKey();
                destDir = entry.getValue();
                break;
            }
        }

        String destFile = dest.getAbsolutePath().replace(destDir.getAbsolutePath(), "");
        String message = null;
        if (source.isDirectory())
            message = this.resourceReader.getText(FileCopyTask.class.getSimpleName() + ".mkdir", destFile);
        else if (source.isFile())
        {
            String srcFile = source.getAbsolutePath().replace(sourceDir.getAbsolutePath(), "");
            message = this.resourceReader.getText(FileCopyTask.class.getSimpleName() + ".copy", srcFile, destFile);
        }
        this.currentLogger.info(message);
    }

    @Override
    public boolean beforeCopy(File source, File dest)
    {
        return true;
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
    protected long getFileSize(File file)
    {
        return 1l;
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

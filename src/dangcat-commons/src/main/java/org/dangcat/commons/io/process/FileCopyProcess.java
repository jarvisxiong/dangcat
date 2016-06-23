package org.dangcat.commons.io.process;

import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.ValueUtils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 文件拷贝进程。
 * 
 */
public class FileCopyProcess extends FileProcessBase
{
    private FileCopyCallback fileCopyCallback = null;
    private Map<File, File> prepareTaskMap = null;
    private Map<File, File> taskMap = new LinkedHashMap<File, File>();

    public void addTask(File source, File dest)
    {
        if (this.isValid(source, dest))
            this.taskMap.put(source, dest);
    }

    public void clearTasks()
    {
        this.taskMap.clear();
    }

    public Map<File, File> getTaskMap()
    {
        return this.taskMap;
    }

    @Override
    protected void innerExecute()
    {
        if (this.prepareTaskMap != null)
        {
            for (Entry<File, File> entry : this.prepareTaskMap.entrySet())
            {
                if (this.isCancel())
                    break;

                File source = entry.getKey();
                File dest = entry.getValue();

                if (source.isFile())
                {
                    FileUtils.mkdir(dest.getParent());
                    FileUtils.copy(source, dest);
                    if (this.getLogger().isDebugEnabled())
                        this.getLogger().debug("copy from " + source.getAbsolutePath() + " to " + dest.getAbsolutePath());
                }
                else
                {
                    FileUtils.mkdir(dest.getAbsolutePath());
                    if (this.getLogger().isDebugEnabled())
                        this.getLogger().debug("mkdir " + dest.getAbsolutePath());
                }
                this.addResult(dest);

                if (this.fileCopyCallback != null)
                    this.fileCopyCallback.afterCopy(source, dest);
            }
        }
    }

    private boolean isValid(File source, File dest)
    {
        if (source.exists())
        {
            if (source.isFile())
                return true;
            if (source.isDirectory() && !dest.isFile())
                return true;
        }
        return false;
    }

    @Override
    public void prepare()
    {
        super.prepare();
        Map<File, File> prepareTaskMap = new LinkedHashMap<File, File>();
        for (Entry<File, File> entry : this.taskMap.entrySet())
        {
            String basePath = null;
            if (entry.getKey().isFile())
                basePath = FileUtils.getCanonicalPath(entry.getKey().getParentFile().getAbsolutePath());
            else
                basePath = FileUtils.getCanonicalPath(entry.getKey().getAbsolutePath());
            this.prepareFileMap(basePath, entry.getKey(), entry.getValue(), prepareTaskMap);
        }
        this.prepareTaskMap = prepareTaskMap;
    }

    private void prepareFileMap(String basePath, File source, File dest, Map<File, File> prepareTaskMap)
    {
        if (this.fileCopyCallback != null)
        {
            if (!this.fileCopyCallback.beforeCopy(source, dest))
                return;
        }

        File destFile = null;
        if (source.isFile() && source.getName().equals(dest.getName()))
            destFile = dest;
        else
        {
            String sourceName = FileUtils.getCanonicalPath(source.getAbsolutePath());
            if (!ValueUtils.isEmpty(basePath))
                sourceName = sourceName.replace(basePath, "");
            destFile = new File(dest.getAbsolutePath() + File.separator + sourceName);
        }
        prepareTaskMap.put(source, destFile);
        this.increaseTotalSize(source);

        if (source.isDirectory())
        {
            File[] files = source.listFiles();
            if (files != null && files.length > 0)
            {
                for (File file : files)
                    this.prepareFileMap(basePath, file, dest, prepareTaskMap);
            }
        }
    }

    public void setFileCopyCallback(FileCopyCallback fileCopyCallback)
    {
        this.fileCopyCallback = fileCopyCallback;
    }
}

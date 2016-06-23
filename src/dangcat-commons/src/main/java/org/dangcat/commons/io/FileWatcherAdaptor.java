package org.dangcat.commons.io;

import java.io.File;
import java.io.FileFilter;

/**
 * 文件行为事件适配器。
 * @author dangcat
 * 
 */
public abstract class FileWatcherAdaptor
{
    private File file;
    private FileFilter fileFilter;

    public FileWatcherAdaptor(File file)
    {
        this.file = file;
    }

    public FileWatcherAdaptor(File file, FileFilter fileFilter)
    {
        this.file = file;
        this.fileFilter = fileFilter;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof FileWatcherAdaptor)
        {
            FileWatcherAdaptor fileWatcherAdaptor = (FileWatcherAdaptor) obj;
            if (this.file.equals(fileWatcherAdaptor.getFile()))
                return this.getFileFilter() == fileWatcherAdaptor.getFileFilter();
        }
        return super.equals(obj);
    }

    public File getDirectory()
    {
        if (this.file.isDirectory())
            return this.file;
        return this.file.getParentFile();
    }

    public File getFile()
    {
        return this.file;
    }

    public FileFilter getFileFilter()
    {
        if (this.fileFilter == null)
        {
            if (this.file.isFile())
            {
                this.fileFilter = new FileFilter()
                {
                    @Override
                    public boolean accept(File pathname)
                    {
                        return file.equals(pathname);
                    }
                };
            }
        }
        return this.fileFilter;
    }

    protected void onDirectoryChange(File directory)
    {

    }

    protected void onDirectoryCreate(File directory)
    {

    }

    protected void onDirectoryDelete(File directory)
    {

    }

    protected void onFileChange(File file)
    {

    }

    protected void onFileCreate(File file)
    {

    }

    protected void onFileDelete(File file)
    {

    }
}

package org.dangcat.boot.event;

import java.io.File;

import org.dangcat.framework.event.Event;

public class FileEvent extends Event
{
    private static final long serialVersionUID = 1L;
    private File file;

    public FileEvent(File file)
    {
        this.file = file;
    }

    public File getFile()
    {
        return file;
    }
}

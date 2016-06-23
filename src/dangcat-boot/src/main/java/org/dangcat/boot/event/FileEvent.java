package org.dangcat.boot.event;

import org.dangcat.framework.event.Event;

import java.io.File;

public class FileEvent extends Event {
    private static final long serialVersionUID = 1L;
    private File file;

    public FileEvent(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}

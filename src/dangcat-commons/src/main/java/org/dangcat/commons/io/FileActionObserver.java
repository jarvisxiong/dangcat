package org.dangcat.commons.io;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

class FileActionObserver extends FileAlterationObserver {
    private static final long serialVersionUID = 1L;
    private FileWatcherAdaptor fileActionListenerAdaptor = null;

    FileActionObserver(FileWatcherAdaptor fileActionListenerAdaptor) {
        super(fileActionListenerAdaptor.getDirectory(), fileActionListenerAdaptor.getFileFilter());
        this.fileActionListenerAdaptor = fileActionListenerAdaptor;
        this.addListener(new FileAlterationListener() {
            @Override
            public void onDirectoryChange(File directory) {
                FileActionObserver.this.fileActionListenerAdaptor.onDirectoryChange(directory);
            }

            @Override
            public void onDirectoryCreate(File directory) {
                FileActionObserver.this.fileActionListenerAdaptor.onDirectoryCreate(directory);
            }

            @Override
            public void onDirectoryDelete(File directory) {
                FileActionObserver.this.fileActionListenerAdaptor.onDirectoryDelete(directory);
            }

            @Override
            public void onFileChange(File file) {
                FileActionObserver.this.fileActionListenerAdaptor.onFileChange(file);
            }

            @Override
            public void onFileCreate(File file) {
                FileActionObserver.this.fileActionListenerAdaptor.onFileCreate(file);
            }

            @Override
            public void onFileDelete(File file) {
                FileActionObserver.this.fileActionListenerAdaptor.onFileDelete(file);
            }

            @Override
            public void onStart(FileAlterationObserver arg0) {
            }

            @Override
            public void onStop(FileAlterationObserver arg0) {
            }
        });
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileActionObserver) {
            FileActionObserver fileActionObserver = (FileActionObserver) obj;
            return fileActionObserver.getFileActionListenerAdaptor() == this.getFileActionListenerAdaptor();
        }
        return super.equals(obj);
    }

    protected FileWatcherAdaptor getFileActionListenerAdaptor() {
        return fileActionListenerAdaptor;
    }
}

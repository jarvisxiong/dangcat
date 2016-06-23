package org.dangcat.commons.io;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.dangcat.commons.utils.NameThreadFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件行为监视器。
 *
 * @author dangcat
 */
public class FileActionMonitor {
    private static FileActionMonitor instance = new FileActionMonitor();
    private List<FileActionObserver> fileActionObserverList = new ArrayList<FileActionObserver>();
    private FileAlterationMonitor fileAlterationMonitor = new FileAlterationMonitor();
    private boolean isRunning = false;

    private FileActionMonitor() {

    }

    public static FileActionMonitor getInstance() {
        return instance;
    }

    public void addFileWatcherAdaptor(FileWatcherAdaptor fileWatcherAdaptor) {
        FileActionObserver fileActionObserver = new FileActionObserver(fileWatcherAdaptor);
        this.fileActionObserverList.add(fileActionObserver);
        this.fileAlterationMonitor.addObserver(fileActionObserver);
        this.start();
    }

    public void removeFileWatcherAdaptor(FileWatcherAdaptor fileWatcherAdaptor) {
        FileActionObserver fileActionObserver = new FileActionObserver(fileWatcherAdaptor);
        this.fileActionObserverList.remove(fileActionObserver);
        this.fileAlterationMonitor.removeObserver(fileActionObserver);
        this.stop();
    }

    public void start() {
        if (isRunning || fileActionObserverList.size() == 0)
            return;

        try {
            this.fileAlterationMonitor.setThreadFactory(new NameThreadFactory("FILEMONITOR-"));
            this.fileAlterationMonitor.start();
            this.isRunning = true;
        } catch (Exception e) {
        }
    }

    public void stop() {
        if (!isRunning || fileActionObserverList.size() == 0)
            return;

        try {
            this.fileAlterationMonitor.stop();
            this.isRunning = false;
        } catch (Exception e) {
        }
    }
}

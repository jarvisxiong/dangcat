package org.dangcat.boot.service.impl;

import org.dangcat.boot.service.FileWriteService;
import org.dangcat.commons.io.FileWriter;
import org.dangcat.commons.timer.AlarmClock;
import org.dangcat.commons.timer.IntervalAlarmClock;
import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceProvider;

import java.util.Collection;
import java.util.HashSet;

/**
 * 文件写入服务。
 *
 * @author dangcat
 */
public class FileWriteServiceImpl extends ServiceBase implements Runnable, FileWriteService {
    private static final int INTERVAL = 30;
    private Collection<FileWriter> fileWriters = new HashSet<FileWriter>();

    /**
     * 所属父服务。
     *
     * @param parent
     */
    public FileWriteServiceImpl(ServiceProvider parent) {
        super(parent);
    }

    /**
     * 添加文件写入对象。
     *
     * @param fileWriter 文件写入对象。
     */
    public void addFileWriter(FileWriter fileWriter) {
        this.fileWriters.add(fileWriter);
    }

    @Override
    public void initialize() {
        super.initialize();

        AlarmClock alarmClock = new IntervalAlarmClock(this) {
            @Override
            public long getInterval() {
                return INTERVAL;
            }

            @Override
            public boolean isEnabled() {
                return fileWriters.size() > 0;
            }
        };
        TimerServiceImpl.getInstance().createTimer(alarmClock);
    }

    /**
     * 删除文件写入对象。
     *
     * @param fileWriter 文件写入对象。
     */
    public void removeFileWriter(FileWriter fileWriter) {
        this.fileWriters.remove(fileWriter);
    }

    /**
     * 定时清理过期的缓存数据。
     */
    @Override
    public void run() {
        for (FileWriter fileWriter : this.fileWriters) {
            synchronized (fileWriter) {
                fileWriter.switchFile();
            }
        }
    }
}

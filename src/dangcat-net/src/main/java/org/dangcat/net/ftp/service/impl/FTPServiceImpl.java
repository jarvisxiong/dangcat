package org.dangcat.net.ftp.service.impl;

import org.dangcat.boot.service.impl.ThreadService;
import org.dangcat.boot.statistics.StatisticsService;
import org.dangcat.commons.io.FileNameFilter;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.timer.IntervalAlarmClock;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.net.ftp.conf.FTPConfig;
import org.dangcat.net.ftp.exceptions.FTPSessionException;
import org.dangcat.net.ftp.service.FTPCallBack;
import org.dangcat.net.ftp.service.FTPService;
import org.dangcat.net.ftp.statistics.FTPStatistics;

import java.io.File;

/**
 * FTP服务。
 *
 * @author dangcat
 */
public class FTPServiceImpl extends ThreadService implements FTPCallBack, FTPService {
    private static final int HANDLE_DELETE = 0;
    private static final int HANDLE_MOVE = 3;
    private static final int HANDLE_RENAME = 1;
    private static final String MODE_DOWNLOAD = "DOWNLOAD-";
    private static final String MODE_UPLOAD = "UPLOAD-";
    private static final String SERVICE_NAME = "FTP-";
    /**
     * 处理后措施：0、删除；1、修改扩展名；3、移动路径。
     */
    private int afterHandle = 0;
    /**
     * FTP上下文。
     */
    private FTPContext ftpContext = null;
    /**
     * FTP统计。
     */
    private FTPStatistics ftpStatistics = null;
    /**
     * 处理后移动路径。
     */
    private String handledPath = null;
    /**
     * 处理后修改扩展名。
     */
    private String handledSuffix = null;
    /**
     * FTP连接错误。
     */
    private boolean isFtpFailure = false;
    /**
     * 本地路径。
     */
    private String localPath = null;
    /**
     * 模式：0、下载；其它：上传。
     */
    private int mode = 0;
    /**
     * FTP资源名称。
     */
    private String name = null;
    /**
     * 处理的文件名前缀。
     */
    private String prefix = null;
    /**
     * 远程路径。
     */
    private String remotePath = null;
    /**
     * 处理的文件扩展名。
     */
    private String suffix = null;

    /**
     * 构建服务。
     *
     * @param parent 所属服务。
     */
    public FTPServiceImpl(ServiceProvider parent) {
        super(parent);
    }

    public int getAfterHandle() {
        return this.afterHandle;
    }

    public void setAfterHandle(int afterHandle) {
        this.afterHandle = afterHandle;
    }

    private FTPContext getFTPContext() {
        if (this.ftpContext == null) {
            FTPContext ftpContext = new FTPContext(new File(this.getLocalPath()), this.getRemotePath(), this);
            if (!ValueUtils.isEmpty(this.getSuffix()) || !ValueUtils.isEmpty(this.getPrefix())) {
                FileNameFilter fileNameFilter = new FileNameFilter(this.getPrefix(), this.getSuffix());
                if (this.isDownloadMode())
                    ftpContext.setRemoteFileFilter(fileNameFilter);
                else
                    ftpContext.setLocalFileFilter(fileNameFilter);
            }
            this.ftpContext = ftpContext;
        }
        return this.ftpContext;
    }

    private FTPStatistics getFTPStatistics() {
        if (this.ftpStatistics == null)
            this.ftpStatistics = new FTPStatistics(this.getName());
        return this.ftpStatistics;
    }

    public String getHandledPath() {
        return this.handledPath;
    }

    public void setHandledPath(String handledPath) {
        this.handledPath = handledPath;
    }

    public String getHandledSuffix() {
        return this.handledSuffix;
    }

    public void setHandledSuffix(String handledSuffix) {
        this.handledSuffix = handledSuffix;
    }

    public String getLocalPath() {
        return this.localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getRemotePath() {
        return this.remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME + (this.isDownloadMode() ? MODE_DOWNLOAD : MODE_UPLOAD) + this.getName();
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public void initialize() {
        super.initialize();

        // 注册统计对象。
        StatisticsService statisticsService = this.getService(StatisticsService.class);
        if (statisticsService != null)
            statisticsService.addStatistics(this.getFTPStatistics());

        // 注册定时器。
        this.setAlarmClock(new IntervalAlarmClock(this) {
            @Override
            public long getInterval() {
                return FTPConfig.getInstance().getInterval() * (FTPServiceImpl.this.isFtpFailure ? 4 : 1);
            }

            @Override
            public boolean isEnabled() {
                return FTPServiceImpl.this.isEnabled();
            }
        });
    }

    @Override
    protected void innerExecute() {
        if (!FTPConfig.getInstance().isEnabled())
            return;

        FTPStatistics ftpStatistics = this.getFTPStatistics();
        try {
            ftpStatistics.begin();
            this.isFtpFailure = false;

            FTPSession ftpSession = FTPSessionFactory.getInstance().openSession(this.getName());
            if (ftpSession != null) {
                FTPContext ftpContext = this.getFTPContext();
                if (this.isDownloadMode())
                    ftpSession.download(ftpContext);
                else
                    ftpSession.upload(ftpContext);
            }
        } catch (FTPSessionException e) {
            this.isFtpFailure = true;
            if (this.logger.isDebugEnabled())
                this.logger.debug(this, e);
        } finally {
            ftpStatistics.end();
        }
    }

    private boolean isDownloadMode() {
        return this.getMode() == 0;
    }

    @Override
    public void onFailure(FTPContext ftpContext, File file, String remotePath, Exception exception) {
        this.isFtpFailure = true;
        if (file != null)
            this.getFTPStatistics().increaseError();
    }

    @Override
    public void onSucess(FTPContext ftpContext, File file, String remotePath) {
        if (file == null)
            return;

        try {
            FTPStatistics ftpStatistics = this.getFTPStatistics();
            ftpStatistics.increaseSuccess();
            ftpStatistics.increaseSize(FileUtils.getTotalSize(file));

            // 处理后措施：0、删除；1、修改扩展名；3、移动路径。
            if (this.isDownloadMode()) {
                if (this.getAfterHandle() == HANDLE_DELETE)
                    ftpContext.getFtpSession().delete(remotePath);
                else if (this.getAfterHandle() == HANDLE_RENAME) {
                    String oldExtension = FileUtils.getExtension(remotePath);
                    String newName = remotePath.substring(0, remotePath.length() - oldExtension.length()) + this.getHandledSuffix();
                    ftpContext.getFtpSession().rename(remotePath, newName);
                } else if (this.getAfterHandle() == HANDLE_MOVE)
                    ftpContext.getFtpSession().rename(remotePath, this.getHandledPath());
            } else {
                if (this.getAfterHandle() == HANDLE_DELETE)
                    file.delete();
                else if (this.getAfterHandle() == HANDLE_RENAME) {
                    String oldExtension = FileUtils.getExtension(file.getName());
                    FileUtils.renameFileExtName(file, oldExtension, this.getHandledSuffix());
                } else if (this.getAfterHandle() == HANDLE_MOVE) {
                    File handledPath = new File(this.getHandledPath());
                    if (!handledPath.exists())
                        FileUtils.mkdir(this.getHandledPath());
                    FileUtils.move(file, handledPath, true);
                }
            }
        } catch (FTPSessionException e) {
            this.logger.error(this, e);
        }
    }
}

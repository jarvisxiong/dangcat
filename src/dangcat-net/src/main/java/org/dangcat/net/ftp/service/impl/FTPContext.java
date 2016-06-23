package org.dangcat.net.ftp.service.impl;

import org.dangcat.net.ftp.service.FTPCallBack;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

public class FTPContext {
    public static String OPT_DELETE = "Delete";
    public static String OPT_DOWNLOAD = "Download";
    public static String OPT_LIST = "List";
    public static String OPT_RENAME = "Rename";
    public static String OPT_TESTCONNECTION = "TestConnection";
    public static String OPT_UPLOAD = "Upload";
    private FTPCallBack callBack = null;
    private boolean cancel = false;
    private long costTime = 0l;
    private long finishedSize = 0l;
    private FTPClient ftpClient = null;
    private FTPSession ftpSession = null;
    private long lastResponseTime = System.currentTimeMillis();
    private FileFilter localFileFilter = null;
    private File localPath = null;
    private String name = null;
    private FilenameFilter remoteFileFilter = null;
    private String remotePath = null;
    private int totalCount = 0;
    private long totalSize = 0l;

    public FTPContext(File localPath, String remotePath) {
        this.localPath = localPath;
        this.remotePath = remotePath;
    }

    public FTPContext(File localPath, String remotePath, FileFilter localFileFilter) {
        this.localPath = localPath;
        this.remotePath = remotePath;
        this.localFileFilter = localFileFilter;
    }

    public FTPContext(File localPath, String remotePath, FilenameFilter remoteFileFilter) {
        this.localPath = localPath;
        this.remotePath = remotePath;
        this.remoteFileFilter = remoteFileFilter;
    }

    public FTPContext(File localPath, String remotePath, FTPCallBack callBack) {
        this.localPath = localPath;
        this.remotePath = remotePath;
        this.callBack = callBack;
    }

    public FTPContext(String remotePath) {
        this.remotePath = remotePath;
    }

    public FTPCallBack getCallBack() {
        return this.callBack;
    }

    public void setCallBack(FTPCallBack callBack) {
        this.callBack = callBack;
    }

    public long getCostTime() {
        return this.costTime;
    }

    protected void setCostTime(long costTime) {
        this.costTime = costTime;
    }

    public int getFinishedPercent() {
        return this.totalSize == 0l ? 0 : (int) (this.finishedSize * 100.0 / this.totalSize);
    }

    public long getFinishedSize() {
        return this.finishedSize;
    }

    public FTPClient getFtpClient() {
        return this.ftpClient;
    }

    protected void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    protected FTPSession getFtpSession() {
        return this.ftpSession;
    }

    protected void setFtpSession(FTPSession ftpSession) {
        this.ftpSession = ftpSession;
    }

    public long getLastResponseTime() {
        return this.lastResponseTime;
    }

    public FileFilter getLocalFileFilter() {
        return this.localFileFilter;
    }

    public void setLocalFileFilter(FileFilter localFileFilter) {
        this.localFileFilter = localFileFilter;
    }

    public File getLocalPath() {
        return this.localPath;
    }

    public String getName() {
        return this.name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public FilenameFilter getRemoteFileFilter() {
        return this.remoteFileFilter;
    }

    public void setRemoteFileFilter(FilenameFilter remoteFileFilter) {
        this.remoteFileFilter = remoteFileFilter;
    }

    public String getRemotePath() {
        return this.remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public long getTotalSize() {
        return this.totalSize;
    }

    public void increaseCostTime(long value) {
        this.costTime += value;
    }

    public void increaseFinished(long value) {
        this.finishedSize += value;
        this.response();
    }

    public void increaseTotalCount() {
        this.totalCount++;
        this.response();
    }

    public void increaseTotalSize(long value) {
        this.totalSize += value;
    }

    public boolean isCancel() {
        return this.cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public void reset() {
        this.totalSize = 0l;
        this.finishedSize = 0l;
        this.costTime = 0l;
        this.totalCount = 0;
        this.cancel = false;
        this.response();
    }

    public void response() {
        this.lastResponseTime = System.currentTimeMillis();
    }
}

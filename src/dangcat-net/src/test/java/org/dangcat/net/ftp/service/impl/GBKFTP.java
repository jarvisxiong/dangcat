package org.dangcat.net.ftp.service.impl;

import org.apache.commons.net.ftp.FTPFile;
import org.dangcat.commons.utils.Environment;
import org.dangcat.net.ftp.exceptions.FTPSessionException;
import org.junit.Assert;

import java.io.File;

public class GBKFTP {
    private static String DOWNLOAD_DIR = "./log/data";
    private static String REMOTE_DIR = "双机";
    private static String REMOTE_FILE = "本地双机安装配置指南.zip";
    private static String UPLOAD_DIR = "E:/Share/" + REMOTE_DIR;
    private File downloadFile = new File(DOWNLOAD_DIR + "/" + REMOTE_FILE);
    private File downloadPath = new File(DOWNLOAD_DIR);
    private String remoteFile = REMOTE_DIR + "/" + REMOTE_FILE;
    private File uploadFile = new File(UPLOAD_DIR + "/" + REMOTE_FILE);

    public static void main(String[] args) throws FTPSessionException {
        new GBKFTP().execute();
    }

    public void execute() throws FTPSessionException {
        Environment.setModuleEnabled(FTPClientPool.class.getSimpleName(), true);
        FTPSession ftpSession = this.getFTPSession();
        ftpSession.delete(REMOTE_DIR);
        FTPFile[] removedFTPFiles = ftpSession.list(REMOTE_DIR);
        Assert.assertEquals(0, removedFTPFiles.length);
        ftpSession.upload(this.uploadFile, REMOTE_DIR, null);
        ftpSession.download(this.downloadPath, this.remoteFile, null);
        Assert.assertTrue(this.downloadFile.exists());
        Assert.assertEquals(this.uploadFile.length(), this.downloadFile.length());
    }

    private FTPSession getFTPSession() {
        FTPClientSession ftpClientSession = new FTPClientSession();
        ftpClientSession.setServer("127.0.0.1");
        ftpClientSession.setUserName("dangcat");
        ftpClientSession.setPassword("dangcat2014");
        ftpClientSession.setInitPath("download");
        ftpClientSession.setPoolEnabled(false);
        ftpClientSession.initialize();
        return ftpClientSession;
    }
}

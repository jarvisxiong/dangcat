package org.dangcat.net.ftp.service.impl;

import org.dangcat.commons.utils.Environment;
import org.dangcat.net.ftp.exceptions.FTPSessionException;
import org.junit.Assert;

import java.io.File;

public class ContinueFTP implements Runnable {
    private static String DOWNLOAD_DIR = "./log/data";
    private static String FILENAME = "eclipse-java-ganymede-SR2-win32.zip";
    private static String UPLOAD_DIR = "E:/Setup/ide/eclipse";
    private FTPContext downloadContext = null;
    private File downloadFile = new File(DOWNLOAD_DIR + "/" + FILENAME);
    private boolean process = true;
    private FTPContext uploadContext = null;
    private File uploadFile = new File(UPLOAD_DIR + "/" + FILENAME);

    public static void main(String[] args) throws FTPSessionException {
        new ContinueFTP().execute();
    }

    public void execute() throws FTPSessionException {
        Environment.setModuleEnabled(FTPClientPool.class.getSimpleName(), true);
        this.downloadContext = new FTPContext(new File(DOWNLOAD_DIR), FILENAME);
        this.uploadContext = new FTPContext(this.uploadFile, null);
        Thread thread = new Thread(this);
        thread.start();

        try {
            FTPSession ftpSession = this.getFTPSession();
            // ftpSession.delete(FILENAME);
            // FTPFile[] removedFTPFiles = ftpSession.list(FILENAME);
            // Assert.assertEquals(0, removedFTPFiles.length);

            ftpSession.upload(this.uploadContext);
            ftpSession.download(this.downloadContext);
            Assert.assertTrue(this.downloadFile.exists());
            Assert.assertEquals(this.uploadFile.length(), this.downloadFile.length());
        } finally {
            this.process = false;
        }
    }

    private FTPSession getFTPSession() {
        FTPClientSession ftpClientSession = new FTPClientSession();
        ftpClientSession.setServer("10.46.60.135");
        ftpClientSession.setUserName("dangcat");
        ftpClientSession.setPassword("dangcat2014");
        ftpClientSession.setInitPath("download");
        ftpClientSession.setPoolEnabled(false);
        ftpClientSession.initialize();
        return ftpClientSession;
    }

    @Override
    public void run() {
        while (this.process) {
            int uploadPercent = this.uploadContext.getFinishedPercent();
            if (uploadPercent < 100)
                System.out.println("Upload finished " + uploadPercent + "%");
            else {
                int downloadPercent = this.downloadContext.getFinishedPercent();
                if (downloadPercent < 100)
                    System.out.println("Download finished " + downloadPercent + "%");
            }
            try {
                Thread.sleep(1000l);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("Process done: upload = " + this.uploadContext.getFinishedPercent() + "%, download = " + this.downloadContext.getFinishedPercent() + "%");
    }
}

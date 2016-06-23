package org.dangcat.net.ftp.service.impl;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.net.ftp.exceptions.FTPSessionException;
import org.dangcat.net.ftp.service.FTPCallBack;

import java.io.*;
import java.util.Collection;
import java.util.LinkedList;

/**
 * FTP下载会话。
 *
 * @author dangcat
 */
class FTPDownloadSession extends FTPSessionExecutor {
    FTPDownloadSession(FTPContext ftpContext, FTPSession ftpSession) {
        super(ftpContext, ftpSession);
        ftpContext.setName(FTPContext.OPT_DOWNLOAD);
    }

    private String createDebugInfo(File localFile, String remotePath, long finishedSize, long costTime) {
        StringBuilder info = new StringBuilder();
        info.append("End download file: ");
        info.append(localFile.getAbsolutePath());
        info.append(" from ");
        info.append(remotePath);
        this.createLogMessage(info, null, remotePath, localFile.getAbsolutePath(), costTime, null, finishedSize);
        return info.toString();
    }

    private FTPFileInfo createFileInfo(FTPFileInfo parent, FTPFile ftpFile) {
        FTPFileInfo ftpFileInfo = new FTPFileInfo(parent);
        ftpFileInfo.setFtpFile(ftpFile);
        return ftpFileInfo;
    }

    private InputStream createInputStream(FTPClient ftpClient, String remotePath, String remoteFile) throws IOException {
        String ftpFileName = this.getFTPFileName(remotePath, remoteFile);
        return ftpClient.retrieveFileStream(ftpFileName);
    }

    private InputStream createInputStream(FTPFile ftpFile, String remotePath, FTPClient ftpClient) throws IOException {
        String remoteRootPath = this.getFtpContext().getRemotePath();
        InputStream inputStream = null;
        if (remoteRootPath.equalsIgnoreCase(ftpFile.getName()) || remoteRootPath.endsWith(FILE_SEPARATOR + ftpFile.getName()))
            inputStream = this.createInputStream(ftpClient, null, remoteRootPath);
        if (inputStream == null)
            inputStream = this.createInputStream(ftpClient, this.getRemotePath(remotePath), null);
        return inputStream;
    }

    private OutputStream createOutputStream(FTPFile ftpFile, File localFile, InputStream inputStream) throws IOException {
        OutputStream outputStream = null;
        if (Boolean.TRUE.equals(this.isContinueLoad()) && localFile.exists() && localFile.length() <= ftpFile.getSize()) {
            if (localFile.length() < ftpFile.getSize()) {
                outputStream = new BufferedOutputStream(new FileOutputStream(localFile, true));
                inputStream.skip(localFile.length());
            }
            this.increaseFinished(localFile.length());
        } else {
            FileUtils.mkdir(localFile.getParent());
            outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
        }
        return outputStream;
    }

    private void download(Collection<FTPFileInfo> ftpFileInfos) throws IOException, FTPSessionException {
        if (ftpFileInfos != null && !ftpFileInfos.isEmpty()) {
            for (FTPFileInfo ftpFileInfo : ftpFileInfos) {
                if (this.getFtpContext().isCancel())
                    break;
                this.download(ftpFileInfo);
                this.download(ftpFileInfo.getChildren());
            }
        }
    }

    private void download(FTPFileInfo ftpFileInfo) throws IOException, FTPSessionException {
        File localFile = ftpFileInfo.getLocalFile();
        FTPFile ftpFile = ftpFileInfo.getFtpFile();
        if (ftpFile.isDirectory()) {
            FileUtils.mkdir(localFile.getAbsolutePath());
            return;
        } else if (!localFile.exists() || localFile.length() != ftpFile.getSize())
            this.downloadFile(ftpFileInfo);
    }

    private void downloadFile(FTPFileInfo ftpFileInfo) throws IOException, FTPSessionException {
        File localFile = ftpFileInfo.getLocalFile();
        String remotePath = ftpFileInfo.getRemotePath();

        if (this.logger.isDebugEnabled())
            this.logger.debug("Begin download file " + localFile.getAbsolutePath() + " from " + remotePath);

        FTPContext ftpContext = this.getFtpContext();
        long beginTime = System.currentTimeMillis();
        long finishedSize = 0l;
        FTPClient ftpClient = ftpContext.getFtpClient();
        FTPCallBack ftpCallBack = ftpContext.getCallBack();
        OutputStream outputStream = null;
        InputStream inputStream = null;
        boolean result = false;
        try {
            FTPFile ftpFile = ftpFileInfo.getFtpFile();
            inputStream = this.createInputStream(ftpFile, remotePath, ftpClient);
            if (inputStream != null) {
                outputStream = this.createOutputStream(ftpFile, localFile, inputStream);
                if (outputStream != null)
                    finishedSize = this.copyStream(ftpContext, inputStream, outputStream);
                result = true;
            }
        } catch (IOException e) {
            if (ftpCallBack != null)
                ftpCallBack.onFailure(ftpContext, localFile, remotePath, e);
            throw e;
        } finally {
            FileUtils.close(inputStream);
            FileUtils.close(outputStream);
            if (result)
                ftpClient.completePendingCommand();

            long costTime = System.currentTimeMillis() - beginTime;
            this.increaseCostTime(costTime);

            if (this.logger.isDebugEnabled())
                this.logger.debug(this.createDebugInfo(localFile, remotePath, finishedSize, costTime));
        }

        if (localFile.exists() && ftpCallBack != null)
            ftpCallBack.onSucess(ftpContext, localFile, remotePath);
    }

    private String getRemotePath(String remotePath) {
        String remoteRootPath = this.getRemoteRootPath();
        if (ValueUtils.isEmpty(remotePath))
            return remoteRootPath;
        if (ValueUtils.isEmpty(remoteRootPath))
            return remotePath;
        return remoteRootPath + FILE_SEPARATOR + remotePath;
    }

    /**
     * 下载文件。
     *
     * @throws FTPSessionException
     */
    @Override
    protected void innerExecute() throws FTPSessionException, IOException {
        Collection<FTPFileInfo> ftpFileInfos = this.loadFileInfos();
        if (ftpFileInfos != null && !ftpFileInfos.isEmpty()) {
            File localPath = this.getFtpContext().getLocalPath();
            this.loadFTPFileInfo(localPath, ftpFileInfos);
            this.download(ftpFileInfos);
        }
    }

    private Collection<FTPFileInfo> loadFileInfos() throws IOException {
        String remotePath = this.getRemoteRootPath();
        if (this.logger.isDebugEnabled())
            this.logger.debug("Begin loadFileInfos from " + remotePath);

        Collection<FTPFileInfo> ftpFileInfos = null;
        long beginTime = System.currentTimeMillis();
        try {
            ftpFileInfos = this.loadFileInfos(null, null);
        } finally {
            if (this.logger.isDebugEnabled()) {
                long costTime = System.currentTimeMillis() - beginTime;
                StringBuilder info = new StringBuilder();
                info.append("End loadFileInfos");
                info.append(" from ");
                info.append(remotePath);
                this.createLogMessage(info, null, remotePath, null, costTime, null, null);
                this.logger.debug(info);
            }
        }
        return ftpFileInfos;
    }

    /**
     * 列出远程的文件。
     *
     * @param ftpFileInfos 本地文件信息。
     * @param ftpFile      远程文件。
     * @throws IOException
     */
    private Collection<FTPFileInfo> loadFileInfos(FTPFileInfo parent, String remotePath) throws IOException {
        FTPContext ftpContext = this.getFtpContext();
        Collection<FTPFileInfo> ftpFileInfos = null;
        FTPFile[] ftpFiles = this.listFTPFiles(this.getRemotePath(remotePath));
        if (ftpFiles != null && ftpFiles.length > 0) {
            ftpFileInfos = new LinkedList<FTPFileInfo>();
            for (FTPFile ftpFile : ftpFiles) {
                if (ftpContext.isCancel())
                    break;

                if (this.isRootPath(ftpFile))
                    continue;

                FTPFileInfo ftpFileInfo = this.createFileInfo(parent, ftpFile);
                if (ftpFile.isDirectory()) {
                    Collection<FTPFileInfo> children = this.loadFileInfos(ftpFileInfo, ftpFileInfo.getRemotePath());
                    if (children != null && !children.isEmpty())
                        ftpFileInfo.setChildren(children);
                } else if (ftpFile.isFile())
                    ftpContext.increaseTotalSize(ftpFile.getSize());
                ftpFileInfos.add(ftpFileInfo);
            }
        }
        return ftpFileInfos;
    }

    private void loadFTPFileInfo(File localPath, Collection<FTPFileInfo> ftpFileInfos) throws IOException {
        if (ftpFileInfos != null && !ftpFileInfos.isEmpty()) {
            FTPContext ftpContext = this.getFtpContext();
            for (FTPFileInfo ftpFileInfo : ftpFileInfos) {
                if (ftpContext.isCancel())
                    break;

                FTPFile ftpFile = ftpFileInfo.getFtpFile();
                File localFile = new File(localPath.getAbsolutePath(), ftpFile.getName());
                ftpFileInfo.setLocalFile(localFile);
                if (ftpFileInfo.getChildren() != null)
                    this.loadFTPFileInfo(localFile, ftpFileInfo.getChildren());
            }
        }
    }
}

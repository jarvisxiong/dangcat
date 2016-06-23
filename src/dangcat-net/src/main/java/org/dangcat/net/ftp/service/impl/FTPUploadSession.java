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
 * @author dangcat
 * 
 */
class FTPUploadSession extends FTPSessionExecutor
{
    FTPUploadSession(FTPContext ftpContext, FTPSession ftpSession)
    {
        super(ftpContext, ftpSession);
        ftpContext.setName(FTPContext.OPT_UPLOAD);
    }

    private String createDebugInfo(File localFile, String remotePath, long finishedSize, long costTime)
    {
        StringBuilder info = new StringBuilder();
        info.append("End upload file: ");
        info.append(localFile.getAbsolutePath());
        info.append(" to ");
        info.append(remotePath);
        this.createLogMessage(info, null, remotePath, localFile.getAbsolutePath(), costTime, null, finishedSize);
        return info.toString();
    }

    private FTPFileInfo createFileInfo(FTPFileInfo parent, File localFile)
    {
        FTPFileInfo ftpFileInfo = new FTPFileInfo(parent);
        ftpFileInfo.setLocalFile(localFile);
        return ftpFileInfo;
    }

    private void createFTPPath(FTPClient ftpClient, String path) throws FTPSessionException, IOException
    {
        if (!ValueUtils.isEmpty(path))
        {
            String directory = this.getFTPFileName(path, null);
            if (!ftpClient.changeWorkingDirectory(directory))
            {
                if (!ftpClient.makeDirectory(directory))
                    throw new FTPSessionException(FTPSessionException.CREATEDIR, path);
                ftpClient.changeWorkingDirectory(directory);
            }
        }
    }

    private OutputStream createOutputStream(File localFile, FTPFile ftpFile, FTPClient ftpClient, InputStream inputStream) throws IOException
    {
        OutputStream outputStream = null;
        String ftpFileName = this.getFTPFileName(null, (ftpFile == null ? localFile.getName() : ftpFile.getName()));
        if (Boolean.TRUE.equals(this.isContinueLoad()) && ftpFile != null && ftpFile.getSize() <= localFile.length())
        {
            if (ftpFile.getSize() < localFile.length())
            {
                inputStream.skip(ftpFile.getSize());
                outputStream = ftpClient.appendFileStream(ftpFileName);
            }
            this.increaseFinished(ftpFile.getSize());
        }
        else
            outputStream = ftpClient.storeFileStream(ftpFileName);
        return outputStream;
    }

    @Override
    protected void innerExecute() throws FTPSessionException, IOException
    {
        FTPContext ftpContext = this.getFtpContext();
        Collection<FTPFileInfo> ftpFileInfos = this.loadFileInfos(ftpContext.getLocalPath());
        if (ftpFileInfos != null && !ftpFileInfos.isEmpty())
        {
            FTPClient ftpClient = ftpContext.getFtpClient();
            String currentDirectory = ftpClient.printWorkingDirectory();
            this.makeDirectory(ftpClient, this.getRemoteRootPath());

            for (FTPFileInfo ftpFileInfo : ftpFileInfos)
                this.loadFTPFileInfo(ftpFileInfo);
            for (FTPFileInfo ftpFileInfo : ftpFileInfos)
                this.upload(ftpFileInfo);

            // 转至上一层目录
            ftpClient.changeWorkingDirectory(currentDirectory);
        }
    }

    private Collection<FTPFileInfo> loadFileInfos(File localFile)
    {
        return this.loadFileInfos(null, localFile);
    }

    /**
     * 列出本地上的文件。
     * @param ftpFileInfos 本地文件信息。
     * @param localFile 本地路径。
     */
    private Collection<FTPFileInfo> loadFileInfos(FTPFileInfo parent, File localFile)
    {
        FTPContext ftpContext = this.getFtpContext();
        Collection<FTPFileInfo> ftpFileInfos = null;
        if (localFile.isDirectory())
        {
            File[] files = null;
            if (ftpContext.getLocalFileFilter() != null)
                files = localFile.listFiles(ftpContext.getLocalFileFilter());
            else
                files = localFile.listFiles();
            if (files != null && files.length > 0)
            {
                ftpFileInfos = new LinkedList<FTPFileInfo>();
                for (File file : files)
                {
                    if (ftpContext.isCancel())
                        break;

                    FTPFileInfo ftpFileInfo = this.createFileInfo(parent, file);
                    Collection<FTPFileInfo> children = this.loadFileInfos(ftpFileInfo, file);
                    if (children != null)
                        ftpFileInfo.setChildren(children);
                    ftpFileInfos.add(ftpFileInfo);
                }
            }
        }
        else if (localFile.isFile())
        {
            FileFilter localFileFilter = ftpContext.getLocalFileFilter();
            if (localFileFilter == null || localFileFilter.accept(localFile))
            {
                FTPFileInfo ftpFileInfo = this.createFileInfo(parent, localFile);
                ftpContext.increaseTotalSize(localFile.length());
                ftpFileInfos = new LinkedList<FTPFileInfo>();
                ftpFileInfos.add(ftpFileInfo);
            }
        }
        return ftpFileInfos;
    }

    private void loadFTPFileInfo(FTPFileInfo ftpFileInfo) throws IOException
    {
        FTPContext ftpContext = this.getFtpContext();
        FTPClient ftpClient = ftpContext.getFtpClient();
        File localFile = ftpFileInfo.getLocalFile();
        FTPFile[] ftpFiles = ftpClient.listFiles(localFile.getName());
        if (ftpFiles != null && ftpFiles.length > 0)
        {
            FTPFile ftpFile = ftpFiles[0];
            ftpFileInfo.setFtpFile(ftpFile);
            if (ftpFileInfo.getChildren() != null)
            {
                String currentDirectory = ftpClient.printWorkingDirectory();
                String ftpFileName = this.getFTPFileName(localFile.getName(), null);
                ftpClient.changeWorkingDirectory(ftpFileName);
                for (FTPFileInfo child : ftpFileInfo.getChildren())
                {
                    if (ftpContext.isCancel())
                        break;

                    this.loadFTPFileInfo(child);
                }
                ftpClient.changeWorkingDirectory(currentDirectory);
            }
        }
    }

    /**
     * 建立远端目录。
     * @param ftpClient FTP客户端对象。
     * @param remotePath 远端路径。
     * @return 当前路径。
     * @throws IOException
     * @throws FTPSessionException
     */
    private void makeDirectory(FTPClient ftpClient, String remotePath) throws IOException, FTPSessionException
    {
        if (!ValueUtils.isEmpty(remotePath))
        {
            FTPContext ftpContext = this.getFtpContext();
            String[] paths = remotePath.split(FILE_SEPARATOR);
            if (paths != null && paths.length > 0)
            {
                for (String path : paths)
                {
                    if (ftpContext.isCancel())
                        return;

                    this.createFTPPath(ftpClient, path);
                }
            }
            else
                this.createFTPPath(ftpClient, remotePath);
        }
    }

    /**
     * 上传文件或者目录。
     * @param ftpFileInfo 本地路径。
     * @throws IOException 运行异常。
     * @throws FTPSessionException
     */
    private void upload(FTPFileInfo ftpFileInfo) throws IOException, FTPSessionException
    {
        FTPContext ftpContext = this.getFtpContext();
        FTPClient ftpClient = ftpContext.getFtpClient();
        File localFile = ftpFileInfo.getLocalFile();
        if (localFile.isDirectory())
        {
            String currentDirectory = ftpClient.printWorkingDirectory();
            this.makeDirectory(ftpClient, localFile.getName());
            if (ftpFileInfo.getChildren() != null)
            {
                for (FTPFileInfo childFTPFileInfo : ftpFileInfo.getChildren())
                {
                    if (ftpContext.isCancel())
                        break;

                    this.upload(childFTPFileInfo);
                }
            }
            // 转至上一层目录
            ftpClient.changeWorkingDirectory(currentDirectory);
        }
        else
            this.uploadFile(ftpFileInfo);

    }

    /**
     * 上传文件或者目录。
     * @param localPath 本地路径。
     * @param remotePath 远程路径。
     * @throws IOException 运行异常。
     * @throws FTPSessionException
     */
    private void uploadFile(FTPFileInfo ftpFileInfo) throws IOException, FTPSessionException
    {
        File localFile = ftpFileInfo.getLocalFile();
        FTPFile ftpFile = ftpFileInfo.getFtpFile();

        FTPContext ftpContext = this.getFtpContext();
        FTPClient ftpClient = ftpContext.getFtpClient();
        FTPCallBack ftpCallBack = ftpContext.getCallBack();
        String remotePath = ftpFileInfo.getRemotePath();

        if (this.logger.isDebugEnabled())
            this.logger.debug("Begin upload file " + localFile.getName() + " to " + remotePath);

        long beginTime = System.currentTimeMillis();
        long finishedSize = 0l;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        boolean result = false;
        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(localFile));
            outputStream = this.createOutputStream(localFile, ftpFile, ftpClient, inputStream);
            if (outputStream != null)
            {
                finishedSize = this.copyStream(ftpContext, inputStream, outputStream);
                if (ftpCallBack != null)
                    ftpCallBack.onSucess(ftpContext, localFile, remotePath);
                result = true;
            }
        }
        catch (IOException e)
        {
            if (ftpCallBack != null)
                ftpCallBack.onFailure(ftpContext, localFile, remotePath, e);
            throw e;
        }
        finally
        {
            FileUtils.close(inputStream);
            FileUtils.close(outputStream);
            if (result)
                ftpClient.completePendingCommand();

            long costTime = System.currentTimeMillis() - beginTime;
            this.increaseCostTime(costTime);

            if (this.logger.isDebugEnabled())
                this.logger.debug(this.createDebugInfo(localFile, remotePath, finishedSize, costTime));
        }
    }
}

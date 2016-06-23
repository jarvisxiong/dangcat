package org.dangcat.net.ftp.service.impl;

import org.apache.commons.net.ftp.FTPFile;
import org.dangcat.boot.service.impl.WatchThreadExecutor;
import org.dangcat.net.ftp.exceptions.FTPSessionException;
import org.dangcat.net.ftp.service.FTPCallBack;

import java.io.File;

/**
 * FTP会话。
 * @author dangcat
 * 
 */
public class FTPSession
{
    private Boolean continueLoad = null;
    private String controlEncoding = null;
    private FTPClientPool ftpClientPool = null;

    public FTPSession(FTPClientPool ftpClientPool)
    {
        this.ftpClientPool = ftpClientPool;
    }

    /**
     * 删除远端文件。
     * @param ftpContext FTP上下文。
     * @throws FTPSessionException 远程操作异常。
     */
    public void delete(FTPContext ftpContext) throws FTPSessionException
    {
        this.execute(new FTPDeleteSession(ftpContext, this));
    }

    /**
     * 删除远端文件。
     * @param remotePath 远程路径。
     * @throws FTPSessionException
     */
    public void delete(String remotePath) throws FTPSessionException
    {
        this.delete(new FTPContext(remotePath));
    }

    /**
     * 下载文件。
     * @param localPath 本地文件路径。
     * @param remotePath 远端路径。
     * @param ftpCallBack 回调接口。
     * @throws FTPSessionException
     */
    public void download(File localPath, String remotePath, FTPCallBack ftpCallBack) throws FTPSessionException
    {
        this.download(new FTPContext(localPath, remotePath, ftpCallBack));
    }

    /**
     * 下载文件。
     * @param ftpContext FTP上下文。
     * @throws FTPSessionException 运行异常。
     */
    public void download(FTPContext ftpContext) throws FTPSessionException
    {
        this.execute(new FTPDownloadSession(ftpContext, this));
    }

    private void execute(FTPSessionExecutor ftpSessionExecutor) throws FTPSessionException
    {
        try
        {
            ftpSessionExecutor.beginSubmit();
            WatchThreadExecutor.getInstance().submit(ftpSessionExecutor);
        }
        finally
        {
            ftpSessionExecutor.endSubmit();
        }
    }

    public String getControlEncoding()
    {
        if (this.controlEncoding == null)
            return this.ftpClientPool.getControlEncoding();
        return this.controlEncoding;
    }

    public void setControlEncoding(String controlEncoding) {
        this.controlEncoding = controlEncoding;
    }

    protected FTPClientPool getFtpClientPool()
    {
        return this.ftpClientPool;
    }

    public Boolean isContinueLoad()
    {
        if (this.continueLoad == null)
            return this.ftpClientPool.isContinueLoad();
        return this.continueLoad;
    }

    /**
     * 列出远端文件名。
     * @param ftpContext FTP上下文。
     * @throws FTPSessionException
     */
    public FTPFile[] list(FTPContext ftpContext) throws FTPSessionException
    {
        FTPListSession ftpListSession = new FTPListSession(ftpContext, this);
        this.execute(ftpListSession);
        return ftpListSession.getFtpFiles();
    }

    /**
     * 枚举远程路径的内容。
     * @param remotePath 远程路径。
     * @return
     * @throws FTPSessionException 远程操作异常。
     */
    public FTPFile[] list(String remotePath) throws FTPSessionException
    {
        return this.list(new FTPContext(remotePath));
    }

    /**
     * 重新命名。
     * @param from 来源文件。
     * @param to 目标文件。
     */
    public boolean rename(String from, String to)
    {
        FTPRenameSession ftpRenameSession = new FTPRenameSession(new FTPContext(from), this);
        try
        {
            ftpRenameSession.setFrom(from);
            ftpRenameSession.setTo(to);
            this.execute(ftpRenameSession);
        }
        catch (FTPSessionException e)
        {
        }
        return ftpRenameSession.isSuccess();
    }

    public void setContinueLoad(Boolean continueLoad)
    {
        this.continueLoad = continueLoad;
    }

    /**
     * 测试是否可以正常连接到服务器。
     */
    public FTPSessionException testConnect()
    {
        FTPSessionExecutor ftpSessionExecutor = new FTPTestConnectionSession(new FTPContext(""), this);
        ftpSessionExecutor.run();
        return ftpSessionExecutor.getException();
    }

    /**
     * 上传文件或目录。
     * @param localPath 本地路径。
     * @param remotePath 远程路径。
     * @param ftpCallBack 回调接口。
     * @throws FTPSessionException
     */
    public void upload(File localPath, String remotePath, FTPCallBack ftpCallBack) throws FTPSessionException
    {
        this.upload(new FTPContext(localPath, remotePath, ftpCallBack));
    }

    /**
     * 上传文件或目录。
     * @param ftpContext FTP上下文。
     * @throws FTPSessionException
     */
    public void upload(FTPContext ftpContext) throws FTPSessionException
    {
        this.execute(new FTPUploadSession(ftpContext, this));
    }
}
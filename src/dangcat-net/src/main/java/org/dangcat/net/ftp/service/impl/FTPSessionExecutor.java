package org.dangcat.net.ftp.service.impl;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.log4j.Logger;
import org.dangcat.boot.service.WatchRunnable;
import org.dangcat.commons.formator.OctetsFormator;
import org.dangcat.commons.formator.OctetsVelocityFormator;
import org.dangcat.commons.formator.ValueFormator;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.net.ftp.exceptions.FTPSessionException;
import org.dangcat.net.ftp.service.FTPCallBack;

import java.io.*;

/**
 * FTP下载会话。
 * @author dangcat
 * 
 */
abstract class FTPSessionExecutor implements WatchRunnable
{
    protected static final String FILE_SEPARATOR = "/";
    private static final String CHARSETNAME = "iso-8859-1";
    protected Logger logger = Logger.getLogger(this.getClass());
    private long beginTime = System.currentTimeMillis();
    private FTPSessionException exception = null;
    private FTPContext ftpContext = null;
    private FTPSession ftpSession = null;
    private ValueFormator octetsFormator = new OctetsFormator();
    private ValueFormator octetsVelocityFormator = new OctetsVelocityFormator();

    FTPSessionExecutor(FTPContext ftpContext, FTPSession ftpSession)
    {
        this.ftpContext = ftpContext;
        this.ftpSession = ftpSession;
    }

    protected void beginSubmit()
    {
        if (this.logger.isDebugEnabled())
            this.logger.debug(this.createBeginLogMessage());
    }

    private long calculateVelocity(long finishedSize, long costTime)
    {
        long second = costTime / 1000l;
        return second == 0l ? 0 : finishedSize / second;
    }

    protected long copyStream(FTPContext ftpContext, InputStream inputStream, OutputStream outputStream) throws IOException
    {
        long finishedSize = 0l;
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1)
        {
            if (ftpContext.isCancel())
                break;

            outputStream.write(buffer, 0, length);
            ftpContext.increaseFinished(length);
            finishedSize += length;
        }
        ftpContext.increaseTotalCount();
        return finishedSize;
    }

    private String createBeginLogMessage()
    {
        String message = "Begin to " + this.getFtpContext().getName();
        StringBuilder info = new StringBuilder();
        String localPath = null;
        if (this.getFtpContext().getLocalPath() != null)
            localPath = this.getFtpContext().getLocalPath().getAbsolutePath();
        this.createLogMessage(info, message, this.getRemoteRootPath(), localPath, null, null, null);
        return info.toString();
    }

    private String createEndLogMessage()
    {
        FTPContext ftpContext = this.getFtpContext();
        String message = "End " + ftpContext.getName();
        StringBuilder info = new StringBuilder();
        String localPath = null;
        if (ftpContext.getLocalPath() != null)
            localPath = ftpContext.getLocalPath().getAbsolutePath();
        this.createLogMessage(info, message, this.getRemoteRootPath(), localPath, ftpContext.getCostTime(), ftpContext.getTotalCount(), ftpContext.getFinishedSize());
        return info.toString();
    }

    private String createExceptionLogMessage()
    {
        if (this.exception == null)
            return null;

        StringBuilder info = new StringBuilder();
        info.append("execute ");
        info.append(this.getFtpContext().getName());
        info.append(" error ");
        info.append(this.exception.getMessage());
        return info.toString();
    }

    protected void createLogMessage(StringBuilder info, String message, String remotePath, String localPath, Long costTime, Integer totalCount, Long finishedSize)
    {
        if (!ValueUtils.isEmpty(message))
            info.append(message);
        if (!ValueUtils.isEmpty(localPath))
        {
            info.append(" LocalPath=");
            info.append(localPath);
        }
        if (!ValueUtils.isEmpty(remotePath))
        {
            info.append(" RemotePath=");
            info.append(remotePath);
        }
        if (totalCount != null && totalCount != 0)
        {
            info.append(" TotalCount=");
            info.append(totalCount);
        }
        if (costTime != null && costTime != 0)
        {
            info.append(" TimeCost=");
            info.append(costTime);
            info.append("(ms)");
        }
        if (finishedSize != null && finishedSize != 0)
        {
            info.append(" FinishedSize=");
            info.append(this.octetsFormator.format(finishedSize));
            if (costTime != null)
            {
                long velocity = this.calculateVelocity(finishedSize, costTime);
                if (velocity != 0l)
                {
                    info.append(" Velocity=");
                    info.append(this.octetsVelocityFormator.format(velocity));
                }
            }
        }
        info.append(".");
    }

    protected void endSubmit() throws FTPSessionException
    {
        FTPCallBack ftpCallBack = this.ftpContext.getCallBack();
        long costTime = System.currentTimeMillis() - this.beginTime;
        this.ftpContext.setCostTime(costTime);

        if (this.exception != null)
        {
            String message = this.createExceptionLogMessage();
            if (message != null)
            {
                if (this.logger.isDebugEnabled())
                    this.logger.error(message, this.getException());
                else
                    this.logger.error(message);
            }
        }
        else if (this.logger.isDebugEnabled())
            this.logger.debug(this.createEndLogMessage());
        if (ftpCallBack != null)
        {
            if (this.getException() != null)
                ftpCallBack.onFailure(this.ftpContext, null, this.ftpContext.getRemotePath(), this.getException());
            else
                ftpCallBack.onSucess(this.ftpContext, null, this.ftpContext.getRemotePath());
        }
        this.release();
        if (this.getException() != null)
            throw this.getException();
    }

    protected FTPSessionException getException()
    {
        return this.exception;
    }

    protected FTPClient getFTPClient() throws FTPSessionException
    {
        FTPClient ftpClient = this.ftpContext.getFtpClient();
        if (this.ftpContext.getFtpClient() == null)
        {
            ftpClient = this.getFtpSession().getFtpClientPool().getFTPClient();
            this.ftpContext.setFtpClient(ftpClient);
        }
        return ftpClient;
    }

    protected FTPContext getFtpContext()
    {
        return this.ftpContext;
    }

    protected String getFTPFileName(String path, String fileName)
    {
        StringBuilder info = new StringBuilder();
        if (!ValueUtils.isEmpty(path))
            info.append(path);
        if (!ValueUtils.isEmpty(fileName))
        {
            if (info.length() > 0)
                info.append("/");
            info.append(fileName);
        }
        String ftpFileName = info.toString();
        try
        {
            byte[] bytes = ftpFileName.getBytes(this.getFtpSession().getControlEncoding());
            ftpFileName = new String(bytes, CHARSETNAME);
        }
        catch (UnsupportedEncodingException e)
        {
        }
        return ftpFileName;
    }

    protected FTPSession getFtpSession()
    {
        return this.ftpSession;
    }

    @Override
    public long getLastResponseTime()
    {
        return this.ftpContext.getLastResponseTime();
    }

    @Override
    public Logger getLogger()
    {
        return this.logger;
    }

    protected String getRemoteRootPath()
    {
        String remoteRootPath = this.getFtpContext().getRemotePath();
        return ValueUtils.isEmpty(remoteRootPath) ? "" : remoteRootPath;
    }

    @Override
    public long getTimeOutLength()
    {
        return this.getFtpSession().getFtpClientPool().getExecuteTimeOut();
    }

    protected void increaseCostTime(long value)
    {
        this.getFtpContext().increaseCostTime(value);
    }

    protected void increaseFinished(long value)
    {
        this.getFtpContext().increaseFinished(value);
    }

    protected abstract void innerExecute() throws FTPSessionException, IOException;

    protected Boolean isContinueLoad()
    {
        return this.getFtpSession().isContinueLoad();
    }

    protected boolean isRootPath(FTPFile ftpFile)
    {
        return ftpFile.getName().equalsIgnoreCase(".") || ftpFile.getName().equalsIgnoreCase("..");
    }

    /**
     * 列出FTP上的文件。
     * @param ftpContext 上下文。
     * @param remotePath 远端路径。
     * @return
     * @throws IOException
     */
    protected FTPFile[] listFTPFiles(String remotePath) throws IOException
    {
        long beginTime = System.currentTimeMillis();
        FTPContext ftpContext = this.getFtpContext();
        String ftpFileName = this.getFTPFileName(remotePath, null);
        FTPClient ftpClient = ftpContext.getFtpClient();
        FTPFile[] FTPFiles = null;
        final FilenameFilter filenameFilter = ftpContext.getRemoteFileFilter();
        if (filenameFilter != null)
        {
            final File directory = remotePath == null ? null : new File(remotePath);
            FTPFiles = ftpClient.listFiles(ftpFileName, new FTPFileFilter()
            {
                @Override
                public boolean accept(FTPFile ftpFile)
                {
                    FTPSessionExecutor.this.response();
                    return filenameFilter.accept(directory, ftpFile.getName());
                }
            });
        }
        else
            FTPFiles = ftpClient.listFiles(ftpFileName);
        this.increaseCostTime(System.currentTimeMillis() - beginTime);
        this.response();
        return FTPFiles;
    }

    private void prepare() throws FTPSessionException
    {
        this.ftpContext.reset();
        this.getFTPClient();
        this.ftpContext.setFtpSession(this.ftpSession);
        if (!ValueUtils.isEmpty(this.ftpContext.getRemotePath()))
            this.ftpContext.setRemotePath(this.ftpContext.getRemotePath().replace("\\", FILE_SEPARATOR));
    }

    private void release()
    {
        this.release(this.ftpContext.getFtpClient());
        this.ftpContext.setFtpClient(null);
        this.ftpContext.setFtpSession(null);
    }

    private void release(FTPClient ftpClient)
    {
        if (ftpClient != null)
        {
            FTPClientPool ftpClientPool = this.getFtpSession().getFtpClientPool();
            if (this.getException() != null)
                ftpClientPool.destroy(ftpClient);
            else
                ftpClientPool.release(ftpClient);
        }
        this.getFtpSession().setContinueLoad(null);
    }

    protected void response()
    {
        this.getFtpContext().response();
    }

    public void run()
    {
        try
        {
            this.prepare();
            this.innerExecute();

        }
        catch (FTPSessionException e)
        {
            this.exception = e;
        }
        catch (Exception e)
        {
            this.exception = new FTPSessionException(e);
        }
    }

    @Override
    public void terminate()
    {
        this.ftpContext.setCancel(true);
        this.exception = new FTPSessionException(FTPSessionException.TIMEOUT);
        this.ftpContext.getFtpClient().setTimeout(true);
    }

    @Override
    public String toString()
    {
        FTPContext ftpContext = this.getFtpContext();
        StringBuilder info = new StringBuilder();
        String localPath = null;
        if (ftpContext.getLocalPath() != null)
            localPath = ftpContext.getLocalPath().getAbsolutePath();
        this.createLogMessage(info, ftpContext.getName(), this.getRemoteRootPath(), localPath, null, null, null);
        return info.toString();
    }
}

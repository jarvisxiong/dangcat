package org.dangcat.net.ftp.service.impl;

import java.util.Map;

import org.apache.commons.net.ftp.FTP;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.pool.ConnectionPool;
import org.dangcat.framework.pool.SessionException;
import org.dangcat.net.ftp.exceptions.FTPSessionException;

public class FTPClientPool extends ConnectionPool<FTPClient>
{
    private static final String ANONYMOUS = "anonymous";
    private static final int DEFAULT_CONNECTMODE = FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE;
    private static final int DEFAULT_CONNECTTIMEOUT = 30000;
    private static final Boolean DEFAULT_CONTINUELOAD = Boolean.FALSE;
    private static final String DEFAULT_CONTROLENCODING = "GBK";
    private static final int DEFAULT_DATATIMEOUT = 30000;
    private static final int DEFAULT_EXECUTETIMEOUT = 60000;
    private static final int DEFAULT_FILETYPE = FTP.BINARY_FILE_TYPE;
    private static final int DEFAULT_FTP_PORT = 21;
    private static final int DEFAULT_TIMEOUT = 30000;
    private Integer connectMode = null;
    private Integer connectTimeout = null;
    private Boolean continueLoad = null;
    private String controlEncoding = null;
    private Integer dataTimeout = null;
    private Integer defaultTimeout = null;
    private Integer executeTimeOut = null;
    private Integer fileType = null;
    private String initPath = null;
    private String password = null;
    private Integer port = DEFAULT_FTP_PORT;
    private String server = null;
    private String userName = null;

    public FTPClientPool()
    {
        super(null, null);
    }

    public FTPClientPool(String name, Map<String, String> params)
    {
        super(name, params);
    }

    /**
     * 关闭连接
     */
    @Override
    protected void close(FTPClient ftpClient)
    {
        ftpClient.close();
    }

    @Override
    protected FTPClient create()
    {
        return new FTPClient(this);
    }

    public int getConnectMode()
    {
        return this.connectMode == null ? DEFAULT_CONNECTMODE : this.connectMode;
    }

    public int getConnectTimeout()
    {
        return this.connectTimeout == null ? DEFAULT_CONNECTTIMEOUT : this.connectTimeout;
    }

    public String getControlEncoding()
    {
        return this.controlEncoding == null ? DEFAULT_CONTROLENCODING : this.controlEncoding;
    }

    public int getDataTimeout()
    {
        return this.dataTimeout == null ? DEFAULT_DATATIMEOUT : this.dataTimeout;
    }

    public int getDefaultTimeout()
    {
        return this.defaultTimeout == null ? DEFAULT_TIMEOUT : this.defaultTimeout;
    }

    public int getExecuteTimeOut()
    {
        return this.executeTimeOut == null ? DEFAULT_EXECUTETIMEOUT : this.executeTimeOut;
    }

    /**
     * ASCII_FILE_TYPE = 0; EBCDIC_FILE_TYPE = 1; BINARY_FILE_TYPE = 2;
     * LOCAL_FILE_TYPE = 3; NON_PRINT_TEXT_FORMAT = 4; TELNET_TEXT_FORMAT = 5;
     * CARRIAGE_CONTROL_TEXT_FORMAT = 6; FILE_STRUCTURE = 7; RECORD_STRUCTURE =
     * 8; PAGE_STRUCTURE = 9; STREAM_TRANSFER_MODE = 10; BLOCK_TRANSFER_MODE =
     * 11; COMPRESSED_TRANSFER_MODE = 12;
     */
    public int getFileType()
    {
        return this.fileType == null ? DEFAULT_FILETYPE : this.fileType;
    }

    public FTPClient getFTPClient() throws FTPSessionException
    {
        if (this.logger.isDebugEnabled())
            this.logger.debug("Begin get FTPClient.");

        FTPClient ftpClient = null;
        try
        {
            do
            {
                if (ftpClient != null)
                    this.destroy(ftpClient);
                ftpClient = this.poll();
            } while (ftpClient.isLogined() && !ftpClient.isValidate());

            boolean result = false;
            if (!ftpClient.isLogined())
                result = ftpClient.login();
            else
                result = ftpClient.isValidate();
            if (!result)
            {
                this.destroy(ftpClient);
                Integer lastReplyCode = ftpClient.getLastReplyCode();
                String lastReplyMessage = ftpClient.getReplyMessage(lastReplyCode, null);
                if (lastReplyCode != 0 && !ValueUtils.isEmpty(lastReplyMessage))
                    throw new FTPSessionException(lastReplyCode);
                throw new FTPSessionException(FTPSessionException.CONNECTION, lastReplyCode);
            }
            if (this.logger.isDebugEnabled())
                this.logger.debug("initialize the ftpClient.");
            // 初始化客户端
            ftpClient.enterInit();
            if (this.logger.isDebugEnabled())
                this.logger.debug("Get ftpClient suceseful.");
        }
        catch (FTPSessionException e)
        {
            if (ftpClient != null)
                this.destroy(ftpClient);
            throw e;
        }
        return ftpClient;
    }

    public String getInitPath()
    {
        return this.initPath == null ? "" : this.initPath;
    }

    public String getPassword()
    {
        if (ValueUtils.isEmpty(this.userName))
            return ANONYMOUS + "@" + this.server;
        return this.password;
    }

    public Integer getPort()
    {
        return this.port;
    }

    public String getServer()
    {
        return this.server;
    }

    public String getUserName()
    {
        if (ValueUtils.isEmpty(this.userName))
            return ANONYMOUS;
        return this.userName;
    }

    /**
     * 初始化连接池。
     * @throws SessionException
     */
    @Override
    public void initialize() throws SessionException
    {
        super.initialize();
        if (this.initPath != null)
            this.initPath = this.initPath.replace("\\", "/");
    }

    public Boolean isContinueLoad()
    {
        return this.continueLoad == null ? DEFAULT_CONTINUELOAD : this.continueLoad;
    }

    public void setConnectMode(Integer connectMode)
    {
        this.connectMode = connectMode;
    }

    public void setConnectTimeout(Integer connectTimeout)
    {
        this.connectTimeout = connectTimeout;
    }

    public void setContinueLoad(Boolean continueLoad)
    {
        this.continueLoad = continueLoad;
    }

    public void setControlEncoding(String controlEncoding)
    {
        this.controlEncoding = controlEncoding;
    }

    public void setDataTimeout(Integer dataTimeout)
    {
        this.dataTimeout = dataTimeout;
    }

    public void setDefaultTimeout(Integer defaultTimeout)
    {
        this.defaultTimeout = defaultTimeout;
    }

    public void setExecuteTimeOut(Integer executeTimeOut)
    {
        this.executeTimeOut = executeTimeOut;
    }

    public void setFileType(Integer fileType)
    {
        this.fileType = fileType;
    }

    public void setInitPath(String initPath)
    {
        this.initPath = initPath;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }
}

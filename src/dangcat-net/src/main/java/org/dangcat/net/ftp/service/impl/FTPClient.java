package org.dangcat.net.ftp.service.impl;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.dangcat.commons.resource.ResourceManager;
import org.dangcat.commons.resource.ResourceReader;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.net.ftp.exceptions.FTPSessionException;

import java.io.IOException;
import java.util.Locale;

public class FTPClient extends org.apache.commons.net.ftp.FTPClient {
    private static final Logger logger = Logger.getLogger(FTPClient.class);
    private static ResourceReader resourceReader = ResourceManager.getInstance().getResourceReader(FTPSessionException.class);
    private FTPClientPool ftpClientPool = null;
    private boolean isLogined = false;
    private boolean isTimeout = false;
    private int lastReplyCode = 0;

    public FTPClient(FTPClientPool ftpClientPool) {
        this.ftpClientPool = ftpClientPool;
        this.addProtocolCommandListener(new PrintCommandListener(new FTPPrintWriter(logger)));
        this.initialize();
    }

    public void close() {
        if (this.isConnected()) {
            if (logger.isDebugEnabled())
                logger.debug("Begin to close ftpClient.");
            if (this.isTimeout()) {
                try {
                    this.logout();
                } catch (Exception e) {
                    if (logger.isDebugEnabled())
                        logger.error(this, e);
                }
            }
            try {
                this.disconnect();
            } catch (Exception e) {
                if (logger.isDebugEnabled())
                    logger.error(this, e);
            }
            this.isLogined = false;
            if (logger.isDebugEnabled())
                logger.debug("End close ftpClient.");
        }
    }

    protected void enterInit() throws FTPSessionException {
        try {
            this.initialize();
            // 文件传输采用二进制，传输图片
            this.setFileType(this.ftpClientPool.getFileType());
            // 设置连接模式
            if (this.ftpClientPool.getConnectMode() == FTPClient.PASSIVE_REMOTE_DATA_CONNECTION_MODE)
                this.enterRemotePassiveMode();

            this.enterInitPath();
        } catch (Exception e) {
            throw new FTPSessionException(e);
        }
    }

    private void enterInitPath() throws IOException {
        String initPath = this.ftpClientPool.getInitPath();
        this.changeWorkingDirectory("/");
        if (!ValueUtils.isEmpty(initPath)) {
            if (logger.isDebugEnabled())
                logger.debug("ftpClient enter initPath :" + initPath);
            this.changeWorkingDirectory(initPath);
            int replyCode = this.printReplyMessage();
            if (!FTPReply.isPositiveCompletion(replyCode))
                throw new IOException();
        }
    }

    public int getLastReplyCode() {
        return this.lastReplyCode;
    }

    public String getReplyMessage(Integer replyCode, Locale locale) {
        return resourceReader.getText(FTPSessionException.class.getSimpleName() + "." + replyCode, locale);
    }

    private void initialize() {
        this.lastReplyCode = 0;
        // 设置连接模式
        switch (this.ftpClientPool.getConnectMode()) {
            case FTPClient.ACTIVE_LOCAL_DATA_CONNECTION_MODE:
                this.enterLocalActiveMode();
                break;
            case FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE:
                this.enterLocalPassiveMode();
                break;
        }
        // 设置数据传输超时
        this.setDataTimeout(this.ftpClientPool.getDataTimeout());
        // 设置连接超时
        this.setDefaultTimeout(this.ftpClientPool.getDefaultTimeout());
        // 设置连接超时
        this.setConnectTimeout(this.ftpClientPool.getConnectTimeout());
        // 设置字符集
        this.setControlEncoding(this.ftpClientPool.getControlEncoding());
    }

    public boolean isLogined() {
        return this.isLogined;
    }

    protected boolean isTimeout() {
        return this.isTimeout;
    }

    protected void setTimeout(boolean isTimeout) {
        this.isTimeout = isTimeout;
    }

    public boolean isValidate() {
        boolean result = this.isLogined() && this.isAvailable();
        if (result) {
            try {
                int replyCode = this.pwd();
                if (!FTPReply.isPositiveCompletion(replyCode))
                    result = false;
            } catch (IOException e) {
                result = false;
            }
        }
        return result;
    }

    public boolean login() {
        if (this.isLogined)
            return this.isLogined;

        this.isLogined = false;
        this.lastReplyCode = 0;
        String ftpUrl = "ftp://" + this.ftpClientPool.getServer() + ":" + this.ftpClientPool.getPort();
        try {
            if (logger.isDebugEnabled())
                logger.debug("connect to " + ftpUrl);
            this.connect(this.ftpClientPool.getServer(), this.ftpClientPool.getPort());
            int replyCode = this.getReplyCode();
            this.printReplyMessage();
            if (!FTPReply.isPositiveCompletion(replyCode))
                logger.error("FTP server refused connection from " + ftpUrl);
            else {
                // 连接成功，身份验证
                if (!this.login(this.ftpClientPool.getUserName(), this.ftpClientPool.getPassword())) {
                    this.printReplyMessage();
                    logger.error("The username " + this.ftpClientPool.getUserName() + " validate failure");
                } else {
                    logger.info("login success: " + ftpUrl);
                    this.isLogined = true;
                }
            }
        } catch (Exception e) {
            String message = "can not login the ftp server by " + ftpUrl;
            if (logger.isDebugEnabled())
                logger.error(message, e);
            else
                logger.error(message + e.getMessage());
        }
        return this.isLogined;
    }

    private int printReplyMessage() {
        int replyCode = this.getReplyCode();
        String message = this.getReplyMessage(replyCode, null);
        if (!ValueUtils.isEmpty(message)) {
            if (FTPReply.isPositiveCompletion(replyCode)) {
                if (logger.isDebugEnabled())
                    logger.debug("(" + replyCode + ")" + message);
            } else
                logger.error("(" + replyCode + ")" + message);
        }
        this.lastReplyCode = replyCode;
        return replyCode;
    }
}

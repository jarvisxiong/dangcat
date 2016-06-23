package org.dangcat.net.ftp.service.impl;

import org.apache.log4j.Logger;
import org.dangcat.framework.pool.SessionException;

/**
 * FTPœ¬‘ÿª·ª∞°£
 *
 * @author dangcat
 */
public class FTPClientSession extends FTPSession {
    private static final Logger logger = Logger.getLogger(FTPClientSession.class);

    public FTPClientSession() {
        super(new FTPClientPool());
    }

    public void close() {
        this.getFtpClientPool().close();
    }

    public int getConnectMode() {
        return this.getFtpClientPool().getConnectMode();
    }

    public void setConnectMode(int connectMode) {
        this.getFtpClientPool().setConnectMode(connectMode);
    }

    public int getConnectTimeout() {
        return this.getFtpClientPool().getConnectTimeout();
    }

    public void setConnectTimeout(int connectTimeout) {
        this.getFtpClientPool().setConnectTimeout(connectTimeout);
    }

    @Override
    public String getControlEncoding() {
        return this.getFtpClientPool().getControlEncoding();
    }

    @Override
    public void setControlEncoding(String controlEncoding) {
        this.getFtpClientPool().setControlEncoding(controlEncoding);
    }

    public int getDataTimeout() {
        return this.getFtpClientPool().getDataTimeout();
    }

    public void setDataTimeout(int dataTimeout) {
        this.getFtpClientPool().setDataTimeout(dataTimeout);
    }

    public int getDefaultTimeout() {
        return this.getFtpClientPool().getDefaultTimeout();
    }

    public void setDefaultTimeout(int defaultTimeout) {
        this.getFtpClientPool().setDefaultTimeout(defaultTimeout);
    }

    /**
     * ASCII_FILE_TYPE = 0; EBCDIC_FILE_TYPE = 1; BINARY_FILE_TYPE = 2;
     * LOCAL_FILE_TYPE = 3; NON_PRINT_TEXT_FORMAT = 4; TELNET_TEXT_FORMAT = 5;
     * CARRIAGE_CONTROL_TEXT_FORMAT = 6; FILE_STRUCTURE = 7; RECORD_STRUCTURE =
     * 8; PAGE_STRUCTURE = 9; STREAM_TRANSFER_MODE = 10; BLOCK_TRANSFER_MODE =
     * 11; COMPRESSED_TRANSFER_MODE = 12;
     */
    public int getFileType() {
        return this.getFtpClientPool().getFileType();
    }

    public void setFileType(int fileType) {
        this.getFtpClientPool().setFileType(fileType);
    }

    public String getInitPath() {
        return this.getFtpClientPool().getInitPath();
    }

    public void setInitPath(String initPath) {
        this.getFtpClientPool().setInitPath(initPath);
    }

    public String getPassword() {
        return this.getFtpClientPool().getPassword();
    }

    public void setPassword(String password) {
        this.getFtpClientPool().setPassword(password);
    }

    public Integer getPort() {
        return this.getFtpClientPool().getPort();
    }

    public void setPort(Integer port) {
        this.getFtpClientPool().setPort(port);
    }

    public String getServer() {
        return this.getFtpClientPool().getServer();
    }

    public void setServer(String server) {
        this.getFtpClientPool().setServer(server);
    }

    public String getUserName() {
        return this.getFtpClientPool().getUserName();
    }

    public void setUserName(String userName) {
        this.getFtpClientPool().setUserName(userName);
    }

    public void initialize() {
        try {
            this.getFtpClientPool().initialize();
        } catch (SessionException e) {
            logger.error(this, e);
        }
    }

    public boolean isPoolEnabled() {
        return this.getFtpClientPool().isPoolEnabled();
    }

    public void setPoolEnabled(boolean poolEnabled) {
        this.getFtpClientPool().setPoolEnabled(poolEnabled);
    }
}

package org.dangcat.net.ftp.service.impl;

import org.dangcat.net.ftp.exceptions.FTPSessionException;

import java.io.IOException;

/**
 * FTP下载会话。
 *
 * @author dangcat
 */
class FTPTestConnectionSession extends FTPSessionExecutor {
    private boolean success = false;

    FTPTestConnectionSession(FTPContext ftpContext, FTPSession ftpSession) {
        super(ftpContext, ftpSession);
        ftpContext.setName(FTPContext.OPT_TESTCONNECTION);
    }

    @Override
    protected void innerExecute() throws FTPSessionException, IOException {
        this.success = this.getFTPClient() != null;
    }

    protected boolean isSuccess() {
        return this.success;
    }
}

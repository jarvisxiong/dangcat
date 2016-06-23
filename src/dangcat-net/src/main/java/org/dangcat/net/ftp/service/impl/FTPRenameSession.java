package org.dangcat.net.ftp.service.impl;

import org.dangcat.net.ftp.exceptions.FTPSessionException;

import java.io.IOException;

/**
 * FTPœ¬‘ÿª·ª∞°£
 * @author dangcat
 * 
 */
class FTPRenameSession extends FTPSessionExecutor
{
    boolean success = false;
    private String from;
    private String to;

    FTPRenameSession(FTPContext ftpContext, FTPSession ftpSession)
    {
        super(ftpContext, ftpSession);
        ftpContext.setName(FTPContext.OPT_RENAME);
    }

    protected String getFrom()
    {
        return this.from;
    }

    protected void setFrom(String from) {
        this.from = from;
    }

    protected String getTo()
    {
        return this.to;
    }

    protected void setTo(String to) {
        this.to = to;
    }

    @Override
    protected void innerExecute() throws FTPSessionException, IOException
    {
        FTPClient ftpClient = this.getFTPClient();
        ftpClient.rename(this.from, this.to);
        this.success = true;
    }

    protected boolean isSuccess()
    {
        return this.success;
    }
}

package org.dangcat.net.ftp.service.impl;

import java.io.IOException;

import org.dangcat.net.ftp.exceptions.FTPSessionException;

/**
 * FTPœ¬‘ÿª·ª∞°£
 * @author dangcat
 * 
 */
class FTPRenameSession extends FTPSessionExecutor
{
    private String from;
    boolean success = false;
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

    protected String getTo()
    {
        return this.to;
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

    protected void setFrom(String from)
    {
        this.from = from;
    }

    protected void setTo(String to)
    {
        this.to = to;
    }
}

package org.dangcat.net.ftp.service.impl;

import org.dangcat.net.ftp.exceptions.FTPSessionException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class TestFTPClientSession extends TestFTPSession
{
    private FTPClientSession ftpClientSession = null;

    @Override
    protected FTPSession getFTPSession()
    {
        if (this.ftpClientSession == null)
        {
            FTPClientSession ftpClientSession = new FTPClientSession();
            ftpClientSession.setServer("localhost");
            ftpClientSession.setUserName("dangcat");
            ftpClientSession.setPassword("dangcat2014");
            ftpClientSession.setInitPath("download");
            ftpClientSession.setPoolEnabled(false);
            ftpClientSession.initialize();
            this.ftpClientSession = ftpClientSession;
        }
        return this.ftpClientSession;
    }

    @Override
    @After
    public void release()
    {
        if (this.ftpClientSession != null)
            this.ftpClientSession.close();
        this.ftpClientSession = null;
    }

    @Test
    public void testConnection() throws FTPSessionException
    {
        FTPClientSession ftpSession = (FTPClientSession) this.getFTPSession();
        ftpSession.setUserName("xxx");
        FTPSessionException exception = ftpSession.testConnect();
        Assert.assertNotNull(exception);
        Assert.assertEquals(new Integer(530), exception.getMessageId());

        ftpSession = (FTPClientSession) this.getFTPSession();
        ftpSession.setPassword("xxx");
        exception = ftpSession.testConnect();
        Assert.assertNotNull(exception);
        Assert.assertEquals(new Integer(530), exception.getMessageId());

        ftpSession = (FTPClientSession) this.getFTPSession();
        ftpSession.setServer("192.168.1.1");
        exception = ftpSession.testConnect();
        Assert.assertNotNull(exception);
        Assert.assertEquals(FTPSessionException.CONNECTION, exception.getMessageId());
    }
}

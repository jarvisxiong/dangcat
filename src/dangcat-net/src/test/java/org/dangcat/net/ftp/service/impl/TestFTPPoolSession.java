package org.dangcat.net.ftp.service.impl;

import org.junit.After;

public class TestFTPPoolSession extends TestFTPSession {
    @Override
    protected FTPSession getFTPSession() {
        return FTPSessionFactory.getInstance().openSession();
    }

    @Override
    @After
    public void release() {
        FTPSessionFactory.getInstance().close();
    }
}

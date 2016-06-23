package org.dangcat.net.ftp.service.impl;

import java.io.File;
import java.util.Collection;

import org.apache.commons.net.ftp.FTPFile;

class FTPFileInfo
{
    private Collection<FTPFileInfo> children = null;
    private FTPFile ftpFile = null;
    private File localFile = null;
    private FTPFileInfo parent = null;

    FTPFileInfo(FTPFileInfo parent)
    {
        this.parent = parent;
    }

    protected Collection<FTPFileInfo> getChildren()
    {
        return this.children;
    }

    protected FTPFile getFtpFile()
    {
        return this.ftpFile;
    }

    protected File getLocalFile()
    {
        return this.localFile;
    }

    protected FTPFileInfo getParent()
    {
        return this.parent;
    }

    protected String getRemotePath()
    {
        StringBuilder info = new StringBuilder();
        if (this.parent != null)
            info.append(this.getParent().getRemotePath());
        if (info.length() > 0)
            info.append("/");
        if (this.ftpFile != null)
            info.append(this.ftpFile.getName());
        else
            info.append(this.localFile.getName());
        return info.toString();
    }

    protected void setChildren(Collection<FTPFileInfo> children)
    {
        this.children = children;
    }

    protected void setFtpFile(FTPFile ftpFile)
    {
        this.ftpFile = ftpFile;
    }

    protected void setLocalFile(File localFile)
    {
        this.localFile = localFile;
    }
}

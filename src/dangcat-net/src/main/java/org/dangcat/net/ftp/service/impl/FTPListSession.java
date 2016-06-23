package org.dangcat.net.ftp.service.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.dangcat.net.ftp.exceptions.FTPSessionException;

/**
 * FTPœ¬‘ÿª·ª∞°£
 * @author dangcat
 * 
 */
class FTPListSession extends FTPSessionExecutor
{
    private FTPFile[] ftpFiles = null;

    FTPListSession(FTPContext ftpContext, FTPSession ftpSession)
    {
        super(ftpContext, ftpSession);
        ftpContext.setName(FTPContext.OPT_LIST);
    }

    protected FTPFile[] getFtpFiles()
    {
        return this.ftpFiles;
    }

    @Override
    protected void innerExecute() throws FTPSessionException, IOException
    {
        FTPContext ftpContext = this.getFtpContext();
        FTPClient ftpClient = this.getFTPClient();
        final FilenameFilter filenameFilter = ftpContext.getRemoteFileFilter();
        if (filenameFilter != null)
        {
            final File directory = new File(".");
            this.ftpFiles = ftpClient.listFiles(ftpContext.getRemotePath(), new FTPFileFilter()
            {
                @Override
                public boolean accept(FTPFile ftpFile)
                {
                    return filenameFilter.accept(ftpFile.isDirectory() ? directory : null, ftpFile.getName());
                }
            });
        }
        else
            this.ftpFiles = ftpClient.listFiles(ftpContext.getRemotePath());
    }
}

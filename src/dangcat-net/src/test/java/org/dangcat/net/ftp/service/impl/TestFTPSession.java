package org.dangcat.net.ftp.service.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.dangcat.commons.io.FileNameFilter;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.FileComparer;
import org.dangcat.framework.pool.SessionException;
import org.dangcat.net.ftp.exceptions.FTPSessionException;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public abstract class TestFTPSession
{
    protected static final Logger logger = Logger.getLogger(FTPSession.class);

    @BeforeClass
    public static void initialize() throws IOException, SessionException
    {
        SimulateUtils.configure();
    }

    private void deleteRemotePath(String remotePath) throws FTPSessionException
    {
        FTPSession ftpSession = this.getFTPSession();
        ftpSession.delete(remotePath);
        this.release();
    }

    protected abstract FTPSession getFTPSession();

    private File getLocalDirectory()
    {
        String pathName = TestFTPSession.class.getPackage().getName().replace(".", "/");
        return new File(FileUtils.getResourcePath(TestFTPSession.class, pathName));
    }

    public abstract void release();

    /**
     * 测试FTP目录功能。
     * @throws FTPSessionException
     */
    @Test
    public void testFTPDirectory() throws FTPSessionException
    {
        final String remotePath = "data/Data/dir1";
        this.deleteRemotePath(remotePath);
        FTPSession ftpSession = this.getFTPSession();
        File localDirectory = this.getLocalDirectory();
        ftpSession.upload(localDirectory, remotePath, null);

        File downlodPath = SimulateUtils.createTmpDirectory(remotePath);
        ftpSession.download(downlodPath, remotePath, null);

        boolean result = FileComparer.compare(localDirectory, downlodPath, null);
        if (!result)
            logger.error("FileComparer error: ");
        Assert.assertTrue(result);
    }

    /**
     * 测试指定FTP文件功能。
     * @throws FTPSessionException
     */
    @Test
    public void testFTPFile() throws FTPSessionException
    {
        final String remotePath = "data\\dir2";
        this.deleteRemotePath(remotePath);
        FTPSession ftpSession = this.getFTPSession();
        File[] localFiles = this.getLocalDirectory().listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                return pathname.isFile();
            }
        });
        File localFile = localFiles[0];
        ftpSession.upload(localFile, remotePath, null);

        File downlodPath = SimulateUtils.createTmpDirectory(remotePath);
        String fileName = FTPSessionExecutor.FILE_SEPARATOR + localFile.getName();
        File downlodFile = new File(downlodPath.getAbsolutePath() + fileName);
        ftpSession.download(downlodPath, remotePath + fileName, null);

        boolean result = FileComparer.compare(localFile, downlodFile, null);
        if (!result)
            logger.error("FileComparer error: ");
        Assert.assertTrue(result);
    }

    /**
     * 测试FTP过滤功能。
     * @throws FTPSessionException
     */
    @Test
    public void testFTPFilter() throws FTPSessionException
    {
        // 测试过滤上传
        final String remotePath = "data/dir3";
        final String directoryName = "filter";
        final String fileName = TestFTPPoolSession.class.getSimpleName() + ".class";
        this.deleteRemotePath(remotePath);
        FTPSession ftpSession = this.getFTPSession();
        File localDirectory = this.getLocalDirectory();
        String path = localDirectory.getAbsolutePath() + File.separator + directoryName;
        FileUtils.mkdir(path);
        FileUtils.copy(new File(localDirectory.getAbsolutePath() + File.separator + fileName), new File(path));
        FileFilter localFileFilter = new FileFilter()
        {
            @Override
            public boolean accept(File file)
            {
                if (file.isDirectory())
                    return file.getName().equalsIgnoreCase("filter");
                return file.getName().startsWith("Test");
            }
        };
        ftpSession.upload(new FTPContext(localDirectory, remotePath, localFileFilter));
        File downlodPath = SimulateUtils.createTmpDirectory(remotePath);
        ftpSession.download(downlodPath, remotePath, null);
        boolean result = FileComparer.compare(localDirectory, downlodPath, localFileFilter);
        if (!result)
            logger.error("FileComparer error: ");
        Assert.assertTrue(result);

        // 测试过滤下载
        final String localPath2 = "data/dir4";
        this.deleteRemotePath(localPath2);
        FilenameFilter remoteFileFilter = new FileNameFilter("TestModel", null);
        File downlodPath2 = SimulateUtils.createTmpDirectory(localPath2);
        ftpSession.download(new FTPContext(downlodPath2, remotePath, remoteFileFilter));
        Assert.assertTrue(FileComparer.compare(localDirectory, downlodPath2, (FileFilter) remoteFileFilter));
    }

    /**
     * 测试FTP重命名功能。
     * @throws FTPSessionException
     */
    @Test
    public void testFTPRename() throws FTPSessionException
    {
        final String remotePath = "data/dir5";
        final String oldFileName = TestFTPPoolSession.class.getSimpleName() + ".class";
        final String newFileName = TestFTPPoolSession.class.getSimpleName() + ".classftp";
        final String oldDirectoryName = "filter";
        final String newDirectoryName = "filterftp";
        this.deleteRemotePath(remotePath);
        FTPSession ftpSession = this.getFTPSession();
        File localDirectory = this.getLocalDirectory();
        FileUtils.mkdir(localDirectory.getAbsolutePath() + File.separator + oldDirectoryName);
        FileFilter localFileFilter = new FileFilter()
        {
            @Override
            public boolean accept(File file)
            {
                if (file.isDirectory())
                    return file.getName().equalsIgnoreCase(oldDirectoryName);
                return file.getName().equals(oldFileName);
            }
        };
        ftpSession.upload(new FTPContext(localDirectory, remotePath, localFileFilter));
        ftpSession.rename(remotePath + FTPSessionExecutor.FILE_SEPARATOR + oldFileName, remotePath + FTPSessionExecutor.FILE_SEPARATOR + newFileName);
        ftpSession.rename(remotePath + FTPSessionExecutor.FILE_SEPARATOR + oldDirectoryName, remotePath + FTPSessionExecutor.FILE_SEPARATOR + newDirectoryName);
        File downlodPath = SimulateUtils.createTmpDirectory(remotePath);
        ftpSession.download(downlodPath, remotePath, null);

        File[] findFiles = downlodPath.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File file)
            {
                if (file.isDirectory())
                    return file.getName().equalsIgnoreCase(newDirectoryName);
                return file.getName().equals(newFileName);
            }
        });
        for (File file : findFiles)
            FileUtils.move(file, new File(file.getParent(), file.getName().replace("ftp", "")), true);
        boolean result = FileComparer.compare(localDirectory, downlodPath, localFileFilter);
        if (!result)
            logger.error("FileComparer error: ");
        Assert.assertTrue(result);
    }
}

package org.dangcat.commons.archive;

import org.apache.commons.compress.archivers.*;
import org.apache.commons.compress.utils.IOUtils;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.ValueUtils;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * 归档压缩。
 * @author dangcat
 * 
 */
abstract class Archiver
{
    private Compressor compressor = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    protected void close()
    {
        try
        {
            if (this.outputStream != null)
            {
                this.outputStream.flush();
                this.outputStream.close();
            }
            this.outputStream = null;
        }
        catch (IOException e)
        {
        }
        finally
        {
            this.inputStream = FileUtils.close(this.inputStream);
        }
    }

    protected abstract ArchiveEntry createArchiveEntry(String name, long size);

    protected InputStream createInputStream(File file) throws IOException
    {
        return new FileInputStream(file);
    }

    protected OutputStream createOutputStream(File file) throws IOException
    {
        return FileUtils.openOutputStream(file);
    }

    protected InputStream decorateInputStream(InputStream inputStream) throws IOException
    {
        InputStream decorateInputStream = null;
        try
        {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            ArchiveStreamFactory archiveStreamFactory = new ArchiveStreamFactory();
            decorateInputStream = archiveStreamFactory.createArchiveInputStream(this.getArchiverName(), bufferedInputStream);
        }
        catch (ArchiveException e)
        {
            throw new IOException(e);
        }
        return decorateInputStream;
    }

    protected OutputStream decorateOutputStream(OutputStream outputStream) throws IOException
    {
        OutputStream decorateOutputStream = null;
        try
        {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            ArchiveStreamFactory archiveStreamFactory = new ArchiveStreamFactory();
            decorateOutputStream = archiveStreamFactory.createArchiveOutputStream(this.getArchiverName(), bufferedOutputStream);
        }
        catch (ArchiveException e)
        {
            throw new IOException(e);
        }
        return decorateOutputStream;
    }

    protected abstract String getArchiverName();

    protected Compressor getCompressor()
    {
        return this.compressor;
    }

    protected String getEncoding()
    {
        return this.compressor.getEncoding();
    }

    protected InputStream getInputStream()
    {
        return this.inputStream;
    }

    protected InputStream getInputStream(ArchiveInputStream archiveInputStream, ArchiveEntry archiveEntry) throws IOException
    {
        byte[] content = new byte[(int) archiveEntry.getSize()];
        archiveInputStream.read(content);
        return new ByteArrayInputStream(content);
    }

    protected OutputStream getOutputStream()
    {
        return this.outputStream;
    }

    /**
     * 归档打包。
     * @param compressor 压缩对象。
     * @param archiveFile 归档文件。
     * @throws IOException 运行异常。
     */
    protected void pack(Compressor compressor, File archiveFile) throws IOException
    {
        this.compressor = compressor;
        try
        {
            OutputStream outputStream = this.createOutputStream(archiveFile);
            this.outputStream = this.decorateOutputStream(outputStream);
            Map<String, List<File>> fileListMap = this.compressor.getEntityMap();
            for (String path : fileListMap.keySet())
            {
                List<File> fileList = fileListMap.get(path);
                if (fileList.size() == 0)
                    this.pack(path + Compressor.FILE_SEPARATOR, null, 0);
                else
                {
                    for (File file : fileList)
                    {
                        String name = ValueUtils.isEmpty(path) ? file.getName() : path + Compressor.FILE_SEPARATOR + file.getName();
                        InputStream inputStream = null;
                        try
                        {
                            inputStream = new FileInputStream(file);
                            this.pack(name, inputStream, file.length());
                        }
                        finally
                        {
                            inputStream = FileUtils.close(inputStream);
                        }
                        this.compressor.processFile(file);
                    }
                }
            }
        }
        catch (FileNotFoundException e)
        {
        }
        finally
        {
            this.close();
        }
    }

    private void pack(String name, InputStream inputStream, long size) throws IOException
    {
        ArchiveOutputStream archiveOutputStream = (ArchiveOutputStream) this.getOutputStream();
        ArchiveEntry archiveEntry = this.createArchiveEntry(name, size);
        archiveOutputStream.putArchiveEntry(archiveEntry);
        if (size > 0)
        {
            IOUtils.copy(inputStream, archiveOutputStream);
            archiveOutputStream.closeArchiveEntry();
        }
    }

    private void unpack(ArchiveInputStream archiveInputStream, File destPath) throws IOException
    {
        ArchiveEntry archiveEntry = null;
        while ((archiveEntry = archiveInputStream.getNextEntry()) != null)
        {
            String entityName = destPath.getAbsolutePath() + Compressor.FILE_SEPARATOR + archiveEntry.getName();
            if (archiveEntry.isDirectory())
                FileUtils.mkdir(entityName);
            else
            {
                File uncompressFile = new File(entityName);
                OutputStream outputStream = null;
                InputStream inputStream = null;
                try
                {
                    outputStream = FileUtils.openOutputStream(uncompressFile);
                    inputStream = this.getInputStream(archiveInputStream, archiveEntry);
                    IOUtils.copy(inputStream, outputStream);
                }
                finally
                {
                    if (outputStream != null)
                        outputStream.close();
                    inputStream = FileUtils.close(inputStream);
                }
                this.compressor.processFile(uncompressFile);
            }
        }
    }

    /**
     * 解开压缩包。
     * @param compressor 打包对象。
     * @param archiveFile 压缩文件。
     * @param destPath 目标路径。
     * @throws IOException 解压异常。
     */
    protected void unpack(Compressor compressor, File archiveFile, File destPath) throws IOException
    {
        this.compressor = compressor;
        ArchiveInputStream archiveInputStream = null;
        try
        {
            InputStream inputStream = this.createInputStream(archiveFile);
            archiveInputStream = (ArchiveInputStream) this.decorateInputStream(inputStream);
            this.unpack(archiveInputStream, destPath);
        }
        finally
        {
            FileUtils.close(archiveInputStream);
            archiveInputStream = null;
            this.close();
        }
    }
}

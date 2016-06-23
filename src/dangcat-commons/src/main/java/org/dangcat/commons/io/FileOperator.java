package org.dangcat.commons.io;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.ValueUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class FileOperator
{
    private static final Logger logger = Logger.getLogger(FileUtils.class);

    protected static File copy(File srcFile, File dstFile)
    {
        File result = null;
        try
        {
            if (srcFile.isDirectory() && dstFile.isDirectory())
                org.apache.commons.io.FileUtils.copyDirectory(srcFile, dstFile);
            else if (srcFile.isFile())
            {
                File tmpFile = null;
                if (dstFile.isDirectory())
                {
                    tmpFile = File.createTempFile("COPY", ".tmp", dstFile);
                    org.apache.commons.io.FileUtils.copyFile(srcFile, tmpFile);
                    result = new File(dstFile.getAbsoluteFile() + File.separator + srcFile.getName());
                }
                else if (dstFile.isFile() || !dstFile.exists())
                {
                    tmpFile = File.createTempFile("COPY", ".tmp", dstFile.getParentFile());
                    org.apache.commons.io.FileUtils.copyFile(srcFile, tmpFile);
                    result = dstFile;
                }
                tmpFile.renameTo(result);
            }
        }
        catch (Exception e)
        {
            logger.error("Move file from " + srcFile.getAbsolutePath() + " to " + dstFile.getAbsolutePath() + " failed! ", e);
            result = null;
        }
        return result;
    }

    protected static boolean copy(File input, OutputStream outputStream)
    {
        boolean result = false;
        try
        {
            org.apache.commons.io.FileUtils.copyFile(input, outputStream);
            result = true;
        }
        catch (IOException e)
        {
            logger.error("copy file from " + input.getAbsolutePath() + " to outputStream failed! ", e);
        }
        return result;
    }

    protected static boolean copy(InputStream inputStream, File output)
    {
        boolean result = false;
        try
        {
            org.apache.commons.io.FileUtils.copyInputStreamToFile(inputStream, output);
            result = true;
        }
        catch (IOException e)
        {
            logger.error("copy inputStream to " + output.getAbsolutePath() + " failed! ", e);
        }
        return result;
    }

    protected static boolean copy(InputStream inputStream, OutputStream outputStream)
    {
        boolean result = false;
        try
        {
            org.apache.commons.io.IOUtils.copy(inputStream, outputStream);
            result = true;
        }
        catch (IOException e)
        {
            logger.error("copy inputStream to inputStream failed! ", e);
        }
        return result;
    }

    protected static boolean move(File srcFile, File dstFile, boolean deleteExists)
    {
        boolean result = false;
        try
        {
            if (srcFile.exists())
            {
                File destFile = dstFile;
                if (dstFile.isDirectory())
                    destFile = new File(dstFile, srcFile.getName());
                if (deleteExists && destFile.isFile() && destFile.exists())
                {
                    if (!destFile.delete())
                        return result;
                }

                if (srcFile.isFile())
                {
                    if (!dstFile.exists())
                        org.apache.commons.io.FileUtils.moveFile(srcFile, destFile);
                    else if (dstFile.isDirectory())
                        org.apache.commons.io.FileUtils.moveToDirectory(srcFile, dstFile, false);
                }
                else if (srcFile.isDirectory())
                {
                    if (!dstFile.exists())
                        org.apache.commons.io.FileUtils.moveDirectory(srcFile, dstFile);
                    else if (dstFile.isDirectory())
                        org.apache.commons.io.FileUtils.moveToDirectory(srcFile, dstFile, false);
                }
                result = true;
            }
        }
        catch (IOException e)
        {
            logger.error("Move file from " + srcFile.getAbsolutePath() + " to " + dstFile.getAbsolutePath() + " failed! ");
        }
        return result;
    }

    protected static File renameFileExtName(File file, String oldExtension, String newExtension)
    {
        String fileName = file.getName();
        File destFile = file;
        if (!ValueUtils.isEmpty(oldExtension))
        {
            if (!oldExtension.startsWith("."))
                oldExtension = "." + oldExtension;
            if (fileName.endsWith(oldExtension))
                fileName = fileName.substring(0, fileName.length() - oldExtension.length());
        }
        if (!ValueUtils.isEmpty(newExtension))
        {
            if (!newExtension.startsWith("."))
                newExtension = "." + newExtension;
            fileName += newExtension;
        }
        destFile = new File(file.getParent(), fileName);
        move(file, destFile, true);
        return destFile;
    }
}

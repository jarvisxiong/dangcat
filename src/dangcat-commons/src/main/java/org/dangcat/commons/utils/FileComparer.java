package org.dangcat.commons.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

import org.apache.log4j.Logger;
import org.dangcat.commons.io.FileUtils;

public class FileComparer
{
    protected static final Logger logger = Logger.getLogger(FileComparer.class);

    public static boolean compare(File srcFile, File dstFile, FileFilter fileFilter)
    {
        boolean result = false;
        if (srcFile.isDirectory() && dstFile.isDirectory())
        {
            File[] files = null;
            if (fileFilter != null)
                files = srcFile.listFiles(fileFilter);
            else
                files = srcFile.listFiles();
            for (final File srcSubFile : files)
            {
                File[] dstSubFiles = dstFile.listFiles(new FilenameFilter()
                {
                    @Override
                    public boolean accept(File dir, String name)
                    {
                        return srcSubFile.getName().equalsIgnoreCase(name);
                    }
                });
                if (dstSubFiles == null || dstSubFiles.length != 1)
                {
                    logger.error(srcSubFile.getAbsolutePath() + " is not same to " + dstSubFiles.length);
                    return false;
                }
                if (!compare(srcSubFile, dstSubFiles[0], fileFilter))
                    return false;
            }
            result = true;
        }
        else if (srcFile.isFile() && dstFile.isFile())
        {
            if (srcFile.getName().equalsIgnoreCase(dstFile.getName()))
            {
                long totalSize1 = FileUtils.getTotalSize(srcFile);
                long totalSize2 = FileUtils.getTotalSize(dstFile);
                if (totalSize1 == totalSize2)
                    result = true;
                else
                    logger.error(srcFile.getAbsolutePath() + " (" + totalSize1 + ") is not same to " + dstFile.getAbsolutePath() + " (" + totalSize2 + ")");
            }
            else
                logger.error(srcFile.getAbsolutePath() + " is not same to " + dstFile.getAbsolutePath());

        }
        return result;
    }

}

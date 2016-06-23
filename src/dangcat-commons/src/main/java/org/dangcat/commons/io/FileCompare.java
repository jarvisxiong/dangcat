package org.dangcat.commons.io;

import org.dangcat.commons.utils.ValueUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件比较器。
 * 
 */
public class FileCompare
{
    public static boolean compare(File srcFile, File destFile)
    {
        return compare(srcFile, destFile, false);
    }

    public static boolean compare(File srcFile, File destFile, boolean deepCompare)
    {
        if (srcFile != null && destFile != null && srcFile.exists() && destFile.exists())
        {
            if (srcFile.isFile() && destFile.isFile())
                return compareFile(srcFile, destFile, deepCompare);
            if (srcFile.isDirectory() && destFile.isDirectory())
                return compareDirectory(srcFile, destFile, deepCompare);
        }
        return false;
    }

    private static boolean compareDirectory(File srcFile, File destFile, boolean deepCompare)
    {
        File[] srcFiles = srcFile.listFiles();
        File[] destFiles = destFile.listFiles();
        if (srcFiles == null && destFiles == null)
            return true;
        if (srcFiles.length == 0 && destFiles.length == 0)
            return true;
        if (srcFiles.length == destFiles.length)
        {
            Map<String, File> fileMap = new HashMap<String, File>();
            for (File file : destFiles)
                fileMap.put(file.getName(), file);
            for (File source : srcFiles)
            {
                File dest = fileMap.get(source.getName());
                if (dest == null)
                    return false;
                if (!compare(source, dest, deepCompare))
                    return false;
            }
            return true;
        }
        return false;
    }

    private static boolean compareFile(File srcFile, File destFile, boolean deepCompare)
    {
        if (!srcFile.getName().equals(destFile.getName()))
            return false;
        if (srcFile.length() != destFile.length())
            return false;
        if (deepCompare)
        {
            try
            {
                byte[] srcBytes = FileUtils.readFileToBytes(srcFile);
                byte[] destBytes = FileUtils.readFileToBytes(destFile);
                return ValueUtils.compare(srcBytes, destBytes) == 0;
            }
            catch (IOException e)
            {
            }
        }
        return true;
    }
}

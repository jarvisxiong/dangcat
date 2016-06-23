package org.dangcat.commons.archive;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;
import org.dangcat.commons.io.FileUtils;

/**
 * 压缩工具。
 * @author dangcat
 * 
 */
public class CompressUtils
{
    public static final String bz = ".bz";
    public static final String bz2 = ".bz2";
    protected static final Map<String, String> compressTypeMap = new HashMap<String, String>();
    public static final String gz = ".gz";
    protected static final Logger logger = Logger.getLogger(CompressUtils.class);
    private static final String TMP_EXTERNSIONNAME = "tmp";
    public static final String xz = ".xz";
    public static final String z = ".z";

    static
    {
        compressTypeMap.put(bz, CompressorStreamFactory.BZIP2);
        compressTypeMap.put(bz2, CompressorStreamFactory.BZIP2);
        compressTypeMap.put(gz, CompressorStreamFactory.GZIP);
        compressTypeMap.put(xz, CompressorStreamFactory.XZ);
        compressTypeMap.put(z, CompressorStreamFactory.GZIP);
    }

    /**
     * 压缩字节数据。
     * @param dataBytes
     * @return 压缩后的数组。
     */
    public static byte[] compress(byte[] dataBytes)
    {
        return compress(gz, dataBytes);
    }

    /**
     * 按照指定的算法压缩字节数组。
     * @param name 算法名。
     * @param dataBytes 字节数组。
     * @return 压缩后的字节。
     */
    public static byte[] compress(String name, byte[] dataBytes)
    {
        byte[] content = null;
        if (dataBytes != null && dataBytes.length > 0)
        {
            try
            {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                InputStream inputStream = new ByteArrayInputStream(dataBytes);
                compress(name, inputStream, byteArrayOutputStream);
                content = byteArrayOutputStream.toByteArray();
            }
            catch (IOException e)
            {
                logger.error(name, e);
            }
        }
        return content;
    }

    /**
     * 压缩单个文件。
     * @param name 目标扩展名。
     * @param sourceFile 来源文件。
     * @throws IOException 运行异常。
     */
    public static File compress(String name, File sourceFile) throws IOException
    {
        File destFile = new File(sourceFile.getAbsolutePath() + name);
        if (name != null && !sourceFile.getAbsolutePath().toLowerCase().endsWith(name))
            compress(name, sourceFile, destFile);
        return destFile.exists() ? destFile : null;
    }

    /**
     * 压缩文件。
     * @param name 压缩名称。
     * @param sourceFile 来源文件。
     * @param destFile 目标文件。
     * @throws IOException 运行异常。
     */
    public static void compress(String name, File sourceFile, File destFile) throws IOException
    {
        if (sourceFile.exists())
        {
            if (sourceFile.isFile())
                compressFile(name, sourceFile, destFile);
            else if (sourceFile.isDirectory())
                compressDir(name, sourceFile, destFile);
        }
    }

    /**
     * 按照指定的算法压缩数据流。
     * @param name 算法名。
     * @param inputStream 输入数据流。
     * @param outputStream 输出数据流。
     * @throws IOException 运行异常。
     */
    public static void compress(String name, InputStream inputStream, OutputStream outputStream) throws IOException
    {
        CompressorOutputStream compressorOutputStream = null;
        String compressType = compressTypeMap.get(name);
        if (compressType == null)
            throw new IOException("The compress type " + name + " is invalid");
        try
        {
            compressorOutputStream = new CompressorStreamFactory().createCompressorOutputStream(compressType, outputStream);
            IOUtils.copy(inputStream, compressorOutputStream);
        }
        catch (CompressorException e)
        {
            throw new IOException(e);
        }
        finally
        {
            if (compressorOutputStream != null)
                compressorOutputStream.close();
        }
    }

    private static void compressDir(String name, File sourceFile, File destFile) throws IOException
    {
        if (sourceFile.isDirectory())
        {
            Compressor compressor = new Compressor();
            for (File childFile : sourceFile.listFiles())
                compressor.addArchiveEntry(childFile);
            compressor.compress(destFile);
        }
    }

    private static void compressFile(String name, File sourceFile, File destFile) throws IOException
    {
        String extensionName = FileUtils.getExtension(destFile.getName());
        String absolutePath = destFile.getAbsolutePath();
        File tmpFile = new File(absolutePath.substring(0, absolutePath.length() - extensionName.length()) + TMP_EXTERNSIONNAME);

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(sourceFile));
            outputStream = new BufferedOutputStream(new FileOutputStream(tmpFile));
            compress(name, inputStream, outputStream);
        }
        finally
        {
            inputStream = FileUtils.close(inputStream);
            if (outputStream != null)
            {
                outputStream.close();
                FileUtils.renameFileExtName(tmpFile, TMP_EXTERNSIONNAME, extensionName);
            }
        }
    }

    /**
     * 解压缩字节数据。
     * @param dataBytes
     * @return 解压缩后的数组。
     */
    public static byte[] decompress(byte[] dataBytes)
    {
        return decompress(gz, dataBytes);
    }

    /**
     * 解压缩单个文件。
     * @param sourceFile 来源文件。
     * @throws IOException 运行异常。
     */
    public static File decompress(File sourceFile) throws IOException
    {
        return decompress(sourceFile, null);
    }

    public static File decompress(File sourceFile, File destFile) throws IOException
    {
        boolean found = false;
        for (String key : compressTypeMap.keySet())
        {
            if (sourceFile.getName().toLowerCase().endsWith(key))
            {
                if (destFile == null)
                    destFile = new File(sourceFile.getAbsolutePath().replace(key, ""));
                decompressFile(key, sourceFile, destFile);
                found = true;
                break;
            }
        }
        if (!found)
        {
            Compressor compressor = new Compressor();
            compressor.decompress(sourceFile, destFile);
        }
        return destFile;
    }

    /**
     * 按照指定的算法解压缩字节数组。
     * @param name 算法名。
     * @param dataBytes 字节数组。
     * @return 压缩后的字节。
     */
    public static byte[] decompress(String name, byte[] dataBytes)
    {
        byte[] content = null;
        if (dataBytes != null && dataBytes.length > 0)
        {
            try
            {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                InputStream inputStream = new ByteArrayInputStream(dataBytes);
                decompress(name, inputStream, byteArrayOutputStream);
                content = byteArrayOutputStream.toByteArray();
            }
            catch (IOException e)
            {
                logger.error(name, e);
            }
        }
        return content;
    }

    /**
     * 按照指定的算法解压缩数据流。
     * @param name 算法名。
     * @param inputStream 输入数据流。
     * @param outputStream 输出数据流。
     * @throws IOException 运行异常。
     */
    public static void decompress(String name, InputStream inputStream, OutputStream outputStream) throws IOException
    {
        CompressorInputStream compressorInputStream = null;
        String compressType = compressTypeMap.get(name);
        if (compressType == null)
            throw new IOException("The compress type " + name + " is invalid");

        try
        {
            compressorInputStream = new CompressorStreamFactory().createCompressorInputStream(compressType, inputStream);
            IOUtils.copy(compressorInputStream, outputStream);
        }
        catch (CompressorException e)
        {
            throw new IOException(e);
        }
        finally
        {
            FileUtils.close(compressorInputStream);
        }
    }

    /**
     * 解压缩文件。
     * @param name 压缩名称。
     * @param sourceFile 来源文件。
     * @param destFile 目标文件。
     * @throws IOException 运行异常。
     */
    protected static void decompressFile(String name, File sourceFile, File destFile) throws IOException
    {
        if (sourceFile.exists() && sourceFile.isFile())
        {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try
            {
                inputStream = new BufferedInputStream(new FileInputStream(sourceFile));
                outputStream = new BufferedOutputStream(new FileOutputStream(destFile));
                decompress(name, inputStream, outputStream);
            }
            finally
            {
                inputStream = FileUtils.close(inputStream);
                if (outputStream != null)
                    outputStream.close();
            }
        }
    }
}
package org.dangcat.commons.io;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.ValueUtils;

import java.io.*;
import java.net.URLDecoder;

/**
 * 文件工具类
 */
public class FileUtils {
    public static final String ENCODING_UTF_8 = "UTF-8";
    private static final Logger logger = Logger.getLogger(FileUtils.class);

    /**
     * 关闭输入流。
     *
     * @param inputStream 输入流。
     * @return 关闭后的结果。
     */
    public static InputStream close(InputStream inputStream) {
        return FileCloser.close(inputStream);
    }

    /**
     * 关闭输出流。
     *
     * @param outputStream 输出流。
     * @return 关闭后的结果。
     */
    public static OutputStream close(OutputStream outputStream) {
        return FileCloser.close(outputStream);
    }

    public static Reader close(Reader reader) {
        return FileCloser.close(reader);
    }

    public static Writer close(Writer writer) {
        return FileCloser.close(writer);
    }

    /**
     * 把文件或者拷贝至目标文件
     *
     * @param srcFile 来源文件或目录。
     * @param dstFile 目标文件或目录。
     * @return
     */
    public static File copy(File srcFile, File dstFile) {
        return FileOperator.copy(srcFile, dstFile);
    }

    public static boolean copy(File input, OutputStream outputStream) {
        return FileOperator.copy(input, outputStream);
    }

    public static boolean copy(InputStream inputStream, File output) {
        return FileOperator.copy(inputStream, output);
    }

    public static boolean copy(InputStream inputStream, OutputStream outputStream) {
        return FileOperator.copy(inputStream, outputStream);
    }

    /**
     * 对路径进行编码转换。
     *
     * @param path 路径。
     * @return 编码后的路径。
     */
    public static String decodePath(String path) {
        String directory = null;
        try {
            directory = URLDecoder.decode(path, ENCODING_UTF_8);
        } catch (UnsupportedEncodingException e) {
            logger.error(path, e);
        }
        return directory;
    }

    /**
     * 强制删除指定文件或目录。
     *
     * @param file 指定文件或目录。
     */
    public static void delete(File file) {
        try {
            if (file.exists())
                org.apache.commons.io.FileUtils.forceDelete(file);
        } catch (IOException e) {
            logger.error("delete " + file.getAbsolutePath() + " failed! ", e);
        }
    }

    /**
     * 清除指定路径下指定扩展名的文件。
     *
     * @param path    路径。
     * @param postFix 后缀名。
     */
    public static void deleteFiles(String path, final String postFix) {
        File filePath = new File(path);
        File[] fileArray = filePath.listFiles((FilenameFilter) new FileNameFilter(postFix));
        if (fileArray != null) {
            for (File file : fileArray) {
                if (logger.isDebugEnabled())
                    logger.info("Delete the file " + file.getAbsolutePath());
                file.delete();
            }
        }
    }

    /**
     * 读取规范路径。
     *
     * @param path 路径。
     * @return 规范标准路径。
     */
    public static String getCanonicalPath(String path) {
        File filePath = new File(path);
        if (filePath.exists()) {
            try {
                return filePath.getCanonicalPath();
            } catch (IOException e) {
            }
        }
        return path;
    }

    /**
     * 读取文件的扩展名。
     *
     * @param fileName 文件名。
     * @return 扩展名。
     */
    public static String getExtension(String fileName) {
        String extension = null;
        if (!ValueUtils.isEmpty(fileName)) {
            int index = fileName.lastIndexOf('.');
            if (index > -1 && index < fileName.length() - 1)
                extension = fileName.substring(index + 1).trim();
        }
        return extension;
    }

    /**
     * 得到指定类型的资源路径，并转换成文件对象。
     *
     * @param classType 类型。
     * @param path      路径。
     * @return 目录对象。
     */
    public static String getResourcePath(Class<?> classType, String path) {
        String directory = classType.getProtectionDomain().getCodeSource().getLocation().getFile();
        if (!ValueUtils.isEmpty(path))
            directory += File.separator + path;
        String reousrcePath = decodePath(directory);
        if (reousrcePath.startsWith("file:/") && reousrcePath.endsWith("!/"))
            reousrcePath = reousrcePath.substring(6, reousrcePath.length() - 2);
        return reousrcePath;
    }

    /**
     * 得到文件或者目录的大小。
     *
     * @param file 文件或者目录。
     * @return 占用空间大小（byte）。
     */
    public static long getTotalSize(File file) {
        long totalSize = 0;
        if (file != null && file.exists()) {
            if (file.isDirectory())
                totalSize = org.apache.commons.io.FileUtils.sizeOfDirectory(file);
            else if (file.isFile())
                totalSize = org.apache.commons.io.FileUtils.sizeOf(file);
        }
        return totalSize;
    }

    public static boolean isEmptyDirectory(File dir) {
        boolean result = true;
        if (dir.exists() && dir.isDirectory()) {
            String[] files = dir.list();
            result = files == null || files.length == 0;
        }
        return result;
    }

    /**
     * 建立指定路径。
     *
     * @param path 路径。
     * @return 是否成功。
     */
    public static boolean mkdir(String path) {
        File directory = new File(path);
        try {
            if (!directory.exists())
                org.apache.commons.io.FileUtils.forceMkdir(directory);
        } catch (Exception e) {
            logger.error("mkdir " + path + " failed! ", e);
        }
        return directory.exists() && directory.isDirectory();
    }

    /**
     * 移动文件。
     *
     * @param srcFile      来源文件。
     * @param dstFile      目标文件。
     * @param deleteExists 如果目标文件存在则先删除。
     * @return 是否成功。
     * @throws IOException
     */
    public static boolean move(File srcFile, File dstFile, boolean deleteExists) {
        return FileOperator.move(srcFile, dstFile, deleteExists);
    }

    /**
     * 打开文件输出流，如果文件路径不存在则自动建立。
     *
     * @param file 文件对象。
     * @return
     * @throws IOException
     */
    public static FileOutputStream openOutputStream(File file) throws IOException {
        return org.apache.commons.io.FileUtils.openOutputStream(file);
    }

    /**
     * 读取文件的内容。
     *
     * @param file 文件对象。
     * @return 文件内容。
     * @throws IOException
     */
    public static byte[] readFileToBytes(File file) throws IOException {
        return org.apache.commons.io.FileUtils.readFileToByteArray(file);
    }

    /**
     * 读取文件的内容。
     *
     * @param file 文件对象。
     * @return 文件内容。
     * @throws IOException
     */
    public static String readFileToString(File file, String encoding) throws IOException {
        return org.apache.commons.io.FileUtils.readFileToString(file, encoding);
    }

    /**
     * 将文件的扩展名。
     *
     * @param oldExtension 旧的文件名。
     * @param newExtension 新的文件扩展名。
     * @return 修改后文件。
     */
    public static File renameFileExtName(File file, String oldExtension, String newExtension) {
        return FileOperator.renameFileExtName(file, oldExtension, newExtension);
    }
}

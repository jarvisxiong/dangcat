package org.dangcat.commons.io;

import java.io.*;

/**
 * 文件工具类
 */
class FileCloser
{
    /**
     * 关闭输入流。
     * @param inputStream 输入流。
     * @return 关闭后的结果。
     */
    protected static InputStream close(InputStream inputStream)
    {
        if (inputStream != null)
        {
            try
            {
                inputStream.close();
                inputStream = null;
            }
            catch (IOException e)
            {
            }
        }
        return inputStream;
    }

    /**
     * 关闭输出流。
     * @param outputStream 输出流。
     * @return 关闭后的结果。
     */
    protected static OutputStream close(OutputStream outputStream)
    {
        if (outputStream != null)
        {
            try
            {
                outputStream.close();
                outputStream = null;
            }
            catch (IOException e)
            {
            }
        }
        return outputStream;
    }

    protected static Reader close(Reader reader)
    {
        if (reader != null)
        {
            try
            {
                reader.close();
                reader = null;
            }
            catch (IOException e)
            {
            }
        }
        return reader;
    }

    protected static Writer close(Writer writer)
    {
        if (writer != null)
        {
            try
            {
                writer.close();
                writer = null;
            }
            catch (IOException e)
            {
            }
        }
        return writer;
    }

}

package org.dangcat.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.persistence.DataReader;
import org.dangcat.persistence.DataWriter;

/**
 * 文档输入输出。
 * @author dangcat
 * 
 */
public abstract class DocumentBase
{
    private InputStream inputStream = null;
    protected int lineCount = 0;
    protected final Logger logger = Logger.getLogger(this.getClass());
    private OutputStream outputStream = null;

    public void close()
    {
        this.lineCount = 0;
        try
        {
            if (this.outputStream != null)
            {
                this.outputStream.close();
                this.outputStream = null;
            }
        }
        catch (IOException e)
        {
        }
        this.inputStream = FileUtils.close(this.inputStream);
    }

    public InputStream getInputStream()
    {
        return this.inputStream;
    }

    public int getLineCount()
    {
        return this.lineCount;
    }

    public OutputStream getOutputStream()
    {
        return this.outputStream;
    }

    /**
     * 从文件加载数据。
     * @param dataWriter 数据输出接口。
     * @return 读入的行数。
     */
    public abstract int read(DataWriter dataWriter);

    /**
     * 从文件加载数据。
     * @param file 目标文件。
     * @param dataWriter 数据输出接口。
     * @return 读入的行数。
     */
    public int read(File file, DataWriter dataWriter)
    {
        int result = 0;
        try
        {
            this.inputStream = new FileInputStream(file);
            result = this.read(dataWriter);
        }
        catch (FileNotFoundException e)
        {
            this.logger.error(file, e);
        }
        finally
        {
            this.close();
        }
        return result;
    }

    public void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    public void setOutputStream(OutputStream outputStream)
    {
        this.outputStream = outputStream;
    }

    /**
     * 导出实体对象数据到数据流。
     * @param outputStream 输出流。
     * @param dataReader 数据来源。
     * @param 输出数量。
     */
    public abstract int write(DataReader dataReader);

    /**
     * 导出实体对象数据到数据流。
     * @param file 输出文件。
     * @param dataReader 来源数据。
     * @param exportHeader 是否输出头。
     */
    public int write(File file, DataReader dataReader)
    {
        int result = 0;
        try
        {
            this.outputStream = new FileOutputStream(file);
            result = this.write(dataReader);
        }
        catch (FileNotFoundException e)
        {
            this.logger.error(file, e);
        }
        finally
        {
            this.close();
        }
        return result;
    }
}

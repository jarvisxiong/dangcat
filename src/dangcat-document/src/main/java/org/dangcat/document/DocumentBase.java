package org.dangcat.document;

import org.apache.log4j.Logger;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.persistence.DataReader;
import org.dangcat.persistence.DataWriter;

import java.io.*;

/**
 * 文档输入输出。
 *
 * @author dangcat
 */
public abstract class DocumentBase {
    protected final Logger logger = Logger.getLogger(this.getClass());
    protected int lineCount = 0;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    public void close() {
        this.lineCount = 0;
        try {
            if (this.outputStream != null) {
                this.outputStream.close();
                this.outputStream = null;
            }
        } catch (IOException e) {
        }
        this.inputStream = FileUtils.close(this.inputStream);
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public int getLineCount() {
        return this.lineCount;
    }

    public OutputStream getOutputStream() {
        return this.outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * 从文件加载数据。
     *
     * @param dataWriter 数据输出接口。
     * @return 读入的行数。
     */
    public abstract int read(DataWriter dataWriter);

    /**
     * 从文件加载数据。
     *
     * @param file       目标文件。
     * @param dataWriter 数据输出接口。
     * @return 读入的行数。
     */
    public int read(File file, DataWriter dataWriter) {
        int result = 0;
        try {
            this.inputStream = new FileInputStream(file);
            result = this.read(dataWriter);
        } catch (FileNotFoundException e) {
            this.logger.error(file, e);
        } finally {
            this.close();
        }
        return result;
    }

    /**
     * 导出实体对象数据到数据流。
     *
     * @param outputStream 输出流。
     * @param dataReader   数据来源。
     * @param 输出数量。
     */
    public abstract int write(DataReader dataReader);

    /**
     * 导出实体对象数据到数据流。
     *
     * @param file         输出文件。
     * @param dataReader   来源数据。
     * @param exportHeader 是否输出头。
     */
    public int write(File file, DataReader dataReader) {
        int result = 0;
        try {
            this.outputStream = new FileOutputStream(file);
            result = this.write(dataReader);
        } catch (FileNotFoundException e) {
            this.logger.error(file, e);
        } finally {
            this.close();
        }
        return result;
    }
}

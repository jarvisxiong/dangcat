package org.dangcat.document.txt;

import org.dangcat.document.DocumentBase;
import org.dangcat.persistence.DataWriter;

import java.io.*;

/**
 * 文档输入输出。
 *
 * @author dangcat
 */
public abstract class TextDocumentBase extends DocumentBase {
    private boolean firstHeader = true;
    private PrintWriter printWriter = null;

    @Override
    public void close() {
        if (this.printWriter != null) {
            this.printWriter.close();
            this.printWriter = null;
        }
        super.close();
    }

    protected PrintWriter getPrintWriter() {
        if (this.printWriter == null && this.getOutputStream() != null)
            this.printWriter = new PrintWriter(this.getOutputStream());
        return this.printWriter;
    }

    public boolean isFirstHeader() {
        return this.firstHeader;
    }

    public void setFirstHeader(boolean firstHeader) {
        this.firstHeader = firstHeader;
    }

    /**
     * 从文件加载数据。
     *
     * @param dataWriter 数据输出接口。
     * @return 读入的行数。
     */
    @Override
    public int read(DataWriter dataWriter) {
        return 0;
    }

    /**
     * 从文件加载数据。
     *
     * @param file       目标文件。
     * @param dataWriter 数据输出接口。
     * @return 读入的行数。
     */
    @Override
    public int read(File file, DataWriter dataWriter) {
        BufferedReader bufferedReader = null;
        int result = 0;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            result = this.read(bufferedReader, dataWriter);
        } catch (Exception e) {
            this.logger.error(file, e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }

    /**
     * 从缓冲流加载数据。
     *
     * @param bufferedReader 数据缓冲流。
     * @param dataWriter     数据输出接口。
     * @return 读入的行数。
     * @throws IOException 异常。
     */
    public abstract int read(Reader reader, DataWriter dataWriter) throws IOException;
}

package org.dangcat.document.txt;

import org.dangcat.persistence.DataWriter;
import org.dangcat.persistence.entity.EntityDataReader;
import org.dangcat.persistence.entity.EntityDataWriter;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.model.TableDataReader;
import org.dangcat.persistence.model.TableDataWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Text工具类。
 * @author dangcat
 * 
 */
public class TextUtils
{
    /**
     * 从缓冲流加载数据。
     * @param reader 数据缓冲流。
     * @param classType 实体类型。
     * @return 读入的行数。
     * @throws IOException 异常。TextDocument
     * @throws EntityException
     */
    public static <T> List<T> read(BufferedReader reader, Class<T> classType) throws IOException, EntityException
    {
        List<T> entityList = new ArrayList<T>();
        DataWriter dataWriter = new EntityDataWriter<T>(entityList, classType);

        TextDocument textDocument = new TextDocument();
        textDocument.read(reader, dataWriter);
        return entityList;
    }

    /**
     * 从缓冲流加载数据。
     * @param reader 数据缓冲流。
     * @param table 表对象。
     * @return 读入的行数。
     * @throws IOException 异常。
     */
    public static int read(BufferedReader reader, Table table) throws IOException
    {
        TextDocument textDocument = new TextDocument();
        return textDocument.read(reader, new TableDataWriter(table));
    }

    /**
     * 从文件加载数据。
     * @param file 目标文件。
     * @param classType 实体类型。
     * @return 读入的行数。
     * @throws IOException 异常。
     */
    public static <T> List<T> read(File file, Class<T> classType)
    {
        List<T> entityList = new ArrayList<T>();
        DataWriter dataWriter = new EntityDataWriter<T>(entityList, classType);

        TextDocument textDocument = new TextDocument();
        textDocument.read(file, dataWriter);
        return entityList;
    }

    /**
     * 从文件加载数据。
     * @param file 目标文件。
     * @param table 表对象。
     * @return 读入的行数。
     * @throws IOException 异常。
     */
    public static int read(File file, Table table)
    {
        TextDocument textDocument = new TextDocument();
        return textDocument.read(file, new TableDataWriter(table));
    }

    /**
     * 导出实体对象数据到数据流。
     * @param outputStream 输出流。
     * @param table 表对象。
     */
    public static <T> void write(File file, List<T> entityList)
    {
        TextDocument textDocument = new TextDocument();
        textDocument.write(file, new EntityDataReader<T>(entityList));
    }

    /**
     * 导出Table对象数据到数据流。
     * @param outputStream 输出流。
     * @param table 表对象。
     */
    public static void write(File file, Table table)
    {
        TextDocument textDocument = new TextDocument();
        textDocument.write(file, new TableDataReader(table));
    }

    /**
     * 导出实体对象数据到数据流。
     * @param outputStream 输出流。
     * @param table 表对象。
     */
    public static <T> void write(OutputStream outputStream, List<T> entityList)
    {
        TextDocument textDocument = new TextDocument();
        textDocument.setOutputStream(outputStream);
        textDocument.write(new EntityDataReader<T>(entityList));
    }

    /**
     * 导出Table对象数据到数据流。
     * @param outputStream 输出流。
     * @param table 表对象。
     */
    public static void write(OutputStream outputStream, Table table)
    {
        TextDocument textDocument = new TextDocument();
        textDocument.setOutputStream(outputStream);
        textDocument.write(new TableDataReader(table));
    }
}

package org.dangcat.document.excel;

import org.dangcat.persistence.DataWriter;
import org.dangcat.persistence.entity.EntityDataReader;
import org.dangcat.persistence.entity.EntityDataWriter;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.model.TableDataReader;
import org.dangcat.persistence.model.TableDataWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel工具类。
 * @author dangcat
 * 
 */
public class ExcelUtils
{
    /**
     * 从文件加载数据。
     * @param file 目标文件。
     * @param classType 实体类型。
     * @param sheetIndex 标签位置。
     * @return 读入的行数。
     * @throws IOException 异常。
     */
    public static <T> List<T> read(File file, Class<T> classType, int sheetIndex) throws IOException
    {
        List<T> entityList = new ArrayList<T>();
        DataWriter dataWriter = new EntityDataWriter<T>(entityList, classType);

        ExcelDocumentReader excelDocumentReader = new ExcelDocumentReader();
        excelDocumentReader.read(file);
        excelDocumentReader.setDataWriter(dataWriter);
        excelDocumentReader.readSheet(sheetIndex);
        return entityList;
    }

    /**
     * 从文件加载数据。
     * @param file 目标文件。
     * @param table 表对象。
     * @param sheetIndex 标签位置。
     * @return 读入的行数。
     * @throws IOException 异常。
     */
    public static int read(File file, Table table, int sheetIndex) throws IOException
    {
        ExcelDocumentReader excelDocumentReader = new ExcelDocumentReader();
        excelDocumentReader.read(file);
        excelDocumentReader.setDataWriter(new TableDataWriter(table));
        excelDocumentReader.readSheet(sheetIndex);
        return table.getRows().size();
    }

    /**
     * 从缓冲流加载数据。
     * @param inputStream 数据缓冲流。
     * @param classType 实体类型。
     * @param sheetIndex 标签位置。
     * @return 读入的行数。
     * @throws IOException 异常。
     * @throws EntityException
     */
    public static <T> List<T> read(InputStream inputStream, Class<T> classType, int sheetIndex) throws IOException, EntityException
    {
        List<T> entityList = new ArrayList<T>();
        DataWriter dataWriter = new EntityDataWriter<T>(entityList, classType);

        ExcelDocumentReader excelDocumentReader = new ExcelDocumentReader();
        excelDocumentReader.read(inputStream);
        excelDocumentReader.setDataWriter(dataWriter);
        excelDocumentReader.readSheet(sheetIndex);
        return entityList;
    }

    /**
     * 从缓冲流加载数据。
     * @param inputStream 数据缓冲流。
     * @param table 表对象。
     * @param sheetIndex 标签位置。
     * @return 读入的行数。
     * @throws IOException 异常。
     */
    public static int read(InputStream inputStream, Table table, int sheetIndex) throws IOException
    {
        ExcelDocumentReader excelDocumentReader = new ExcelDocumentReader();
        excelDocumentReader.read(inputStream);
        excelDocumentReader.setDataWriter(new TableDataWriter(table));
        excelDocumentReader.readSheet(sheetIndex);
        return table.getRows().size();
    }

    /**
     * 导出实体对象数据到数据流。
     * @param file 输出流。
     * @param table 表对象。
     * @param sheetTitle 标签标题。
     * @throws IOException
     */
    public static <T> void write(File file, List<T> entityList, String sheetTitle) throws IOException
    {
        ExcelDocumentWriter excelDocumentWriter = new ExcelDocumentWriter();
        excelDocumentWriter.setSheetTitle(sheetTitle);
        excelDocumentWriter.setDataReader(new EntityDataReader<T>(entityList));
        excelDocumentWriter.write(file);
    }

    /**
     * 导出Table对象数据到数据流。
     * @param outputStream 输出流。
     * @param table 表对象。
     * @param sheetTitle 标签标题。
     * @throws IOException
     */
    public static void write(File file, Table table, String sheetTitle) throws IOException
    {
        ExcelDocumentWriter excelDocumentWriter = new ExcelDocumentWriter();
        excelDocumentWriter.setSheetTitle(sheetTitle);
        excelDocumentWriter.setDataReader(new TableDataReader(table));
        excelDocumentWriter.write(file);
    }

    /**
     * 导出Table对象数据到数据流。
     * @param printStream 输出流。
     * @param table 表对象。
     * @param sheetIndex 标签位置。
     * @throws IOException
     */
    public static <T> void write(OutputStream outputStream, List<T> entityList, String sheetTitle) throws IOException
    {
        ExcelDocumentWriter excelDocumentWriter = new ExcelDocumentWriter();
        excelDocumentWriter.setSheetTitle(sheetTitle);
        excelDocumentWriter.setDataReader(new EntityDataReader<T>(entityList));
        excelDocumentWriter.write(outputStream);
    }

    /**
     * 导出Table对象数据到数据流。
     * @param printStream 输出流。
     * @param table 表对象。
     * @param sheetIndex 标签位置。
     * @param fieldText 字符标识。
     * @throws IOException
     */
    public static void write(OutputStream outputStream, Table table, String sheetTitle) throws IOException
    {
        ExcelDocumentWriter excelDocumentWriter = new ExcelDocumentWriter();
        excelDocumentWriter.setSheetTitle(sheetTitle);
        excelDocumentWriter.setDataReader(new TableDataReader(table));
        excelDocumentWriter.write(outputStream);
    }
}

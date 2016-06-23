package org.dangcat.document.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.dangcat.persistence.DataWriter;
import org.dangcat.persistence.entity.EntityDataReader;
import org.dangcat.persistence.entity.EntityDataWriter;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Columns;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.model.TableDataReader;
import org.dangcat.persistence.model.TableDataWriter;

/**
 * Csv工具类。
 * @author dangcat
 * 
 */
public class CsvUtils
{
    /**
     * 从缓冲流加载数据。
     * @param reader 数据缓冲流。
     * @param classType 实体类型。
     * @param firstHeader 是否第一行为表头。
     * @return 读入的行数。
     * @throws IOException 异常。
     * @throws EntityException
     */
    public static <T> List<T> read(BufferedReader reader, Class<T> classType, boolean firstHeader) throws IOException, EntityException
    {
        List<T> entityList = new ArrayList<T>();
        DataWriter dataWriter = new EntityDataWriter<T>(entityList, classType);

        CsvDocument csvDocument = new CsvDocument();
        csvDocument.setFirstHeader(firstHeader);
        csvDocument.read(reader, dataWriter);
        return entityList;
    }

    /**
     * 从缓冲流加载数据。
     * @param reader 数据缓冲流。
     * @param table 表对象。
     * @param firstHeader 是否第一行为表头。
     * @return 读入的行数。
     * @throws IOException 异常。
     */
    public static int read(BufferedReader reader, Table table, boolean firstHeader) throws IOException
    {
        CsvDocument csvDocument = new CsvDocument();
        csvDocument.setFirstHeader(firstHeader);
        return csvDocument.read(reader, new TableDataWriter(table));
    }

    /**
     * 从文件加载数据。
     * @param file 目标文件。
     * @param classType 实体类型。
     * @param firstHeader 是否第一行为表头。
     * @return 读入的行数。
     * @throws IOException 异常。
     */
    public static <T> List<T> read(File file, Class<T> classType, boolean firstHeader)
    {
        List<T> entityList = new ArrayList<T>();
        DataWriter dataWriter = new EntityDataWriter<T>(entityList, classType);

        CsvDocument csvDocument = new CsvDocument();
        csvDocument.setFirstHeader(firstHeader);
        csvDocument.read(file, dataWriter);
        return entityList;
    }

    /**
     * 从文件加载数据。
     * @param file 目标文件。
     * @param table 表对象。
     * @param firstHeader 是否第一行为表头。
     * @return 读入的行数。
     * @throws IOException 异常。
     */
    public static int read(File file, Table table, boolean firstHeader)
    {
        CsvDocument csvDocument = new CsvDocument();
        csvDocument.setFirstHeader(firstHeader);
        return csvDocument.read(file, new TableDataWriter(table));
    }

    /**
     * 把实体对象输出成一行文本。
     * @param entity 实体对象。
     * @param fieldNames 字段名数组。
     * @return 格式化后内容。
     */
    public static String toCSV(Object entity, String... fieldNames)
    {
        return toText(entity, null, fieldNames);
    }

    /**
     * 把实体对象输出成一行文本。
     * @param entity 实体对象。
     * @param fieldText 文本字段。
     * @param fieldNames 字段名数组。
     * @return 格式化后内容。
     */
    public static String toText(Object entity, WriteUserSettings userSettings, String... fieldNames)
    {
        String text = null;
        if (entity != null && fieldNames != null && fieldNames.length > 0)
        {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
            Columns columns = entityMetaData.getTable().getColumns();
            try
            {
                StringWriter writer = new StringWriter();
                CsvWriter csvWriter = new CsvWriter(writer, Letters.COMMA);
                if (userSettings != null)
                    csvWriter.setUserSettings(userSettings);
                for (String fieldName : fieldNames)
                {
                    Column column = columns.find(fieldName);
                    if (column != null)
                    {
                        Object value = entityMetaData.getValue(fieldName, entity);
                        csvWriter.write(column.toString(value));
                    }
                }
                if (writer.getBuffer().length() > 0)
                {
                    csvWriter.endRecord();
                    csvWriter.close();
                    text = writer.toString();
                }
            }
            catch (IOException e)
            {

            }
        }
        return text;
    }

    /**
     * 导出实体对象数据到数据流。
     * @param outputStream 输出流。
     * @param table 表对象。
     * @param firstHeader 是否输出头。
     */
    public static <T> void write(File file, List<T> entityList, boolean firstHeader)
    {
        CsvDocument csvDocument = new CsvDocument();
        csvDocument.setFirstHeader(firstHeader);
        csvDocument.write(file, new EntityDataReader<T>(entityList));
    }

    /**
     * 导出实体对象数据到数据流。
     * @param outputStream 输出流。
     * @param table 表对象。
     * @param firstHeader 是否输出头。
     */
    public static <T> void write(File file, List<T> entityList, boolean firstHeader, WriteUserSettings writeuserSettings)
    {
        CsvDocument csvDocument = new CsvDocument();
        csvDocument.setFirstHeader(firstHeader);
        csvDocument.setWriteUserSettings(writeuserSettings);
        csvDocument.write(file, new EntityDataReader<T>(entityList));
    }

    /**
     * 导出Table对象数据到数据流。
     * @param outputStream 输出流。
     * @param table 表对象。
     * @param firstHeader 是否输出头。
     */
    public static void write(File file, Table table, boolean firstHeader)
    {
        CsvDocument csvDocument = new CsvDocument();
        csvDocument.setFirstHeader(firstHeader);
        csvDocument.write(file, new TableDataReader(table));
    }

    /**
     * 导出Table对象数据到数据流。
     * @param outputStream 输出流。
     * @param table 表对象。
     * @param firstHeader 是否输出头。
     */
    public static void write(File file, Table table, boolean firstHeader, WriteUserSettings writeuserSettings)
    {
        CsvDocument csvDocument = new CsvDocument();
        csvDocument.setFirstHeader(firstHeader);
        csvDocument.setWriteUserSettings(writeuserSettings);
        csvDocument.write(file, new TableDataReader(table));
    }

    /**
     * 导出实体对象数据到数据流。
     * @param printStream 输出流。
     * @param table 表对象。
     * @param firstHeader 是否输出头。
     */
    public static <T> void write(PrintStream printStream, List<T> entityList, boolean firstHeader)
    {
        CsvDocument csvDocument = new CsvDocument();
        csvDocument.setFirstHeader(firstHeader);
        csvDocument.setOutputStream(printStream);
        csvDocument.write(new EntityDataReader<T>(entityList));
    }

    /**
     * 导出实体对象数据到数据流。
     * @param printStream 输出流。
     * @param table 表对象。
     * @param firstHeader 是否输出头。
     */
    public static <T> void write(PrintStream printStream, List<T> entityList, boolean firstHeader, WriteUserSettings writeuserSettings)
    {
        CsvDocument csvDocument = new CsvDocument();
        csvDocument.setFirstHeader(firstHeader);
        csvDocument.setWriteUserSettings(writeuserSettings);
        csvDocument.setOutputStream(printStream);
        csvDocument.write(new EntityDataReader<T>(entityList));
    }

    /**
     * 导出Table对象数据到数据流。
     * @param printStream 输出流。
     * @param table 表对象。
     * @param firstHeader 是否输出头。
     */
    public static void write(PrintStream printStream, Table table, boolean firstHeader)
    {
        CsvDocument csvDocument = new CsvDocument();
        csvDocument.setFirstHeader(firstHeader);
        csvDocument.setOutputStream(printStream);
        csvDocument.write(new TableDataReader(table));
    }

    /**
     * 导出Table对象数据到数据流。
     * @param printStream 输出流。
     * @param table 表对象。
     * @param firstHeader 是否输出头。
     * @param fieldText 字符标识。
     */
    public static void write(PrintStream printStream, Table table, boolean firstHeader, WriteUserSettings writeuserSettings)
    {
        CsvDocument csvDocument = new CsvDocument();
        csvDocument.setFirstHeader(firstHeader);
        csvDocument.setWriteUserSettings(writeuserSettings);
        csvDocument.setOutputStream(printStream);
        csvDocument.write(new TableDataReader(table));
    }
}

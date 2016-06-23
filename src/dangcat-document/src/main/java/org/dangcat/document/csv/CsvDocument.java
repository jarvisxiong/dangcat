package org.dangcat.document.csv;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.document.txt.TextDocumentBase;
import org.dangcat.persistence.DataReader;
import org.dangcat.persistence.DataWriter;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Columns;

/**
 * CSV文档输入输出。
 * @author dangcat
 * 
 */
public class CsvDocument extends TextDocumentBase
{
    private ReadUserSettings readUserSettings = null;
    private WriteUserSettings writeUserSettings = null;

    public ReadUserSettings getReadUserSettings()
    {
        return this.readUserSettings;
    }

    public WriteUserSettings getWriteUserSettings()
    {
        return this.writeUserSettings;
    }

    /**
     * 从缓冲流加载数据。
     * @param bufferedReader 数据缓冲流。
     * @param dataWriter 数据输出接口。
     * @return 读入的行数。
     * @throws IOException 异常。
     */
    @Override
    public int read(Reader reader, DataWriter dataWriter) throws IOException
    {
        Columns columns = dataWriter.getColumns();
        if (columns.size() == 0)
            return 0;

        CsvReader csvReader = new CsvReader(reader);
        if (this.readUserSettings != null)
            csvReader.setUserSettings(this.readUserSettings);
        if (this.isFirstHeader() && csvReader.readHeaders())
            columns = this.readHeader(columns, csvReader.getHeaders());

        while (csvReader.readRecord())
        {
            int columnIndex = 0;
            int rowIndex = dataWriter.size();
            for (Column column : columns)
            {
                if (column.getFieldClass() != null && !ValueUtils.isEmpty(column.getName()))
                {
                    String textValue = csvReader.get(columnIndex);
                    Object value = column.parse(textValue);
                    dataWriter.setValue(rowIndex, column.getName(), value);
                }
                columnIndex++;
            }
            this.lineCount++;
        }
        return dataWriter.size();
    }

    private Columns readHeader(Columns columns, String[] textArray)
    {
        Columns columnList = new Columns();
        for (int i = 0; i < textArray.length; i++)
        {
            String fieldName = textArray[i];
            Column column = columns.find(fieldName);
            if (column == null)
            {
                column = new Column();
                column.setTitle(fieldName);
            }
            columnList.add(column);
        }
        return columnList;
    }

    public void setReadUserSettings(ReadUserSettings readUserSettings)
    {
        this.readUserSettings = readUserSettings;
    }

    public void setWriteUserSettings(WriteUserSettings writeUserSettings)
    {
        this.writeUserSettings = writeUserSettings;
    }

    /**
     * 导出实体对象数据到数据流。
     * @param outputStream 输出流。
     * @param dataReader 数据来源。
     * @param 输出数量。
     */
    @Override
    public int write(DataReader dataReader)
    {
        if (dataReader == null || dataReader.size() == 0)
            return 0;

        Columns columns = dataReader.getColumns();
        if (columns.size() == 0)
            return 0;

        PrintWriter writer = this.getPrintWriter();
        try
        {
            CsvWriter csvWriter = new CsvWriter(writer, Letters.COMMA);
            if (this.writeUserSettings != null)
                csvWriter.setUserSettings(this.writeUserSettings);
            if (this.isFirstHeader())
                this.writeHeader(csvWriter, columns);
            // 输出数据内容。
            for (int i = 0; i < dataReader.size(); i++)
            {
                for (Column column : columns)
                {
                    if (ValueUtils.isEmpty(column.getName()))
                        csvWriter.write(null);
                    else
                    {
                        Object value = dataReader.getValue(i, column.getName());
                        csvWriter.write(column.toString(value));
                    }
                }
                csvWriter.endRecord();
                this.lineCount++;
            }
        }
        catch (IOException e)
        {
            this.logger.error(this, e);
        }
        return dataReader.size();
    }

    /**
     * 输出标题。
     * @throws IOException
     */
    private void writeHeader(CsvWriter writer, Columns columns) throws IOException
    {
        for (Column column : columns)
            writer.write(column.getTitle());
        writer.endRecord();
        this.lineCount++;
    }
}

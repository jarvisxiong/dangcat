package org.dangcat.document.txt;

import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.DataReader;
import org.dangcat.persistence.DataWriter;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Columns;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TXT文档输入输出。
 * @author dangcat
 * 
 */
public class TextDocument extends TextDocumentBase
{
    private static final String EMPTY_TEXT = " ";
    private static final String TEXT_NULL = "null";
    private int[] columnWidths = null;
    private char fieldSeparator = ' ';

    private void addContent(List<String> textList, String content)
    {
        if (content.startsWith(EMPTY_TEXT) || content.endsWith(EMPTY_TEXT))
            content = content.trim();
        textList.add(content);
    }

    private void calculateColumnWidth(DataReader dataReader)
    {
        Columns columns = dataReader.getColumns();
        this.columnWidths = new int[columns.size()];
        int index = 0;
        for (Column column : columns)
        {
            String text = column.getTitle();
            if (text != null)
                this.columnWidths[index] = text.trim().getBytes().length;
            else
                this.columnWidths[index] = 0;
            index++;
        }
        for (int i = 0; i < dataReader.size(); i++)
        {
            index = 0;
            for (Column column : columns)
            {
                Object value = dataReader.getValue(i, column.getName());
                String text = column.toString(value);
                if (text == null)
                    text = TEXT_NULL;
                else
                    text = text.trim();
                this.columnWidths[index] = Math.max(text.getBytes().length, this.columnWidths[index]);
                index++;
            }
        }
    }

    /**
     * 格式化字段内容。
     * @param value 数值对象。
     * @return 格式化后内容。
     */
    private String format(int width, String text)
    {
        if (width > 0 && text != null)
            return String.format("%-" + width + "s", text.trim());
        return text;
    }

    public int[] getColumnWidths()
    {
        return this.columnWidths;
    }

    public void setColumnWidths(int[] columnWidths) {
        this.columnWidths = columnWidths;
    }

    public char getFieldSeparator()
    {
        return this.fieldSeparator;
    }

    public void setFieldSeparator(char fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    /**
     * 由行数据解析成数组。
     * @param line 数据行。
     * @param length 长度。
     * @return
     */
    public String[] parseTextArray(String line)
    {
        List<String> textList = new ArrayList<String>();
        int beginIndex = 0;
        if (this.columnWidths != null)
        {
            char[] charArray = line.toCharArray();
            int index = 0;
            for (int width : this.columnWidths)
            {
                char[] content = Arrays.copyOfRange(charArray, index, index + width);
                this.addContent(textList, new String(content));
                index += width + 1;
            }
        }
        else
        {
            for (int position = 0; position < line.length(); position++)
            {
                if (line.charAt(position) == this.getFieldSeparator())
                {
                    if (position > beginIndex)
                        this.addContent(textList, line.substring(beginIndex, position));
                    beginIndex = position + 1;
                }
            }
            this.addContent(textList, line.substring(beginIndex));
        }
        return textList.toArray(new String[0]);
    }

    /**
     * 从缓冲流加载数据。
     * @param reader 数据缓冲流。
     * @param dataWriter 数据输出接口。
     * @return 读入的行数。
     * @throws IOException 异常。
     */
    @Override
    public int read(Reader reader, DataWriter dataWriter) throws IOException
    {
        BufferedReader bufferedReader = null;
        if (reader instanceof BufferedReader)
            bufferedReader = (BufferedReader) reader;
        else
            bufferedReader = new BufferedReader(reader);
        Columns columns = dataWriter.getColumns();
        if (columns.size() == 0)
            return 0;

        int count = 0;
        String line = null;
        while ((line = bufferedReader.readLine()) != null)
        {
            String[] textArray = this.parseTextArray(line);
            if (textArray.length != columns.size())
            {
                this.logger.error("The line is invalid :" + line);
                continue;
            }

            if (count == 0)
                columns = this.readHeader(columns, textArray);
            else
            {
                int columnIndex = 0;
                int rowIndex = dataWriter.size();
                for (Column column : columns)
                {
                    if (column.getFieldClass() != null && !ValueUtils.isEmpty(column.getName()))
                    {
                        String textValue = textArray[columnIndex].trim();
                        Object value = column.parse(textValue);
                        dataWriter.setValue(rowIndex, column.getName(), value);
                    }
                    columnIndex++;
                }
            }
            count++;
        }
        return dataWriter.size();
    }

    private Columns readHeader(Columns columns, String[] textArray)
    {
        Columns columnList = new Columns();
        for (String fieldName : textArray)
        {
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

    /**
     * 导出数据到数据流。
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

        this.calculateColumnWidth(dataReader);
        PrintWriter writer = this.getPrintWriter();
        if (this.isFirstHeader())
            this.writeHeader(writer, columns);
        // 输出数据内容。
        for (int i = 0; i < dataReader.size(); i++)
        {
            if (this.lineCount > 0 || this.isFirstHeader())
                writer.append(Environment.LINE_SEPARATOR);
            int index = 0;
            for (Column column : columns)
            {
                if (index > 0)
                    writer.append(this.getFieldSeparator());

                String value = column.toString(dataReader.getValue(i, column.getName()));
                writer.append(this.format(this.columnWidths[index], value));
                index++;
            }
            this.lineCount++;
        }
        return dataReader.size();
    }

    /**
     * 输出标题。
     */
    private void writeHeader(PrintWriter writer, Columns columns)
    {
        int index = 0;
        for (Column column : columns)
        {
            if (index > 0)
                writer.append(this.getFieldSeparator());
            writer.append(this.format(this.columnWidths[index], column.getTitle()));
            index++;
        }
    }
}

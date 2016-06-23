package org.dangcat.document.excel;

import org.dangcat.persistence.DataReader;
import org.dangcat.persistence.model.Column;

/**
 * Excel文档操作。
 * @author dangcat
 * 
 */
public class ExcelDocumentWriter extends ExcelWriter
{
    private DataReader dataReader = null;
    private ExcelFormator excelFormator = null;
    private String sheetTitle = null;

    public DataReader getDataReader()
    {
        return this.dataReader;
    }

    public void setDataReader(DataReader dataReader)
    {
        this.dataReader = dataReader;
        this.excelFormator = new DataExcelFormator(dataReader);
    }

    public ExcelFormator getExcelFormator()
    {
        return this.excelFormator;
    }

    public void setExcelFormator(ExcelFormator excelFormator)
    {
        this.excelFormator = excelFormator;
    }

    @Override
    protected ExcelFormator getExcelFormator(String sheetName)
    {
        return this.excelFormator;
    }

    public String getSheetTitle()
    {
        return this.sheetTitle;
    }

    public void setSheetTitle(String sheetTitle)
    {
        this.sheetTitle = sheetTitle;
    }

    @Override
    public String toString()
    {
        return "ExcelDocumentWriter [dataReader=" + this.dataReader + ", excelFormator=" + this.excelFormator + "]";
    }

    /**
     * 写入数据。
     * @param sheet 页标对象。
     * @param dataReader 数据来源。
     */
    private void writeBody()
    {
        for (int index = 0; index < this.dataReader.size(); index++)
        {
            int columnIndex = 0;
            for (Column column : this.dataReader.getColumns())
            {
                String value = column.toString(this.dataReader.getValue(index, column.getName()));
                this.write(this.getSheetTitle(), index + 1, columnIndex++, value);
            }
        }
    }

    @Override
    protected void writeContent()
    {
        this.writeHeader();
        this.writeBody();
    }

    /**
     * 产生标题。
     * @param sheet 页标对象。
     * @param dataReader 数据来源。
     */
    private void writeHeader()
    {
        int index = 0;
        for (Column column : this.dataReader.getColumns())
            this.write(this.getSheetTitle(), 0, index++, column.getTitle());
    }
}
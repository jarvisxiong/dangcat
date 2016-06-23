package org.dangcat.document.excel;

import org.dangcat.persistence.DataWriter;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Columns;

import java.util.List;

/**
 * Excel文档操作。
 * @author dangcat
 * 
 */
public class ExcelDocumentReader extends ExcelReader
{
    private Columns columns = null;
    private DataWriter dataWriter = null;
    private int rowIndex = 0;

    public DataWriter getDataWriter()
    {
        return this.dataWriter;
    }

    public void setDataWriter(DataWriter dataWriter) {
        this.dataWriter = dataWriter;
        this.columns = null;
    }

    /**
     * 读取文档的数据内容。
     * @param sheet 页面标签。
     * @param dataWriter 数据接收对象。
     */
    private void readBody(List<Object> values)
    {
        for (int i = 0; i < values.size(); i++)
        {
            Object value = values.get(i);
            if (value == null)
                continue;

            Column column = this.columns.get(i);
            if (column == null)
                continue;

            // 防止类型不一致。
            if (value != null && !column.getFieldClass().isAssignableFrom(value.getClass()))
                value = column.parse(value.toString());
            this.dataWriter.setValue(this.rowIndex, column.getName(), value);
        }
        this.rowIndex++;
    }

    /**
     * 读取文档的栏位标题。
     * @param sheet 页面标签。
     * @param dataWriter 数据接收对象。
     */
    private void readHeader(List<Object> values)
    {
        Columns columns = new Columns();
        for (Object value : values)
        {
            Column column = null;
            if (value != null)
            {
                column = this.dataWriter.getColumns().find(value.toString());
                if (column != null)
                    this.columns = columns;
            }
            columns.add(column);
        }
        this.rowIndex = 0;
    }

    @Override
    protected void readRowData(int rowIndex, List<Object> values)
    {
        if (this.columns == null)
            this.readHeader(values);
        else
            this.readBody(values);
    }
}

package org.dangcat.document.excel;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dangcat.persistence.DataReader;
import org.dangcat.persistence.model.Column;

public class DataExcelFormator implements ExcelFormator
{
    private static final Integer MAX_COLUMN_DISPLAYSIZE = 40;
    private static final Integer MAX_COLUMN_WIDTH = MAX_COLUMN_DISPLAYSIZE * 256;
    private static final Integer MIN_COLUMN_DISPLAYSIZE = 10;
    private static final Integer MIN_COLUMN_WIDTH = MIN_COLUMN_DISPLAYSIZE * 256;
    private Map<Integer, ExcelCellStyle> cellStyleMap = new HashMap<Integer, ExcelCellStyle>();
    private DataReader dataReader = null;
    private Sheet sheet = null;
    private Workbook workbook = null;

    public DataExcelFormator(DataReader dataReader)
    {
        this.dataReader = dataReader;
    }

    private void format(Cell cell, Column column, int position, int logic)
    {
        Integer key = ExcelCellStyle.getkey(position, logic, column.getFieldClass());
        ExcelCellStyle excelCellStyle = this.cellStyleMap.get(key);
        if (excelCellStyle == null)
        {
            excelCellStyle = new ExcelCellStyle(position, logic, column.getFieldClass());
            excelCellStyle.createCellStyle(this.workbook);
            this.cellStyleMap.put(key, excelCellStyle);
        }
        cell.setCellStyle(excelCellStyle.getCellStyle());
    }

    /**
     * 格式化文档。
     * @param sheetIndex 页标位置。
     * @param rectangle 格式化范围。
     */
    @Override
    public void format(Workbook workbook, Sheet sheet)
    {
        this.workbook = workbook;
        this.sheet = sheet;

        // 设置标题栏。
        this.formatHeader();
        // 设置报表身。
        this.formatBody();
        // 设置总合计。
        if (this.hasTail())
            this.formatTail();
        // 设置栏位宽度。
        this.formatColumnWidth();
    }

    /**
     * 设置报表身。
     */
    private void formatBody()
    {
        for (Row row : this.sheet)
        {
            if (row.getRowNum() == 0 || (this.hasTail() && row.getRowNum() == this.sheet.getLastRowNum()))
                continue;

            int columnIndex = 0;
            for (Column column : this.dataReader.getColumns())
            {
                int logic = ExcelCellStyle.LOGIC_BODY;
                if (column.isPrimaryKey())
                    logic = ExcelCellStyle.LOGIC_PRIMARY;
                int position = this.getPosition(row.getRowNum(), columnIndex);
                Cell cell = row.getCell(columnIndex);
                this.format(cell, column, position, logic);
                this.rememberLength(column, cell.getStringCellValue());
                columnIndex++;
            }
        }
    }

    /**
     * 设置栏位宽度。
     */
    private void formatColumnWidth()
    {
        int columnIndex = 0;
        for (Column column : this.dataReader.getColumns())
        {
            Integer length = null;
            if (column.getParams().containsKey("MAX_LENGTH"))
                length = (Integer) column.getParams().get("MAX_LENGTH");
            int columnWidth = Math.max(Math.min(MAX_COLUMN_DISPLAYSIZE, length), MIN_COLUMN_DISPLAYSIZE);
            if (columnWidth == MAX_COLUMN_DISPLAYSIZE)
                this.sheet.setColumnWidth(columnIndex, MAX_COLUMN_WIDTH);
            else if (columnWidth == MIN_COLUMN_DISPLAYSIZE)
                this.sheet.setColumnWidth(columnIndex, MIN_COLUMN_WIDTH);
            else
                this.sheet.autoSizeColumn(columnIndex);
            columnIndex++;
        }
    }

    /**
     * 设置标题栏。
     */
    private void formatHeader()
    {
        int columnIndex = 0;
        for (Column column : this.dataReader.getColumns())
        {
            int position = this.getPosition(0, columnIndex);
            Cell cell = this.sheet.getRow(0).getCell(columnIndex);
            this.format(cell, column, position, ExcelCellStyle.LOGIC_HEADER);
            this.rememberLength(column, cell.getStringCellValue());
            columnIndex++;
        }
    }

    /**
     * 设置总合计。
     */
    private void formatTail()
    {
        int columnIndex = 0;
        for (Column column : this.dataReader.getColumns())
        {
            int position = this.getPosition(this.sheet.getLastRowNum(), columnIndex);
            Cell cell = this.sheet.getRow(this.sheet.getLastRowNum()).getCell(columnIndex);
            this.format(cell, column, position, ExcelCellStyle.LOGIC_TAIL);
            columnIndex++;
        }
    }

    private int getPosition(int row, int col)
    {
        if (row == 0)
        {
            if (col == 0)
                return 1;
            if (col == this.sheet.getRow(row).getLastCellNum() - 1)
                return 3;
            return 2;
        }
        else if (row == this.sheet.getLastRowNum())
        {
            if (col == 0)
                return 7;
            if (col == this.sheet.getRow(row).getLastCellNum() - 1)
                return 9;
            return 8;
        }
        else
        {
            if (col == 0)
                return 4;
            if (col == this.sheet.getRow(row).getLastCellNum() - 1)
                return 6;
        }
        return 5;
    }

    private boolean hasTail()
    {
        for (Column column : this.dataReader.getColumns())
        {
            if (this.dataReader.getValue(this.dataReader.size(), column.getName()) != null)
                return true;
        }
        return false;
    }

    private void rememberLength(Column column, String value)
    {
        if (value != null)
        {
            int valueLenth = value.getBytes().length + 2;
            Integer length = (Integer) column.getParams().get("MAX_LENGTH");
            column.getParams().put("MAX_LENGTH", length == null ? valueLenth : Math.max(length, valueLenth));
        }
    }
}

package org.dangcat.document.excel;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.dangcat.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public abstract class ExcelReader
{
    protected final Logger logger = Logger.getLogger(this.getClass());
    private Sheet currentSheet = null;
    private Workbook workbook = null;

    protected void afterReadSheet(int sheetIndex)
    {
    }

    protected boolean beforeReadSheet(int sheetIndex)
    {
        return true;
    }

    protected String getCurrentSheetName()
    {
        if (this.currentSheet == null)
            return null;
        return this.currentSheet.getSheetName();
    }

    public Workbook getWorkbook()
    {
        return this.workbook;
    }

    /**
     * 从指定档案读取数据。
     * @param file 文件对象。
     * @throws IOException 文件访问异常。
     */
    public void read(File file) throws IOException
    {
        FileInputStream fileInputStream = null;
        try
        {
            fileInputStream = new FileInputStream(file);
            this.read(fileInputStream);
        }
        finally
        {
            FileUtils.close(fileInputStream);
        }
    }

    /**
     * 从数据流读取数据。
     * @param InputStream 文件输入流。
     * @throws IOException 文件访问异常。
     * @throws InvalidFormatException 格式异常。
     */
    public void read(InputStream InputStream) throws IOException
    {
        try
        {
            this.workbook = WorkbookFactory.create(InputStream);
        }
        catch (InvalidFormatException e)
        {
            throw new IOException("The file format is invalid.", e);
        }
    }

    protected Object readCellData(int rowIndex, int columnIndex, Object value)
    {
        return value;
    }

    protected abstract void readRowData(int rowIndex, List<Object> values);

    public void readSheet(int sheetIndex)
    {
        Sheet sheet = this.getWorkbook().getSheetAt(sheetIndex);
        if (sheet != null)
            this.readSheet(sheetIndex, sheet);
    }

    /**
     * 从文档读取数据。
     * @param sheetIndex 页面标签。
     */
    protected void readSheet(int sheetIndex, Sheet sheet)
    {
        if (!this.beforeReadSheet(sheetIndex))
            return;

        for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++)
        {
            Row row = sheet.getRow(rowIndex);
            if (row == null)
                continue;
            List<Object> values = new LinkedList<Object>();
            for (int columnIndex = 0; columnIndex <= row.getLastCellNum(); columnIndex++)
            {
                Cell cell = row.getCell(columnIndex);
                if (!sheet.isColumnHidden(columnIndex))
                {
                    Object value = null;
                    if (cell != null)
                    {
                        switch (cell.getCellType())
                        {
                            case Cell.CELL_TYPE_STRING:
                                value = cell.getRichStringCellValue().getString();
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell))
                                    value = cell.getDateCellValue();
                                else
                                    value = cell.getNumericCellValue();
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:
                                value = cell.getBooleanCellValue();
                                break;
                            case Cell.CELL_TYPE_FORMULA:
                                value = cell.getCellFormula();
                                break;
                        }
                    }
                    values.add(this.readCellData(rowIndex, columnIndex, value));
                }
            }
            this.readRowData(rowIndex, values);
        }
        this.afterReadSheet(sheetIndex);
    }

    public void readSheet(String name)
    {
        Sheet sheet = this.workbook.getSheet(name);
        if (sheet != null)
        {
            int sheetIndex = this.workbook.getSheetIndex(sheet);
            this.readSheet(sheetIndex, sheet);
        }
    }

    public void readSheets()
    {
        for (int i = 0; i < this.workbook.getNumberOfSheets(); i++)
        {
            this.currentSheet = this.workbook.getSheetAt(i);
            if (this.currentSheet != null)
                this.readSheet(i, this.currentSheet);
        }
    }
}

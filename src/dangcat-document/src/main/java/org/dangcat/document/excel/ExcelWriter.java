package org.dangcat.document.excel;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dangcat.commons.utils.ValueUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Excel文档操作。
 *
 * @author dangcat
 */
public abstract class ExcelWriter {
    public static final String EXCEL_HSSF = "xls";
    public static final String EXCEL_XSSF = "xlsx";
    protected final Logger logger = Logger.getLogger(this.getClass());
    private String fileExt = EXCEL_HSSF;
    private String password = null;
    private Workbook workbook = null;

    protected Cell findCell(String sheetName, int rowIndex, int cellIndex) {
        Sheet sheet = this.getWorkbook().getSheet(sheetName);
        if (sheet == null) {
            String safeName = WorkbookUtil.createSafeSheetName(sheetName);
            sheet = this.getWorkbook().createSheet(safeName);
        }

        Row row = sheet.getRow(rowIndex);
        if (row == null)
            row = sheet.createRow(rowIndex);
        Cell cell = row.getCell(cellIndex);
        if (cell == null)
            cell = row.createCell(cellIndex);
        return cell;
    }

    private void formatWorkbook() {
        Workbook workbook = this.getWorkbook();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            ExcelFormator excelFormator = this.getExcelFormator(sheet.getSheetName());
            if (excelFormator != null)
                excelFormator.format(workbook, sheet);
        }
    }

    protected ExcelFormator getExcelFormator(String sheetName) {
        return null;
    }

    public String getFileExt() {
        return this.fileExt;
    }

    public void setFileExt(String fileExt) {
        if (fileExt.toLowerCase().endsWith(EXCEL_XSSF))
            this.fileExt = EXCEL_XSSF;
        else
            this.fileExt = EXCEL_HSSF;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Sheet getSheet(String sheetName) {
        Sheet sheet = null;
        Workbook workbook = this.getWorkbook();
        if (workbook != null)
            sheet = workbook.getSheet(sheetName);
        return sheet;
    }

    public String[] getSheetNames() {
        List<String> sheetNameList = new ArrayList<String>();
        for (int i = 0; i < this.getWorkbook().getNumberOfSheets(); i++)
            sheetNameList.add(this.workbook.getSheetAt(i).getSheetName());
        return sheetNameList.toArray(new String[0]);
    }

    public Workbook getWorkbook() {
        if (this.workbook == null)
            this.workbook = this.getFileExt().equals(EXCEL_HSSF) ? new HSSFWorkbook() : new XSSFWorkbook();
        return this.workbook;
    }

    private void protectSheets() {
        // 设置工作表保护模式。
        if (!ValueUtils.isEmpty(this.getPassword())) {
            for (int i = 0; i < this.workbook.getNumberOfSheets(); i++) {
                Sheet sheet = this.workbook.getSheetAt(i);
                sheet.protectSheet(this.getPassword());
            }
        }
    }

    /**
     * 把数据写入指定文件。
     *
     * @param file       目标文件。
     * @param dataReader 数据来源。
     * @throws IOException 文件访问异常。
     */
    public void write(File file) throws IOException {
        this.setFileExt(file.getName());
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        this.write(fileOutputStream);
        fileOutputStream.close();
    }

    /**
     * 把数据写入指定输出流。
     *
     * @param outputStream 输出流。
     * @param dataReader   数据来源。
     * @throws IOException 文件访问异常。
     */
    public void write(OutputStream outputStream) throws IOException {
        this.writeContent();
        Workbook workbook = this.getWorkbook();

        this.formatWorkbook();
        this.protectSheets();
        workbook.write(outputStream);
    }

    protected Cell write(String sheetName, int rowIndex, int cellIndex, boolean value) {
        Cell cell = this.findCell(sheetName, rowIndex, cellIndex);
        cell.setCellValue(value);
        return cell;
    }

    protected Cell write(String sheetName, int rowIndex, int cellIndex, Calendar value) {
        Cell cell = this.findCell(sheetName, rowIndex, cellIndex);
        cell.setCellValue(value);
        return cell;
    }

    protected Cell write(String sheetName, int rowIndex, int cellIndex, Date value) {
        Cell cell = this.findCell(sheetName, rowIndex, cellIndex);
        cell.setCellValue(value);
        return cell;
    }

    protected Cell write(String sheetName, int rowIndex, int cellIndex, double value) {
        Cell cell = this.findCell(sheetName, rowIndex, cellIndex);
        cell.setCellValue(value);
        return cell;
    }

    protected Cell write(String sheetName, int rowIndex, int cellIndex, RichTextString value) {
        Cell cell = this.findCell(sheetName, rowIndex, cellIndex);
        cell.setCellValue(value);
        return cell;
    }

    protected Cell write(String sheetName, int rowIndex, int cellIndex, String value) {
        Cell cell = this.findCell(sheetName, rowIndex, cellIndex);
        cell.setCellValue(value);
        return cell;
    }

    protected abstract void writeContent();
}
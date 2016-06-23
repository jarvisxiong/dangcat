package org.dangcat.document.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 格式化文档。
 */
public interface ExcelFormator
{
    /**
     * 格式化文档。
     * @param workbook 工作簿。
     * @param sheet 标签页。
     */
    void format(Workbook workbook, Sheet sheet);
}

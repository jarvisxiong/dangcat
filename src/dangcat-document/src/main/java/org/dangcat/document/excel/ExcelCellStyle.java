package org.dangcat.document.excel;

import java.util.Date;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelCellStyle
{
    private static final short COLOR_BODY_INDEX = HSSFColor.LIGHT_YELLOW.index;
    private static final short COLOR_HEADER_INDEX = HSSFColor.PALE_BLUE.index;
    private static final short COLOR_PRIMARY_INDEX = HSSFColor.TAN.index;
    private static final short COLOR_TAIL_INDEX = HSSFColor.SKY_BLUE.index;
    public static final short LOGIC_BODY = 1;
    public static final short LOGIC_HEADER = 0;
    public static final short LOGIC_PRIMARY = 3;
    public static final short LOGIC_TAIL = 2;

    public static int getkey(int position, int logic, Class<?> classType)
    {
        int id = position;
        id = id * 10 + logic;

        int dataType = 0;
        if (Number.class.isAssignableFrom(classType))
            dataType = 1;
        else if (Date.class.isAssignableFrom(classType))
            dataType = 2;
        id = id * 10 + dataType;
        return id;
    }

    private CellStyle cellStyle = null;

    /**
     * 数据类型。
     */
    private Class<?> classType;

    /**
     * 内容类型：0：header；1、body；2、tail；3、primary
     */
    private int logic;
    /**
     * 九宫位置：0-10
     */
    private int position;

    public ExcelCellStyle(int position, int logic, Class<?> classType)
    {
        this.position = position;
        this.logic = logic;
        this.classType = classType;
    }

    public void createCellStyle(Workbook workbook)
    {
        CellStyle cellStyle = workbook.createCellStyle();
        this.initBorder(cellStyle);
        this.initForegroundColor(cellStyle);
        this.initAlign(cellStyle);
        this.cellStyle = cellStyle;
    }

    public CellStyle getCellStyle()
    {
        return cellStyle;
    }

    private void initAlign(CellStyle cellStyle)
    {
        if (this.logic == LOGIC_HEADER)
            cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        else
        {
            if (Number.class.isAssignableFrom(this.classType))
                cellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            else if (Date.class.isAssignableFrom(this.classType))
                cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
            else
                cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
        }
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    }

    private void initBorder(CellStyle cellStyle)
    {
        short left = CellStyle.BORDER_THIN;
        short top = CellStyle.BORDER_THIN;
        short right = CellStyle.BORDER_THIN;
        short bottom = CellStyle.BORDER_THIN;
        short borderColor = HSSFColor.BLACK.index;

        if (this.position == 1 || this.position == 2 || this.position == 3 || this.position == 10)
            top = CellStyle.BORDER_DOUBLE;

        if (this.position == 1 || this.position == 4 || this.position == 7 || this.position == 10)
            left = CellStyle.BORDER_DOUBLE;

        if (this.position == 3 || this.position == 6 || this.position == 9 || this.position == 10)
            right = CellStyle.BORDER_DOUBLE;

        if (this.position == 7 || this.position == 8 || this.position == 9 || this.position == 10)
            bottom = CellStyle.BORDER_DOUBLE;

        if (this.position > 0)
        {
            cellStyle.setBorderRight(right);
            cellStyle.setRightBorderColor(borderColor);
            cellStyle.setBorderLeft(left);
            cellStyle.setLeftBorderColor(borderColor);
            cellStyle.setBorderTop(top);
            cellStyle.setTopBorderColor(borderColor);
            cellStyle.setBorderBottom(bottom);
            cellStyle.setBottomBorderColor(borderColor);
        }
    }

    private void initForegroundColor(CellStyle cellStyle)
    {
        if (this.logic == LOGIC_HEADER)
            cellStyle.setFillForegroundColor(COLOR_HEADER_INDEX);
        else if (this.logic == LOGIC_BODY)
            cellStyle.setFillForegroundColor(COLOR_BODY_INDEX);
        else if (this.logic == LOGIC_TAIL)
            cellStyle.setFillForegroundColor(COLOR_TAIL_INDEX);
        else if (this.logic == LOGIC_PRIMARY)
            cellStyle.setFillForegroundColor(COLOR_PRIMARY_INDEX);
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    }
}

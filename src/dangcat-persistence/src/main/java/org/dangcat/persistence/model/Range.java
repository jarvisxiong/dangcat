package org.dangcat.persistence.model;

/**
 * 数据范围。
 * @author dangcat
 * 
 */
public class Range implements java.io.Serializable, Cloneable
{
    public static final int BY_RESULTSET = 1;
    public static final int BY_SQLSYNTAX = 0;
    private static final long serialVersionUID = 1L;
    public static final String TOP_SQLFLAG = "@%" + Range.class.getSimpleName() + "%@";
    public static final String TOTALSIZE = "TotalSize";
    private boolean calculateTotalSize = false;
    private int from = 1;
    /** 实现方式：0：通过ResultSet实现翻页查询；1：通过语句实现翻页查询。 */
    private int mode = BY_RESULTSET;
    private int pageNum = 0;
    private int pageSize = 0;
    private int to = 0;
    private int totalSize = 0;

    public Range()
    {
    }

    public Range(int topN)
    {
        this.from = 1;
        this.pageNum = 1;
        this.pageSize = topN;
        this.to = topN;
    }

    public Range(int pageNum, int pageSize)
    {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.calculateTotalSize = true;
        this.calculatePosition();
    }

    private void calculatePosition()
    {
        this.from = this.pageSize * (this.pageNum - 1) + 1;
        this.to = this.pageSize * this.pageNum;
    }

    public int getFrom()
    {
        return this.from;
    }

    public int getMode()
    {
        return mode;
    }

    public int getPageNum()
    {
        return this.pageNum;
    }

    public int getPageSize()
    {
        return this.pageSize;
    }

    public int getTo()
    {
        return this.to;
    }

    public int getTotalSize()
    {
        return this.totalSize;
    }

    public boolean isCalculateTotalSize()
    {
        return this.calculateTotalSize;
    }

    public void setCalculateTotalSize(boolean calculateTotalSize)
    {
        this.calculateTotalSize = calculateTotalSize;
        this.calculateTotalSize = true;
    }

    public void setFrom(int from)
    {
        this.from = from;
    }

    public void setMode(int mode)
    {
        this.mode = mode;
    }

    public void setPageNum(int pageNum)
    {
        this.pageNum = pageNum;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public void setRange(int from, int pageSize)
    {
        this.from = from;
        this.to = from + pageSize - 1;
        this.pageSize = pageSize;
        this.pageNum = from / this.pageSize;
        if (from % this.pageSize != 0)
            this.pageNum++;
    }

    public void setTo(int to)
    {
        this.to = to;
    }

    public void setTotalSize(int totalSize)
    {
        if (totalSize > 0)
        {
            int totalPageNum = totalSize / this.pageSize;
            if (totalSize % this.pageSize != 0)
                totalPageNum++;
            this.pageNum = Math.min(totalPageNum, this.pageNum);
            this.calculatePosition();
        }
        this.totalSize = totalSize;
    }
}

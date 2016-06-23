package org.dangcat.persistence.filter;

/**
 * 过滤类型。
 * @author dangcat
 * 
 */
public enum FilterType
{
    /**
     * 范围。
     */
    between(1),
    /**
     * 等于。
     */
    eq(2),
    /**
     * 大于等于。
     */
    ge(4),
    /**
     * 大于。
     */
    gt(3),
    /**
     * 忽略。
     */
    ignore(0),
    /**
     * 小于等于。
     */
    le(6),
    /**
     * LIKE。
     */
    like(7),
    /**
     * 小于。
     */
    lt(5),
    /**
     * 自定义子句。
     */
    sql(8);

    /**
     * 过滤类型值。
     */
    private final int value;

    /**
     * 构造过滤类型。
     * @param value 过滤类型值。
     */
    FilterType(int value)
    {
        this.value = value;
    }

    /**
     * 取得过滤类型。
     * @return 过滤类型值。
     */
    public int getValue()
    {
        return this.value;
    }
}

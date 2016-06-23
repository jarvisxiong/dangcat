package org.dangcat.persistence.model;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.ValueUtils;

import java.sql.Time;
import java.util.Date;

/**
 * 基本数据对象。
 *
 * @author dangcat
 */
public class Field implements java.io.Serializable, Comparable<Field> {
    private static final long serialVersionUID = 1L;
    /**
     * 数据对象的状态：新增、修改。
     */
    private DataState dataState = DataState.Browse;
    /**
     * 旧的数据值。
     */
    private Object oldValue;
    /**
     * 所属数据行。
     */
    private Row parent;
    /**
     * 当前数据值。
     */
    private Object value;

    /**
     * 构造函数。
     */
    public Field() {
    }

    /**
     * 构造函数。
     *
     * @param value 当前数据值。
     */
    public Field(Object value) {
        this.value = value;
    }

    /**
     * 构造函数。
     *
     * @param parent 父数据行对象。
     */
    public Field(Row parent) {
        this.parent = parent;
    }

    /**
     * 建立新的实例。
     *
     * @return
     */
    public static Field newInstance() {
        return new Field();
    }

    /**
     * 改变当前值。
     *
     * @param newValue 新的值对象。
     */
    private void changeValue(Object newValue) {
        if (this.oldValue == null)
            this.oldValue = this.value;
        this.value = newValue;

        if (this.dataState == DataState.Browse) {
            this.dataState = DataState.Modified;
            if (this.parent != null)
                this.parent.notify(this);
        }
    }

    /**
     * 字段比较：相等返回0，大于返回大于0的数，小于返回小于0的数。
     */

    public int compareTo(Field destField) {
        if (destField == null)
            return -1;
        return ValueUtils.compare(this.getObject(), destField.getObject());
    }

    public byte[] getBytes() {
        return (byte[]) this.value;
    }

    public void setBytes(byte[] newValue) {
        this.changeValue(newValue);
    }

    public Character getChar() {
        if (this.value instanceof char[])
            return ((char[]) this.value)[0];
        return (Character) this.value;
    }

    public void setChar(char newValue) {
        this.changeValue(new char[]{newValue});
    }

    public char[] getChars() {
        return (char[]) this.value;
    }

    public void setChars(char[] newValue) {
        this.changeValue(newValue);
    }

    public DataState getDataState() {
        return this.dataState;
    }

    /**
     * 设置数据状态。
     *
     * @param dataState 数据状态。
     */
    public void setDataState(DataState dataState) {
        this.dataState = dataState;
        if (dataState == DataState.Browse || dataState == DataState.Insert)
            this.oldValue = null;
    }

    public Date getDate() {
        return (Date) this.value;
    }

    public void setDate(Date newValue) {
        this.changeValue(newValue);
    }

    public Double getDouble() {
        return (Double) this.value;
    }

    public void setDouble(Double newValue) {
        this.changeValue(newValue);
    }

    public Integer getInteger() {
        return (Integer) this.value;
    }

    public void setInteger(Integer newValue) {
        this.changeValue(newValue);
    }

    public Long getLong() {
        return (Long) this.value;
    }

    public void setLong(Long newValue) {
        this.changeValue(newValue);
    }

    public Number getNumber() {
        return (Number) this.value;
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject() {
        return (T) this.value;
    }

    public void setObject(Object newValue) {
        this.changeValue(newValue);
    }

    /**
     * 取得旧的数据值。
     *
     * @return 旧的值对象。
     */
    public Object getOldValue() {
        return this.oldValue;
    }

    public Row getParent() {
        return this.parent;
    }

    public void setParent(Row parent) {
        this.parent = parent;
    }

    public Short getShort() {
        return (Short) this.value;
    }

    public void setShort(Short newValue) {
        this.changeValue(newValue);
    }

    public String getString() {
        if (this.value instanceof String)
            return (String) this.value;
        return this.toString();
    }

    public void setString(String newValue) {
        this.changeValue(newValue);
    }

    public Time getTime() {
        return (Time) this.value;
    }

    public void setTime(Time newValue) {
        this.changeValue(newValue);
    }

    /**
     * 把当前值转成SQL表达式。
     *
     * @param transOldValue 是否转换旧值。
     * @return 表达式。
     */
    public String toSqlString(boolean transOldValue) {
        return TableStatementHelper.toSqlString(transOldValue ? this.oldValue : this.value);
    }

    /**
     * 输出数据内容。
     */
    public String toString() {
        if (this.value instanceof Date)
            return DateUtils.format((Date) this.value);
        return this.value == null ? null : this.value.toString();
    }
}

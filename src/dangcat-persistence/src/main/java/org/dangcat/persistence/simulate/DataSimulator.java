package org.dangcat.persistence.simulate;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.simulate.data.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 实体数据模拟器。
 * @author dangcat
 * 
 */
public abstract class DataSimulator
{
    /** 随机产生数据。 **/
    public static final int MODE_RANDOM = 1;
    /** 按照序列产生数据。 **/
    public static final int MODE_SEQUENCE = 0;
    /** 关联模拟数据库。 **/
    private DatabaseSimulator databaseSimulator = null;
    /** 字段映射表。 **/
    private Map<String, ValueSimulator> fieldSimulatorMap = new HashMap<String, ValueSimulator>();
    /** 数据产生模式 。 **/
    private int simulateMode = 0;
    /** 模拟数据数量。 **/
    private int size = 0;

    protected void addValueSimulator(Column column)
    {
        ValueSimulator valueSimulator = null;
        if (String.class.equals(column.getFieldClass()))
            valueSimulator = new StringSimulator(column.getName(), column.getDisplaySize());
        else if (char[].class.equals(column.getFieldClass()) || Character[].class.equals(column.getFieldClass()))
            valueSimulator = new CharsSimulator(column.getDisplaySize());
        else if (byte[].class.equals(column.getFieldClass()) || Byte[].class.equals(column.getFieldClass()))
            valueSimulator = new BytesSimulator(column.getDisplaySize());
        else if (Date.class.isAssignableFrom(column.getFieldClass()))
            valueSimulator = new DateSimulator();
        else if (ValueUtils.isNumber(column.getFieldClass()))
            valueSimulator = new NumberSimulator(column.getFieldClass());
        else if (ValueUtils.isBoolean(column.getFieldClass()))
            valueSimulator = new BooleanSimulator();
        if (valueSimulator != null)
        {
            valueSimulator.setDataSimulator(this);
            this.fieldSimulatorMap.put(column.getName(), valueSimulator);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends ValueSimulator> T findValueSimulator(String fieldName)
    {
        return (T) this.fieldSimulatorMap.get(fieldName);
    }

    public DatabaseSimulator getDatabaseSimulator()
    {
        return databaseSimulator;
    }

    public void setDatabaseSimulator(DatabaseSimulator databaseSimulator) {
        this.databaseSimulator = databaseSimulator;
    }

    public int getSimulateMode()
    {
        return simulateMode;
    }

    public void setSimulateMode(int simulateMode) {
        this.simulateMode = simulateMode;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size) {
        if (this.size != size) {
            for (ValueSimulator valueSimulator : this.fieldSimulatorMap.values()) {
                if (valueSimulator.getSize() == 0)
                    valueSimulator.setSize(size);
            }
            this.size = size;
        }
    }

    public abstract Table getTable();

    public String getTableName()
    {
        return this.getTable().getTableName().getName();
    }

    protected Object getValue(int index, String fieldName)
    {
        Object value = null;
        ValueSimulator valueSimulator = this.findValueSimulator(fieldName);
        if (valueSimulator != null)
            value = valueSimulator.getValue(index);
        return value;
    }

    protected Object getValue(String fieldName)
    {
        Object value = null;
        ValueSimulator valueSimulate = this.findValueSimulator(fieldName);
        if (valueSimulate != null)
        {
            if (this.getSimulateMode() == MODE_RANDOM)
                value = valueSimulate.nextRandom();
            else
                value = valueSimulate.nextSequence();
        }
        return value;
    }

    public abstract void initialize();
}

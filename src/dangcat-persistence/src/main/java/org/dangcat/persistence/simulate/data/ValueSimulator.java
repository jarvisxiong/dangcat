package org.dangcat.persistence.simulate.data;

import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.simulate.DataSimulator;
import org.dangcat.persistence.simulate.DatabaseSimulator;
import org.dangcat.persistence.simulate.table.SimulateData;

import java.util.Random;

/**
 * 数据模拟接口。
 * @author dangcat
 * 
 */
public abstract class ValueSimulator
{
    private Class<?> classType = null;
    private DataSimulator dataSimulator = null;
    private int position = 0;
    private Random random = null;
    private int size = 0;
    private Object[] values = null;

    public ValueSimulator(Class<?> classType)
    {
        this.classType = classType;
    }

    public void bind(Class<?> classType, String fieldName)
    {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classType);
        String tableName = entityMetaData.getTableName().getName();
        this.bind(tableName, fieldName);
    }

    public void bind(String tableName, String fieldName)
    {
        DatabaseSimulator databaseSimulator = this.dataSimulator.getDatabaseSimulator();
        if (databaseSimulator != null)
        {
            SimulateData simulateData = databaseSimulator.getSimulateData(tableName);
            if (simulateData != null)
            {
                ValueSimulator srcValueSimulator = simulateData.getDataSimulator().findValueSimulator(fieldName);
                if (srcValueSimulator != null)
                    this.setValues(srcValueSimulator.getValues());
            }
        }
    }

    protected abstract Object createValue(int index);

    protected Class<?> getClassType()
    {
        return classType;
    }

    public DataSimulator getDataSimulator()
    {
        return dataSimulator;
    }

    public void setDataSimulator(DataSimulator dataSimulator) {
        this.dataSimulator = dataSimulator;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private synchronized int getRandomIndex()
    {
        if (this.random == null)
            this.random = new Random();
        return this.random.nextInt() % this.size;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size) {
        if (this.size != size)
            this.values = null;
        this.size = size;
    }

    /**
     * 读取指定位置数据。
     * @param index 位置索引。
     * @return 产生的模拟数据。
     */
    public Object getValue(int index)
    {
        Object[] values = this.getValues();
        if (values == null || index < 0 || values.length == 0)
            return null;
        return values[index % this.size];
    }

    public Object[] getValues()
    {
        if (this.values == null)
        {
            Object[] values = new Object[this.size];
            for (int i = 0; i < values.length; i++)
                values[i] = this.createValue(i);
            this.values = values;
        }
        return this.values;
    }

    public void setValues(Object[] values) {
        if (values != null)
            this.setSize(values.length);
        this.values = values;
    }

    /**
     * 读取下一个随机模拟数据。
     * @return 产生的模拟数据。
     */
    public Object nextRandom()
    {
        return this.getValue(this.getRandomIndex());
    }

    /**
     * 读取下一个顺序模拟数据。
     * @return 产生的模拟数据。
     */
    public Object nextSequence()
    {
        if (this.position >= this.size)
            this.position = 0;
        return this.getValue(this.position);
    }
}

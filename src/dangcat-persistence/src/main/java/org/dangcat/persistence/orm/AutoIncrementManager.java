package org.dangcat.persistence.orm;

import org.dangcat.commons.utils.ValueUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 自增字段管理。
 * @author dangcat
 * 
 */
public class AutoIncrementManager
{
    /** 自增字段映射表。 */
    private static Map<String, Map<String, AutoIncrement>> databaseAutoIncrementMap = new HashMap<String, Map<String, AutoIncrement>>();
    private String databaseName = null;

    AutoIncrementManager(String databaseName) {
        this.databaseName = databaseName;
    }

    public static void reset()
    {
        reset(null, null);
    }

    public static void reset(String databaseName, String tableName)
    {
        synchronized (databaseAutoIncrementMap)
        {
            if (ValueUtils.isEmpty(tableName))
                databaseAutoIncrementMap.clear();
            else
            {
                Map<String, AutoIncrement> autoIncrementMap = databaseAutoIncrementMap.get(databaseName);
                if (autoIncrementMap != null)
                {
                    synchronized (autoIncrementMap)
                    {
                        if (ValueUtils.isEmpty(tableName))
                            autoIncrementMap.clear();
                        else
                            autoIncrementMap.remove(tableName);
                    }
                }
            }
        }
    }

    private Map<String, AutoIncrement> getDatabaseAutoIncrementMap()
    {
        Map<String, AutoIncrement> autoIncrementMap = null;
        synchronized (databaseAutoIncrementMap)
        {
            autoIncrementMap = databaseAutoIncrementMap.get(this.databaseName);
            if (autoIncrementMap == null)
            {
                autoIncrementMap = new HashMap<String, AutoIncrement>();
                databaseAutoIncrementMap.put(this.databaseName, autoIncrementMap);
            }
        }
        return autoIncrementMap;
    }

    /**
     * 根据表名字段名读取下一个主键序列。
     * @param tableName 表名。
     * @param fieldName 字段名。
     * @param classType 字段类型。
     * @param tableGenerator 序号策略。
     * @return 主键序列。
     * @throws SQLException
     */
    protected Object nextSequence(Connection connection, String tableName, String fieldName, Class<?> classType, TableGenerator tableGenerator) throws SQLException
    {
        Object sequence = null;
        Map<String, AutoIncrement> autoIncrementMap = this.getDatabaseAutoIncrementMap();
        synchronized (autoIncrementMap)
        {
            AutoIncrement autoIncrement = autoIncrementMap.get(tableName);
            if (autoIncrement == null)
            {
                autoIncrement = new AutoIncrement(tableName, fieldName, tableGenerator);
                autoIncrement.initialize(connection);
                autoIncrementMap.put(tableName, autoIncrement);
            }
            sequence = autoIncrement.nextSequence(classType);
        }
        return sequence;
    }

    protected void update(Connection connection) throws SQLException
    {
        Map<String, AutoIncrement> autoIncrementMap = this.getDatabaseAutoIncrementMap();
        synchronized (autoIncrementMap)
        {
            for (AutoIncrement autoIncrement : autoIncrementMap.values())
                autoIncrement.update(connection);
        }
    }
}

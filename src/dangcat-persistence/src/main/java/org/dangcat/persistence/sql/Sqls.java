package org.dangcat.persistence.sql;

import java.util.HashMap;
import java.util.Map;

import org.dangcat.commons.database.DatabaseType;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.orm.SqlBuilder;

/**
 * 查询集合。
 * @author dangcat
 * 
 */
public class Sqls
{
    private Class<?> classType = null;
    private String namePrefix = null;
    private Map<String, Sql> sqlNameMap = new HashMap<String, Sql>();

    /**
     * 添加查询对象。
     */
    public void add(Sql sql)
    {
        String sqlName = this.getSqlName(sql.getDatabaseType(), sql.getName());
        this.sqlNameMap.put(sqlName, sql);
    }

    /**
     * 查找指定的查询对象。
     * @param databaseType 数据库类型。
     * @return 查询对象。
     */
    public SqlBuilder find(DatabaseType databaseType)
    {
        return find(databaseType, null);
    }

    /**
     * 查找指定的查询对象。
     * @param databaseType 数据库类型。
     * @param name 命名语句。
     * @return 查询对象。
     */
    public SqlBuilder find(DatabaseType databaseType, String name)
    {
        SqlBuilder found = null;
        String sqlName = this.getSqlName(databaseType, name);
        Sql sql = this.sqlNameMap.get(sqlName);
        if (sql == null || ValueUtils.isEmpty(sql.getSql()))
        {
            sqlName = this.getSqlName(DatabaseType.Default, name);
            sql = this.sqlNameMap.get(sqlName);
        }
        if (sql != null && !ValueUtils.isEmpty(sql.getSql()))
        {
            found = new SqlBuilder(sql.getSql());
            if (sql.getDelimiter() != null)
                found.setDelimiter(sql.getDelimiter());
        }
        return found;
    }

    public SqlBuilder find(String name)
    {
        return find(null, name);
    }

    public Class<?> getClassType()
    {
        return classType;
    }

    private String getNamePrefix(Class<?> classType)
    {
        String fileName = this.namePrefix;
        if (ValueUtils.isEmpty(fileName))
            fileName = classType.getSimpleName();
        return fileName;
    }

    private String getSqlName(DatabaseType databaseType, String name)
    {
        if (databaseType == null)
            databaseType = DatabaseType.Default;

        StringBuilder info = new StringBuilder();
        info.append(databaseType.name());
        info.append("_");
        info.append(name == null ? Sql.QUERY : name);
        return info.toString().toLowerCase();
    }

    /**
     * 配置的查询语句。
     * @param classType 资源所在位置。
     */
    public void read(Class<?> classType)
    {
        this.read(classType, null);
    }

    /**
     * 配置的查询语句。
     * @param classType 资源所在位置。
     * @param namePrefix 文件名前缀。
     */
    public void read(Class<?> classType, String namePrefix)
    {
        if (classType == null || Object.class.equals(classType))
            return;

        read(classType.getSuperclass(), namePrefix);

        if (this.classType == null)
            this.classType = classType;
        if (ValueUtils.isEmpty(this.namePrefix))
            this.namePrefix = namePrefix;
        String resourceName = this.getNamePrefix(classType);
        new SqlsReader(classType, resourceName, this).read();
    }

    public void setClassType(Class<?> classType)
    {
        this.classType = classType;
    }

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        info.append(Sqls.class.getSimpleName());
        info.append(" : ");
        boolean isFirst = true;
        for (String key : this.sqlNameMap.keySet())
        {
            if (!isFirst)
                info.append(Environment.LINETAB_SEPARATOR);
            info.append(key + ": ");
            info.append(Environment.LINETAB_SEPARATOR);
            info.append(this.sqlNameMap.get(key));
            isFirst = false;
        }
        return info.toString();
    }
}

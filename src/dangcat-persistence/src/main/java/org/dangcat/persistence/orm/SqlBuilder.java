package org.dangcat.persistence.orm;

import org.apache.log4j.Logger;
import org.dangcat.commons.io.TextMarker;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.TableStatementHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 动态查询语句。
 * @author dangcat
 * 
 */
public class SqlBuilder implements java.io.Serializable
{
    public static final String DEFAULT_DELIMITER = ";";
    public static final String MACROS_BEGIN = "<%";
    public static final String MACROS_END = "%>";
    protected static final String[] PARAMS_FLAG = {":"};
    private static final String EMPTY = "";
    private static final String FILTER = "filter";
    public static final String FILTER_FLAG = "${" + FILTER + "}";
    private static final Logger logger = Logger.getLogger(SqlBuilder.class);
    private static final long serialVersionUID = 1L;
    private static final String TABLENAME = "tableName";
    public static final String TABLENAME_FLAG = "${" + TABLENAME + "}";

    /** 语句截止符。 */
    private String delimiter = DEFAULT_DELIMITER;
    /** 附加过滤条件。 */
    private String filter = null;
    /** 参数映射表。 */
    private Map<String, Object> params = null;
    /** 查询语句组合对象。 */
    private StringBuilder sqlBuilder = new StringBuilder();
    /** 表名。 */
    private String tableName = null;

    public SqlBuilder()
    {
    }

    public SqlBuilder(String sql)
    {
        this.sqlBuilder.append(sql);
    }

    /**
     * 添加批量语句。
     */
    public void addBatch()
    {
        this.sqlBuilder.append(this.getDelimiter());
    }

    /**
     * 添加批量语句，去掉空语句。
     */
    private void addBatch(List<String> batchSqlList, String sql)
    {
        if (sql != null)
        {
            for (int index = 0; index < sql.length(); index++)
            {
                char charValue = sql.charAt(index);
                if (Character.isLetterOrDigit(charValue))
                {
                    batchSqlList.add(sql);
                    break;
                }
            }
        }
    }

    /**
     * 添加批量语句。
     * @param subSqlExpress 表达子句。
     */
    public void addBatch(String subSqlExpress)
    {
        this.sqlBuilder.append(subSqlExpress);
        this.addBatch();
    }

    /**
     * 添加查询语句。
     * @param subSqlExpress 表达子句。
     */
    public void append(String subSqlExpress)
    {
        this.sqlBuilder.append(subSqlExpress);
    }

    /**
     * 清除所有语句。
     */
    public void clear()
    {
        this.sqlBuilder = new StringBuilder();
    }

    public List<String> getBatchSqlList()
    {
        String sql = this.toString();
        List<String> sqlBatchList = new ArrayList<String>();
        if (sql.indexOf(this.getDelimiter()) > 0)
        {
            for (String sqlExpress : this.split(sql))
            {
                if (!ValueUtils.isEmpty(sqlExpress))
                    sqlBatchList.add(sqlExpress);
            }
        }
        else if (!ValueUtils.isEmpty(sql))
            sqlBatchList.add(sql);
        return sqlBatchList;
    }

    public String getDelimiter()
    {
        return this.delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getFilter()
    {
        return this.filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Map<String, Object> getParams()
    {
        return this.params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getTableName()
    {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 判断当前位置是否是分割字符。
     * @param sql 原始语句。
     * @param beginIndex 起始位置。
     * @return 是否分割。
     */
    private boolean isDelimiter(String sql, int beginIndex)
    {
        String delimiter = this.getDelimiter();
        int endIndex = beginIndex + delimiter.length();
        if (endIndex < sql.length())
            return delimiter.equalsIgnoreCase(sql.substring(beginIndex, endIndex));
        return false;
    }

    private boolean isReplace(String paramName)
    {
        return paramName.startsWith(MACROS_BEGIN) && paramName.startsWith(MACROS_END);
    }

    /**
     * 返回语句长度。
     */
    public int length()
    {
        return this.sqlBuilder.length();
    }

    private String processSqlExpress(String sqlExpress)
    {
        if (!ValueUtils.isEmpty(sqlExpress))
        {
            try
            {
                TextMarker textMarker = new TextMarker();
                textMarker.setTemplate(sqlExpress);
                // 替换参数内容。
                if (this.params != null && this.params.size() > 0)
                {
                    for (String paramName : this.params.keySet())
                    {
                        boolean replace = this.isReplace(paramName);
                        if (replace)
                            paramName = paramName.replace(MACROS_BEGIN, EMPTY).replace(MACROS_END, EMPTY);

                        Object value = this.params.get(paramName);
                        if (value != null && !ReflectUtils.isConstClassType(value.getClass()))
                            textMarker.putData(paramName, replace ? value.toString() : value);
                        else
                            textMarker.putData(paramName, replace ? value : TableStatementHelper.toSqlString(value));
                    }
                }
                // 替换数据表名。
                textMarker.putData(TABLENAME, ValueUtils.isEmpty(this.tableName) ? EMPTY : this.tableName);
                // 替换过滤条件。
                textMarker.putData(FILTER, ValueUtils.isEmpty(this.filter) ? EMPTY : this.filter);
                sqlExpress = textMarker.process();
                sqlExpress = this.replaceParams(sqlExpress);
            }
            catch (IOException e)
            {
                logger.error(sqlExpress, e);
            }
        }
        return sqlExpress;
    }

    private String replaceParams(String sqlExpress)
    {
        if (this.params != null && this.params.size() > 0)
        {
            for (String paramName : this.params.keySet())
            {
                Object value = this.params.get(paramName);
                for (String paramFlag : PARAMS_FLAG)
                {
                    String param = paramFlag + paramName;
                    int index = sqlExpress.indexOf(param);
                    if (index != -1)
                    {
                        boolean isValid = index + param.length() == sqlExpress.length();
                        if (!isValid)
                        {
                            char charValue = sqlExpress.charAt(index + param.length());
                            isValid = !(Character.isLetterOrDigit(charValue) || charValue == '_');
                        }
                        if (isValid)
                            sqlExpress = sqlExpress.replace(paramFlag + paramName, TableStatementHelper.toSqlString(value));
                    }
                }
            }
        }
        return sqlExpress;
    }

    /**
     * 分割批量语句。
     * @param sql 原始语句。
     * @return 分割语句组。
     */
    private String[] split(String sql)
    {
        List<String> batchSqlList = new ArrayList<String>();
        StringBuilder sqlCache = new StringBuilder();
        boolean isInText = false;
        for (int index = 0; index < sql.length(); index++)
        {
            char charValue = sql.charAt(index);
            if (charValue == '\'')
                isInText = !isInText;
            if (!isInText && this.isDelimiter(sql, index))
            {
                this.addBatch(batchSqlList, sqlCache.toString());
                sqlCache = new StringBuilder();
                // 如果分割符是多字符需要跳过
                if (this.getDelimiter().length() > 1)
                    index += this.getDelimiter().length() - 1;
            }
            else
                sqlCache.append(charValue);
        }
        this.addBatch(batchSqlList, sqlCache.toString());
        return batchSqlList.toArray(new String[0]);
    }

    /**
     * 返回原始的表达语句。
     */
    public String toOrigString()
    {
        if (this.sqlBuilder != null)
            return this.sqlBuilder.toString();
        return null;
    }

    /**
     * 返回合成后的表达语句。
     */
    @Override
    public String toString()
    {
        String sqlExpress = this.sqlBuilder.toString();
        sqlExpress = this.processSqlExpress(sqlExpress);
        if (sqlExpress.indexOf(this.getDelimiter()) > 0)
            sqlExpress = sqlExpress.replaceAll(this.getDelimiter(), this.getDelimiter() + Environment.LINE_SEPARATOR);
        return sqlExpress;
    }
}

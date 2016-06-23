package org.dangcat.persistence.filter;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.TableStatementHelper;

class BetweenFilter implements Filter
{
    /**
     * 校验数值是否有效。
     * @param values 数值数组。
     * @param value 比较对象。
     * @return 是否有效。
     */
    @Override
    public boolean isValid(Object[] values, Object value)
    {
        boolean result = false;
        if (values != null)
        {
            Object from = values.length > 0 ? values[0] : null;
            Object to = values.length > 1 ? values[1] : null;
            if (from != null && to != null)
                result = ValueUtils.compare(value, from) >= 0 && ValueUtils.compare(value, to) <= 0;
            else if (from != null)
                result = ValueUtils.compare(value, from) >= 0;
            else if (to != null)
                result = ValueUtils.compare(value, to) <= 0;
        }
        return result;
    }

    /**
     * 转换成SQL表达式。
     * @param fieldName 字段名。
     * @param values 数值数组。
     * @return 表达式。
     */
    @Override
    public String toSql(String fieldName, Object[] values)
    {
        StringBuilder sql = new StringBuilder();
        if (values != null)
        {
            Object from = values.length > 0 ? values[0] : null;
            Object to = values.length > 1 ? values[1] : null;
            if (from != null && to != null)
            {
                sql.append(fieldName + " BETWEEN ");
                sql.append(TableStatementHelper.toSqlString(from));
                sql.append(" AND ");
                sql.append(TableStatementHelper.toSqlString(to));
            }
            else if (from != null)
            {
                sql.append(fieldName + " >= ");
                sql.append(TableStatementHelper.toSqlString(from));
            }
            else if (to != null)
            {
                sql.append(fieldName + " <= ");
                sql.append(TableStatementHelper.toSqlString(to));
            }
        }
        return sql.toString();
    }
}

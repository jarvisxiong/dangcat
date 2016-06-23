package org.dangcat.persistence.filter;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.TableStatementHelper;

public class GreaterEqualFilter implements Filter
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
            Object compareObject = null;
            for (Object valueObject : values)
            {
                if (compareObject == null || ValueUtils.compare(valueObject, compareObject) >= 0)
                    compareObject = valueObject;
            }
            if (compareObject != null)
                result = ValueUtils.compare(value, compareObject) >= 0;
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
            Object value = null;
            for (Object valueObject : values)
            {
                if (value == null || ValueUtils.compare(valueObject, value) >= 0)
                    value = valueObject;
            }
            if (value != null)
                sql.append(fieldName + " >= " + TableStatementHelper.toSqlString(value));
        }
        return sql.toString();
    }
}

package org.dangcat.persistence.filter;

import org.dangcat.persistence.model.TableStatementHelper;

class LikeFilter implements Filter {
    /**
     * 校验数值是否有效。
     *
     * @param values 数值数组。
     * @param value  比较对象。
     * @return 是否有效。
     */
    @Override
    public boolean isValid(Object[] values, Object value) {
        if (values != null && values.length > 0 && value instanceof String) {
            String compareObject = (String) value;
            for (Object valueObject : values) {
                if (valueObject instanceof String) {
                    if (compareObject.indexOf((String) valueObject) != -1)
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * 转换成SQL表达式。
     *
     * @param fieldName 字段名。
     * @param values    数值数组。
     * @return 表达式。
     */
    @Override
    public String toSql(String fieldName, Object[] values) {
        for (Object valueObject : values) {
            if (valueObject instanceof String) {
                String value = TableStatementHelper.toSqlString(valueObject);
                return fieldName + " LIKE " + value.substring(0, value.length() - 1) + "%'";
            }
        }
        return null;
    }
}

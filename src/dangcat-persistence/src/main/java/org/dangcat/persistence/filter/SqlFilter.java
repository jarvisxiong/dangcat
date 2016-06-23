package org.dangcat.persistence.filter;

class SqlFilter implements Filter {
    /**
     * 校验数值是否有效。
     *
     * @param values 数值数组。
     * @param value  比较对象。
     * @return 是否有效。
     */
    @Override
    public boolean isValid(Object[] values, Object value) {
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
            if (valueObject instanceof String)
                return (String) valueObject;
        }
        return null;
    }
}

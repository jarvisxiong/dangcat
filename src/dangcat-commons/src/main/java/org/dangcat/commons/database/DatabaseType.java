package org.dangcat.commons.database;

import org.dangcat.commons.utils.ValueUtils;

/**
 * 数据库类型。
 *
 * @author dangcat
 */
public enum DatabaseType {
    Default, Hsqldb, MySql, Oracle, SqlServer;

    public static DatabaseType parse(String value) {
        if (!ValueUtils.isEmpty(value)) {
            String text = value.toLowerCase();
            if (text.contains("hsql") || text.contains(Hsqldb.name().toLowerCase()))
                return Hsqldb;
            if (text.contains("sql server") || text.contains(SqlServer.name().toLowerCase()))
                return SqlServer;
            if (text.contains("mysql") || text.contains(MySql.name().toLowerCase()))
                return MySql;
            if (text.contains("oracle") || text.contains(Oracle.name().toLowerCase()))
                return Oracle;
        }
        return null;
    }
}

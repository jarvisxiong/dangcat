package org.dangcat.persistence.sql;

import org.dangcat.commons.database.DatabaseType;
import org.dangcat.commons.utils.ValueUtils;

/**
 * SQLµ¥Ôª¡£
 *
 * @author dangcat
 */
public class Sql {
    public static final String CREATE = "CREATE";
    public static final String DELETE = "DELETE";
    public static final String DROP = "DROP";
    public static final String EXECUTE = "EXECUTE";
    public static final String INSERT = "INSERT";
    public static final String QUERY = "QUERY";
    public static final String TOTALSIZE = "TOTALSIZE";
    public static final String UPDATE = "UPDATE";
    private DatabaseType databaseType = DatabaseType.Default;
    private String delimiter = null;
    private String name = QUERY;
    private String sql = null;

    public Sql() {
    }

    public Sql(org.dangcat.persistence.annotation.Sql sqlAnnotation) {
        this.databaseType = sqlAnnotation.databaseType();
        this.name = sqlAnnotation.name();
        this.sql = sqlAnnotation.value();
        this.delimiter = sqlAnnotation.delimiter();
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder();
        info.append(Sql.class.getSimpleName());
        info.append(" : ");
        info.append("DatabaseType = ");
        info.append(this.getDatabaseType());
        if (!ValueUtils.isEmpty(this.getName())) {
            info.append(", Name = ");
            info.append(this.getName());
        }
        if (!ValueUtils.isEmpty(this.getDelimiter())) {
            info.append(", Delimiter = ");
            info.append(this.getDelimiter());
        }
        info.append(", sql = ");
        info.append(sql);
        return info.toString();
    }
}

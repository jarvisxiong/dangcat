package org.dangcat.persistence.syntax;

import org.dangcat.commons.database.DatabaseType;
import org.dangcat.persistence.orm.SqlSyntaxHelper;

public class SqlSyntaxHelperFactory
{
    private static SqlSyntaxHelperFactory instance = new SqlSyntaxHelperFactory();

    private SqlSyntaxHelperFactory()
    {

    }

    public static SqlSyntaxHelperFactory getInstance()
    {
        return instance;
    }

    public SqlSyntaxHelper getSqlSyntaxHelper(DatabaseType databaseType)
    {
        if (DatabaseType.MySql.equals(databaseType))
            return new MysqlSyntaxAdapter();
        else if (DatabaseType.SqlServer.equals(databaseType))
            return new SqlServerSyntaxAdapter();
        else if (DatabaseType.Oracle.equals(databaseType))
            return new OracleSyntaxAdapter();
        else if (DatabaseType.Hsqldb.equals(databaseType))
            return new HsqldbSyntaxAdapter();
        return new StandSqlSyntaxHelper();
    }
}

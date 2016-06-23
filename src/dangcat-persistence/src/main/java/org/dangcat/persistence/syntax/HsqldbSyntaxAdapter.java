package org.dangcat.persistence.syntax;

/**
 * 表达式构建器。
 * @author dangcat
 * 
 */
public class HsqldbSyntaxAdapter extends StandSqlSyntaxHelper
{
    @Override
    protected String createCreateStatement(String tableName)
    {
        String statement = "CREATE ";
        if (this.isUseCachedTable())
            statement += "CACHED ";
        return statement + "TABLE " + tableName;
    }

    private boolean isUseCachedTable()
    {
        return this.containsParam("useCachedTable", "true");
    }
}

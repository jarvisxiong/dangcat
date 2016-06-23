package org.dangcat.commons.database;

public class SqlServerDatabase extends Database {
    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.SqlServer;
    }

    @Override
    public Integer getDefaultPort() {
        return super.getDefaultPort() == null ? 1422 : super.getDefaultPort();
    }

    @Override
    public String getDefaultUser() {
        return super.getDefaultUser() == null ? "sa" : super.getDefaultUser();
    }

    @Override
    public String getDriver() {
        if (this.isUsePoolDriver())
            return "com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource";
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    @Override
    public String getUrl() {
        return "jdbc:sqlserver://" + this.getServer() + ":" + this.getPort() + "/databaseName=" + this.getName();
    }
}

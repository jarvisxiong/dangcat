package org.dangcat.commons.database;

public class MySqlDatabase extends Database {
    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.MySql;
    }

    @Override
    public Integer getDefaultPort() {
        return super.getDefaultPort() == null ? 3306 : super.getDefaultPort();
    }

    @Override
    public String getDefaultUser() {
        return super.getDefaultUser() == null ? "root" : super.getDefaultUser();
    }

    @Override
    public String getDriver() {
        if (this.isUsePoolDriver())
            return "com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource";
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public String getUrl() {
        return "jdbc:mysql://" + this.getServer() + ":" + this.getPort() + "/" + this.getName();
    }
}

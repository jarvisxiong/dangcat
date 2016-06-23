package org.dangcat.commons.database;

public class OracleDatabase extends Database {
    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.Oracle;
    }

    @Override
    public Integer getDefaultPort() {
        return super.getDefaultPort() == null ? 1521 : super.getDefaultPort();
    }

    @Override
    public String getDefaultUser() {
        return super.getDefaultUser() == null ? "SYSTEM" : super.getDefaultUser();
    }

    @Override
    public String getDriver() {
        if (this.isUsePoolDriver())
            return "oracle.jdbc.pool.OracleConnectionPoolDataSource";
        return "oracle.jdbc.driver.OracleDriver";
    }

    @Override
    public String getUrl() {
        return "jdbc:oracle:thin:@" + this.getServer() + ":" + this.getPort() + ":" + this.getName();
    }
}

package org.dangcat.commons.database;

public class HsqldbDatabase extends Database
{
    private String dbFile = null;

    @Override
    public DatabaseType getDatabaseType()
    {
        return DatabaseType.Hsqldb;
    }

    public String getDbFile()
    {
        return this.dbFile;
    }

    public void setDbFile(String dbFile) {
        this.dbFile = dbFile;
    }

    @Override
    public String getDefaultUser()
    {
        return super.getDefaultUser() == null ? "sa" : super.getDefaultUser();
    }

    @Override
    public String getDriver()
    {
        return "org.hsqldb.jdbcDriver";
    }

    @Override
    public String getUrl()
    {
        if (this.dbFile != null)
            return "jdbc:hsqldb:file:/" + this.dbFile;
        return "jdbc:hsqldb:mem:" + this.getName();
    }
}

package org.dangcat.persistence;

import org.apache.log4j.Logger;
import org.dangcat.commons.database.DatabaseType;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.impl.PropertiesManager;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.orm.SessionFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

@Ignore
public abstract class TestDatabase
{
    protected static final Logger logger = Logger.getLogger(TestDatabase.class);
    private static final String TEST_DATABASES = "TEST_DATABASES";
    private static boolean hasInit = false;

    @BeforeClass
    public static void initialize()
    {
        if (!hasInit)
        {
            Environment.setHomePath(TestDatabase.class);
            if (logger.isDebugEnabled())
                logger.info(PropertiesManager.getInstance().toText());
            removeHsqldb();
            hasInit = true;
        }
    }

    private static void removeHsqldb()
    {
        File hsqlData = new File(Environment.getHomePath() + "/../log/HsqlData");
        if (!hsqlData.exists())
            hsqlData = new File(Environment.getHomePath() + "/log/HsqlData");
        FileUtils.delete(hsqlData);
    }

    protected boolean couldTestDatabase(DatabaseType databaseType, boolean defaultValue)
    {
        String value = System.getProperty(TEST_DATABASES);
        if (ValueUtils.isEmpty(value))
            value = System.getenv(TEST_DATABASES);
        if (ValueUtils.isEmpty(value) || !value.toLowerCase().contains(databaseType.name().toLowerCase()))
            return defaultValue;
        return true;
    }

    protected abstract void testDatabase(String databaseName) throws TableException, EntityException;

    @Test
    public void testDatabases() throws TableException, EntityException
    {
        SessionFactory.getInstance().close();
        if (this.couldTestDatabase(DatabaseType.SqlServer, false))
            this.testDatabase(DatabaseType.SqlServer.name());

        SessionFactory.getInstance().close();
        if (this.couldTestDatabase(DatabaseType.MySql, true))
            this.testDatabase(DatabaseType.MySql.name());

        SessionFactory.getInstance().close();
        if (this.couldTestDatabase(DatabaseType.Oracle, false))
            this.testDatabase(DatabaseType.Oracle.name());

        SessionFactory.getInstance().close();
        if (this.couldTestDatabase(DatabaseType.Hsqldb, false))
            this.testDatabase(DatabaseType.Hsqldb.name());
    }
}

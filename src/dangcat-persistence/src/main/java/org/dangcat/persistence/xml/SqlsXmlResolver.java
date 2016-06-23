package org.dangcat.persistence.xml;

import org.dangcat.commons.database.DatabaseType;
import org.dangcat.commons.serialize.xml.XmlResolver;
import org.dangcat.persistence.sql.Sql;
import org.dangcat.persistence.sql.Sqls;

/**
 * 栏位对象解析器。
 * @author dangcat
 * 
 */
public class SqlsXmlResolver extends XmlResolver
{
    private DatabaseType databaseType = null;
    private Sqls sqls = null;

    /**
     * 构建解析器。
     */
    public SqlsXmlResolver()
    {
        super(Sqls.class.getSimpleName());
        this.addChildXmlResolver(new SqlXmlResolver());
    }

    /**
     * 产生子元素对象。
     * @param elementName 子元素名称。
     * @param child 子元素对象。
     */
    @Override
    protected void afterChildCreate(String elementName, Object child)
    {
        if (child != null)
        {
            Sql sql = (Sql) child;
            if (this.databaseType != null)
                sql.setDatabaseType(this.databaseType);
            this.sqls.add(sql);
        }
    }

    public DatabaseType getDatabaseType()
    {
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType)
    {
        this.databaseType = databaseType;
    }

    @Override
    public void setResolveObject(Object resolveObject)
    {
        this.sqls = (Sqls) resolveObject;
        super.setResolveObject(resolveObject);
    }
}

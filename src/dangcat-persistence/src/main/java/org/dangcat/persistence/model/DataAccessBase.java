package org.dangcat.persistence.model;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.impl.ServiceInfo;

public abstract class DataAccessBase
{
    private Columns columns = new Columns();

    /**
     * 添加指定输入输出栏位。
     * @param fieldName 字段名。
     * @return 栏位对象。
     */
    public Column addColumn(String fieldName)
    {
        Column column = this.getTable().getColumns().find(fieldName);
        if (column != null && !this.columns.contains(column))
        {
            column = (Column) column.clone();
            this.columns.add(column);
        }
        return column;
    }

    public Columns getColumns()
    {
        if (this.columns.size() == 0)
            return this.getTable().getColumns();
        return this.columns;

    }

    protected abstract Table getTable();

    /**
     * 读取字段标题。
     * @param fieldName 字段名称。
     * @return 字段标题。
     */
    public String getTitle(String fieldName)
    {
        String title = null;
        ServiceContext serviceContext = ServiceContext.getInstance();
        if (serviceContext != null)
        {
            String resourceKey = this.getTable().getName() + "." + fieldName;
            ServiceInfo serviceInfo = serviceContext.getServiceInfo();
            title = serviceInfo.getResourceReader().getText(resourceKey);
        }
        if (ValueUtils.isEmpty(title))
        {
            Column column = this.getColumns().find(fieldName);
            if (column != null)
                title = column.getTitle();
        }
        return title;
    }

    public abstract int size();

}

package org.dangcat.persistence.event;

import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.model.Field;
import org.dangcat.persistence.model.Row;
import org.dangcat.persistence.model.Table;

/**
 * ±Ì ¬º˛  ≈‰∆˜°£
 * @author dangcat
 * 
 */
public abstract class TableEventAdapter
{
    public void afterCreate(Table table) throws TableException
    {
    }

    public void afterDelete(Table table) throws TableException
    {
    }

    public void afterDrop(Table table) throws TableException
    {
    }

    public void afterExecute(Table table) throws TableException
    {
    }

    public void afterLoad(Table table) throws TableException
    {
    }

    public void afterRowLoad(Row row)
    {
    }

    public void afterRowRemove(Row row)
    {
    }

    public void afterSave(Table table) throws TableException
    {
    }

    public void afterTruncate(Table table) throws TableException
    {
    }

    public boolean beforeCreate(Table table) throws TableException
    {
        return true;
    }

    public boolean beforeDelete(Table table) throws TableException
    {
        return true;
    }

    public boolean beforeDrop(Table table) throws TableException
    {
        return true;
    }

    public boolean beforeExecute(Table table) throws TableException
    {
        return true;
    }

    public boolean beforeLoad(Table table) throws TableException
    {
        return true;
    }

    public boolean beforeRowLoad(Row row)
    {
        return true;
    }

    public boolean beforeRowRemove(Row row)
    {
        return true;
    }

    public boolean beforeSave(Table table) throws TableException
    {
        return true;
    }

    public boolean beforeTruncate(Table table) throws TableException
    {
        return true;
    }

    public void onCalculateTotal(Row row)
    {
    }

    public void onCreateError(Table table, Exception exception)
    {
    }

    public void onCreateNewRow(Row row)
    {
    }

    public void onDeleteError(Table table, Exception exception)
    {
    }

    public void onDropError(Table table, Exception exception)
    {
    }

    public void onExecuteError(Table table, Exception exception)
    {
    }

    public void onFieldStateChanged(Row row, Field field)
    {
    }

    public void onLoadError(Table table, Exception exception)
    {
    }

    public void onLoadMetaData(Table table) throws TableException
    {
    }

    public void onRowStateChanged(Row row)
    {
    }

    public void onSaveError(Table table, Exception exception)
    {
    }

    public void onTableStateChanged(Table table)
    {
    }

    public void onTruncateError(Table table, Exception exception)
    {
    }
}

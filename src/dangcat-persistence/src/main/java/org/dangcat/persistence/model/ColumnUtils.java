package org.dangcat.persistence.model;

import org.dangcat.commons.formator.DataFormator;
import org.dangcat.commons.formator.DateFormator;
import org.dangcat.commons.utils.ValueUtils;

import java.util.Map;

class ColumnUtils
{
    protected static void assign(Column column, org.dangcat.persistence.annotation.Column columnAnnotation)
    {
        if (!ValueUtils.isEmpty(columnAnnotation.fieldName()))
            column.setFieldName(columnAnnotation.fieldName());
        column.setPrimaryKey(columnAnnotation.isPrimaryKey());
        column.setAutoIncrement(columnAnnotation.isAutoIncrement());
        column.setCalculate(columnAnnotation.isCalculate());
        column.setNullable(columnAnnotation.isNullable());
        column.setUnsigned(columnAnnotation.isUnsigned());
        column.setVisible(columnAnnotation.visible());
        column.setScale(columnAnnotation.scale());
        column.setSqlType(columnAnnotation.sqlType());
        if (!ValueUtils.isEmpty(columnAnnotation.format()))
            column.setFormat(columnAnnotation.format());
        if (!ValueUtils.isEmpty(columnAnnotation.title()))
            column.setTitle(columnAnnotation.title());
        column.setDisplaySize(columnAnnotation.displaySize());
        column.setGenerationType(columnAnnotation.generationType());
        column.setReadOnly(columnAnnotation.isReadonly());
        column.setAssociate(columnAnnotation.isAssociate());
        column.setIndex(columnAnnotation.index());
        column.setLogic(columnAnnotation.logic());
        column.setRowNum(column.getName().equalsIgnoreCase("rowNum"));
    }

    protected static void reset(Column column)
    {
        column.setPrimaryKey(false);
        column.setName(null);
        column.setFieldName(null);
        column.setAutoIncrement(false);
        column.setCalculate(false);
        column.setForeignKey(false);
        column.setFormat(null);
        column.setDataFormator(null);
        column.setNullable(true);
        column.setReadOnly(false);
        column.setAssociate(false);
        column.setTitle(null);
        column.setVisible(true);
        column.setUnsigned(false);
        column.setScale(0);
        column.setSqlType(0);
        column.setIndex(-1);
        column.setLogic(null);
        column.setDateType(null);
        column.setTableName(null);
    }

    /**
     * Êä³ö×Ö¶ÎÄÚÈÝ¡£
     */
    protected static String toString(Column column)
    {
        StringBuffer info = new StringBuffer();
        if (!ValueUtils.isEmpty(column.getName()))
            info.append("Name=" + column.getName() + "\t");
        if (!ValueUtils.isEmpty(column.getTitle()) && !column.getTitle().equalsIgnoreCase(column.getName()))
            info.append("Title=" + column.getTitle() + "\t");
        if (column.getFieldClass() != null)
            info.append("FieldClass=" + column.getFieldClass().getName() + "\t");
        if (column.getDisplaySize() != 0)
            info.append("DisplaySize=" + column.getDisplaySize() + "\t");
        if (column.isPrimaryKey())
            info.append("IsPrimaryKey=" + column.isPrimaryKey() + "\t");
        if (column.isForeignKey())
            info.append("IsForeignKey=" + column.isForeignKey() + "\t");
        if (column.isAutoIncrement())
            info.append("IsAutoIncrement=" + column.isAutoIncrement() + "\t");
        if (!column.isVisible())
            info.append("Visible=" + column.isVisible() + "\t");
        if (column.isCalculate())
            info.append("Calculate=" + column.isCalculate() + "\t");
        if (column.isReadOnly())
            info.append("ReadOnly=" + column.isReadOnly() + "\t");
        if (column.getScale() != 0 && column.isDoubleType())
            info.append("Scale=" + column.getScale() + "\t");
        if (column.getSqlType() != 0)
            info.append("SqlType=" + column.getSqlType() + "\t");
        if (column.getIndex() != -1)
            info.append("Index=" + column.getIndex() + "\t");
        if (!ValueUtils.isEmpty(column.getFormat()))
            info.append("Format=" + column.getFormat() + "\t");
        if (!ValueUtils.isEmpty(column.getLogic()))
            info.append("Logic=" + column.getLogic() + "\t");
        Map<String, Object> params = column.getParams();
        if (!params.isEmpty())
        {
            for (String paramName : params.keySet())
                info.append(paramName + "=" + params.get(paramName) + "\t");
        }
        return info.toString();
    }

    protected static String toString(Column column, Object value)
    {
        if (value != null)
        {
            DataFormator dataFormator = column.getDataFormator();
            if (dataFormator != null && dataFormator instanceof DateFormator)
                return dataFormator.format(value);
        }
        return ValueUtils.toString(value);
    }
}

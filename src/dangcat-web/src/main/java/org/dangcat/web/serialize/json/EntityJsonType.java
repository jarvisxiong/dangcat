package org.dangcat.web.serialize.json;

import java.sql.Timestamp;
import java.util.Date;

import org.dangcat.commons.formator.DateType;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.Column;

public class EntityJsonType
{
    private static final String DATATYPE_BOOLEAN = "boolean";
    private static final String DATATYPE_DATE = "date";
    private static final String DATATYPE_DATETIME = "datetime";
    private static final String DATATYPE_FLOAT = "float";
    private static final String DATATYPE_INTEGER = "integer";
    private static final String DATATYPE_STRING = "string";

    public static String getSerializeType(Column column)
    {
        Class<?> classType = column.getFieldClass();
        String type = null;
        if (ValueUtils.isText(classType))
            type = DATATYPE_STRING;
        else if (double.class.equals(classType) || Double.class.equals(classType) || float.class.equals(classType) || Float.class.equals(classType))
            type = DATATYPE_FLOAT;
        else if (int.class.equals(classType) || Integer.class.equals(classType) || short.class.equals(classType) || Short.class.equals(classType) || long.class.equals(classType)
                || Long.class.equals(classType))
            type = DATATYPE_INTEGER;
        else if (Timestamp.class.equals(classType) || Date.class.equals(classType))
        {
            if (DateType.Day.equals(column.getDateType()))
                type = DATATYPE_DATE;
            else
                type = DATATYPE_DATETIME;
        }
        else if (boolean.class.equals(classType) || Boolean.class.equals(classType))
            type = DATATYPE_BOOLEAN;
        return type;
    }
}

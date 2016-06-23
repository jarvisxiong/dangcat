package org.dangcat.web.serialize.json;

import com.google.gson.stream.JsonWriter;
import org.apache.log4j.Logger;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.serialize.json.JsonSerializer;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.entity.EntityField;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Columns;
import org.dangcat.persistence.validator.DataValidator;
import org.dangcat.persistence.validator.LogicValidator;
import org.dangcat.persistence.validator.impl.RangeValidator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 栏位对象序列化。
 * @author dangcat
 * 
 */
class ColumnJsonSerializer
{
    protected static final Logger logger = Logger.getLogger(ColumnJsonSerializer.class);
    private static final String AUTO_INCREMENT = "autoIncrement";
    private static final String CALCULATE = "calculate";
    private static final String COLUMNS = "columns";
    private static final String DATA_TYPE = "dataType";
    private static final String FIELD_PROPERTIES = "fieldProperties";
    private static final String FORMAT = "format";
    private static final String LENGTH = "length";
    private static final String LOGIC = "logic";
    private static final String MAX_VALUE = "maxValue";
    private static final String MIN_VALUE = "minValue";
    private static final String NAME = "name";
    private static final String NULLABLE = "nullable";
    private static final String PARAMS = "params";
    private static final String PRIMARY_KEY = "primaryKey";
    private static final String READONLY = "readOnly";
    private static final String SCALE = "scale";
    private static final String TITLE = "title";
    private static final String VALUE_MAP = "valueMap";
    private static final String VISIBLE = "visible";
    private Map<String, ColumnExtendInfo> columnExtendInfoMap = new HashMap<String, ColumnExtendInfo>();
    private EntityMetaData entityMetaData = null;

    ColumnJsonSerializer(EntityMetaData entityMetaData)
    {
        this.entityMetaData = entityMetaData;
    }

    private void createColumnExtendInfo() throws IOException
    {
        for (EntityField entityField : this.entityMetaData.getEntityFieldCollection())
        {
            ColumnExtendInfo columnExtendInfo = new ColumnExtendInfo(this.entityMetaData.getEntityClass(), entityField.getName());
            columnExtendInfo.create();
            if (columnExtendInfo.getColumnProperties().size() > 0 || columnExtendInfo.getFieldProperties().size() > 0)
                this.columnExtendInfoMap.put(entityField.getName(), columnExtendInfo);
        }
    }

    private void serializeColumn(JsonWriter jsonWriter, Column column) throws IOException
    {
        String name = ReflectUtils.toPropertyName(column.getName());
        JsonWriter propertiesWriter = jsonWriter.name(name);
        propertiesWriter.beginObject();
        // Name
        JsonSerializer.serialize(propertiesWriter, NAME, name);
        // Title
        String title = this.entityMetaData.getResourceReader().getText(column.getName());
        if (!ValueUtils.isEmpty(title) && ValueUtils.compare(column.getName(), title) != 0)
            JsonSerializer.serialize(propertiesWriter, TITLE, title);
        // type
        String dataType = EntityJsonType.getSerializeType(column);
        JsonSerializer.serialize(jsonWriter, DATA_TYPE, dataType);
        // Length
        if (column.getDisplaySize() > 0)
            JsonSerializer.serialize(propertiesWriter, LENGTH, column.getDisplaySize());
        // primaryKey
        if (column.isPrimaryKey())
            JsonSerializer.serialize(propertiesWriter, PRIMARY_KEY, true);
        // Calculate
        if (column.isCalculate())
            JsonSerializer.serialize(propertiesWriter, CALCULATE, true);
        // Format
        if (!ValueUtils.isEmpty(column.getFormat()))
            JsonSerializer.serialize(propertiesWriter, FORMAT, column.getFormat());
        // Logic
        if (!ValueUtils.isEmpty(column.getLogic()))
            JsonSerializer.serialize(propertiesWriter, LOGIC, column.getLogic());
        // ReadOnly
        if (column.isReadOnly())
            JsonSerializer.serialize(propertiesWriter, READONLY, true);
        // required
        if (!column.isNullable())
            JsonSerializer.serialize(propertiesWriter, NULLABLE, false);
        // autoIncrement
        if (column.isAutoIncrement())
            JsonSerializer.serialize(propertiesWriter, AUTO_INCREMENT, true);
        // visible
        if (column.isAutoIncrement() || !column.isVisible())
            JsonSerializer.serialize(propertiesWriter, VISIBLE, false);
        // Scale
        if (column.getScale() != 0 && column.isDoubleType())
            JsonSerializer.serialize(propertiesWriter, SCALE, column.getScale());
        // ValueMap
        Map<?, ?> valueMap = this.entityMetaData.getResourceReader().getValueMap(column.getName());
        if (valueMap != null)
            JsonSerializer.serialize(propertiesWriter, VALUE_MAP, valueMap);
        // DataValidator
        this.serializeDataValidator(propertiesWriter, column);
        // Params
        this.serializeParams(propertiesWriter, column);
        // ColumnExtendInfo
        this.serializeColumnExtendInfo(jsonWriter, column, columnExtendInfoMap);
        propertiesWriter.endObject();
    }

    private void serializeColumnExtendInfo(JsonWriter jsonWriter, Column column, Map<String, ColumnExtendInfo> columnExtendInfoMap) throws IOException
    {
        ColumnExtendInfo columnExtendInfo = columnExtendInfoMap.get(column.getName());
        if (columnExtendInfo != null)
        {
            if (columnExtendInfo.getColumnProperties().size() > 0)
            {
                for (Entry<String, Object> param : columnExtendInfo.getColumnProperties().entrySet())
                    JsonSerializer.serialize(jsonWriter, param.getKey(), param.getValue());
            }
            if (columnExtendInfo.getFieldProperties().size() > 0)
            {
                JsonWriter fieldPropertiesJsonWriter = jsonWriter.name(FIELD_PROPERTIES);
                fieldPropertiesJsonWriter.beginObject();
                for (Entry<String, Object> param : columnExtendInfo.getFieldProperties().entrySet())
                    JsonSerializer.serialize(fieldPropertiesJsonWriter, param.getKey(), param.getValue());
                fieldPropertiesJsonWriter.endObject();
            }
        }
    }

    protected void serializeColumns(JsonWriter jsonWriter, Columns columns) throws IOException
    {
        this.createColumnExtendInfo();

        JsonWriter columnsJsonWriter = jsonWriter.name(COLUMNS);
        columnsJsonWriter.beginObject();
        for (Column column : columns)
            this.serializeColumn(columnsJsonWriter, column);
        columnsJsonWriter.endObject();
    }

    private void serializeDataValidator(JsonWriter jsonWriter, Column column) throws IOException
    {
        DataValidator[] dataValidators = column.getDataValidators();
        if (dataValidators != null)
        {
            for (DataValidator dataValidator : dataValidators)
            {
                if (dataValidator instanceof RangeValidator)
                {
                    RangeValidator rangeValidator = (RangeValidator) dataValidator;
                    JsonSerializer.serialize(jsonWriter, MIN_VALUE, rangeValidator.getMinValue());
                    JsonSerializer.serialize(jsonWriter, MAX_VALUE, rangeValidator.getMaxValue());
                }
                if (dataValidator instanceof LogicValidator)
                {
                    LogicValidator logicValidator = (LogicValidator) dataValidator;
                    JsonSerializer.serialize(jsonWriter, LOGIC, logicValidator.getName());
                }
            }
        }
    }

    private void serializeParams(JsonWriter jsonWriter, Column column) throws IOException
    {
        if (column.getParams().size() > 0)
        {
            JsonWriter paramsJsonWriter = jsonWriter.name(PARAMS);
            paramsJsonWriter.beginObject();
            for (Entry<String, Object> param : column.getParams().entrySet())
                JsonSerializer.serialize(paramsJsonWriter, param.getKey(), param.getValue());
            paramsJsonWriter.endObject();
        }
    }
}
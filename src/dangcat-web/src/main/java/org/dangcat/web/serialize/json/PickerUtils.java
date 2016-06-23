package org.dangcat.web.serialize.json;

import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.model.Column;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickerUtils
{
    private static final String CAN_BE_UNKNOWN_VALUE = "canBeUnknownValue";
    private static final String COMBO_BOX_ITEM = "ComboBoxItem";
    private static final String DATA_TYPE = "dataType";
    private static final String DISPLAY_FIELD = "displayField";
    private static final String EDITOR_TYPE = "editorType";
    private static final String FIELD_PICKDTA_JNDINAME = "fieldPickDataJndiName";
    private static final String FILTER_FIELDS = "filterFields";
    private static final String FORMAT = "format";
    private static final String LENGTH = "length";
    private static final String LOGIC = "logic";
    private static final String NAME = "name";
    private static final String PICK_DATA = "pickData";
    private static final String PICK_LIST_CONSTRUCTOR = "pickListConstructor";
    private static final String PICK_LIST_FIELDS = "pickListFields";
    private static final String PICK_LIST_PROPERTIES = "pickListProperties";
    private static final String PICKER_ICON_SRC = "pickerIconSrc";
    private static final String TITLE = "title";
    private static final String TREE_DATA_GRID_PICKER = "TreeDataGridPicker";
    private static final String VALUE_FIELD = "valueField";
    private static final String VALUE_MAP = "valueMap";
    private static final String VISIBLE = "visible";

    private static void createColumnProperties(ColumnExtendInfo columnExtendInfo, org.dangcat.web.annotation.Picker pickerAnnotation)
    {
        Class<? extends PickerInfo> classType = pickerAnnotation.value();
        PickerInfo pickerInfo = (PickerInfo) ReflectUtils.newInstance(classType);
        if (pickerInfo != null)
        {
            Map<String, Object> columnProperties = columnExtendInfo.getColumnProperties();
            Map<String, Object> fieldProperties = columnExtendInfo.getFieldProperties();
            fieldProperties.put(CAN_BE_UNKNOWN_VALUE, pickerAnnotation.canBeUnknownValue());
            fieldProperties.put(EDITOR_TYPE, COMBO_BOX_ITEM);
            if (pickerInfo.isTree())
                fieldProperties.put(PICK_LIST_CONSTRUCTOR, TREE_DATA_GRID_PICKER);
            columnProperties.put(FIELD_PICKDTA_JNDINAME, pickerInfo.getJndiName());

            if (!ValueUtils.isEmpty(pickerAnnotation.valueField()))
                fieldProperties.put(VALUE_FIELD, ReflectUtils.toPropertyName(pickerAnnotation.valueField()));
            else if (!ValueUtils.isEmpty(pickerInfo.getValueField()))
                fieldProperties.put(VALUE_FIELD, ReflectUtils.toPropertyName(pickerInfo.getValueField()));

            if (!ValueUtils.isEmpty(pickerAnnotation.displayField()))
                fieldProperties.put(DISPLAY_FIELD, ReflectUtils.toPropertyName(pickerAnnotation.displayField()));
            else if (!ValueUtils.isEmpty(pickerInfo.getDisplayField()))
                fieldProperties.put(DISPLAY_FIELD, ReflectUtils.toPropertyName(pickerInfo.getDisplayField()));

            if (!ValueUtils.isEmpty(pickerInfo.getPickerIconSrc()))
                fieldProperties.put(PICKER_ICON_SRC, pickerInfo.getPickerIconSrc());

            String[] filterFields = pickerAnnotation.filterFields();
            if (filterFields == null || filterFields.length == 0)
                filterFields = pickerInfo.getFilterFields();
            if (filterFields != null && filterFields.length > 0)
            {
                for (int i = 0; i < filterFields.length; i++)
                    filterFields[i] = ReflectUtils.toPropertyName(filterFields[i]);
                fieldProperties.put(FILTER_FIELDS, filterFields);
            }

            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(pickerInfo.getResultClass());
            Object[] pickListFields = createPickListFields(entityMetaData, pickerInfo.getPickListFields());
            if (pickListFields != null && pickListFields.length > 0)
                fieldProperties.put(PICK_LIST_FIELDS, pickListFields);

            if (pickerInfo.getPickData() != null)
                fieldProperties.put(PICK_DATA, pickerInfo.getPickData());

            pickerInfo.createFieldProperties(fieldProperties);
            pickerInfo.createColumnProperties(columnProperties);
            Map<String, Object> pickListProperties = pickerInfo.getPickListProperties();
            if (pickListProperties != null && !pickListProperties.isEmpty())
                fieldProperties.put(PICK_LIST_PROPERTIES, pickListProperties);
        }
    }

    private static void createColumnProperties(ColumnExtendInfo columnExtendInfo, org.dangcat.web.annotation.PickList pickListAnnotation)
    {
        Map<String, Object> columnProperties = columnExtendInfo.getColumnProperties();
        Map<String, Object> fieldProperties = columnExtendInfo.getFieldProperties();
        fieldProperties.put(CAN_BE_UNKNOWN_VALUE, pickListAnnotation.canBeUnknownValue());
        fieldProperties.put(EDITOR_TYPE, COMBO_BOX_ITEM);
        columnProperties.put(FIELD_PICKDTA_JNDINAME, pickListAnnotation.jndiName());

        if (pickListAnnotation.isTree())
            fieldProperties.put(PICK_LIST_CONSTRUCTOR, TREE_DATA_GRID_PICKER);

        if (!ValueUtils.isEmpty(pickListAnnotation.valueField()))
            fieldProperties.put(VALUE_FIELD, ReflectUtils.toPropertyName(pickListAnnotation.valueField()));

        if (!ValueUtils.isEmpty(pickListAnnotation.displayField()))
            fieldProperties.put(DISPLAY_FIELD, ReflectUtils.toPropertyName(pickListAnnotation.displayField()));
        else
            fieldProperties.put(DISPLAY_FIELD, ReflectUtils.toPropertyName(pickListAnnotation.valueField()));

        if (!ValueUtils.isEmpty(pickListAnnotation.pickerIconSrc()))
            fieldProperties.put(PICKER_ICON_SRC, pickListAnnotation.pickerIconSrc());

        String[] filterFields = pickListAnnotation.filterFields();
        if (filterFields != null && filterFields.length > 0)
        {
            for (int i = 0; i < filterFields.length; i++)
                filterFields[i] = ReflectUtils.toPropertyName(filterFields[i]);
            fieldProperties.put(FILTER_FIELDS, filterFields);
        }

        if (pickListAnnotation.resultClass() != null)
        {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(pickListAnnotation.resultClass());
            Object[] pickListFields = createPickListFields(entityMetaData, entityMetaData.getTable().getColumns().toArray(new Column[0]));
            if (pickListFields != null && pickListFields.length > 0)
                fieldProperties.put(PICK_LIST_FIELDS, pickListFields);
            if (pickListAnnotation.pickData() != null)
            {
                Map<Integer, String> valueMap = entityMetaData.getResourceReader().getValueMap(pickListAnnotation.pickData());
                if (valueMap != null)
                    fieldProperties.put(PICK_DATA, valueMap);
            }
        }
    }

    public static void createPickList(ColumnExtendInfo columnExtendInfo)
    {
        org.dangcat.web.annotation.Picker pickerAnnotation = columnExtendInfo.getAnnotation(org.dangcat.web.annotation.Picker.class);
        if (pickerAnnotation != null && pickerAnnotation.value() != null)
            createColumnProperties(columnExtendInfo, pickerAnnotation);
        else
        {
            org.dangcat.web.annotation.PickList pickListAnnotation = columnExtendInfo.getAnnotation(org.dangcat.web.annotation.PickList.class);
            if (pickListAnnotation != null)
                createColumnProperties(columnExtendInfo, pickListAnnotation);
        }
    }

    private static Object[] createPickListFields(EntityMetaData entityMetaData, Column[] columns)
    {
        List<Object> columnList = new ArrayList<Object>();
        if (columns != null && columns.length > 0)
        {
            for (Column column : columns)
            {
                Map<String, Object> map = new HashMap<String, Object>();
                // Name
                String fieldName = ReflectUtils.toPropertyName(column.getName());
                map.put(NAME, fieldName);
                // Title
                String title = entityMetaData.getResourceReader().getText(column.getName());
                if (!ValueUtils.isEmpty(title))
                    map.put(TITLE, title);
                // type
                String dataType = EntityJsonType.getSerializeType(column);
                map.put(DATA_TYPE, dataType);
                // visible
                if (column.isAutoIncrement() || !column.isVisible())
                    map.put(VISIBLE, false);
                // Length
                if (column.getDisplaySize() > 0)
                    map.put(LENGTH, column.getDisplaySize());
                // Format
                if (!ValueUtils.isEmpty(column.getFormat()))
                    map.put(FORMAT, column.getFormat());
                // Logic
                if (!ValueUtils.isEmpty(column.getLogic()))
                    map.put(LOGIC, column.getLogic());
                // ValueMap
                Map<?, ?> valueMap = entityMetaData.getResourceReader().getValueMap(column.getName());
                if (valueMap != null)
                    map.put(VALUE_MAP, valueMap);
                columnList.add(map);
            }
        }
        return columnList.toArray();
    }
}

package org.dangcat.web.serialize.json;

import org.dangcat.commons.reflect.BeanUtils;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.model.DataState;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ColumnExtendInfo {
    private static final String DATA_STATE_READ_ONLY = "dataStateReadOnly";
    private static final String DATA_STATE_VISIBLE = "dataStateVisible";
    private static final String PROPERTY_ENCRYPT_ALGORITHM = "encryptAlgorithm";
    private static final String PROPERTY_PASSWORD = "password";
    private static final String PROPERTY_PERMISSION = "permission";
    private static final String PROPERTY_PERMISSION_READONLY = "permissionReadOnly";
    private static final String VALUE_MAP = "valueMap";
    private Class<?> classType = null;
    private Map<String, Object> columnProperties = new HashMap<String, Object>();
    private Map<String, Object> fieldProperties = new HashMap<String, Object>();
    private String name = null;

    ColumnExtendInfo(Class<?> classType, String name) {
        this.classType = classType;
        this.name = name;
    }

    protected void create() {
        this.createValueMap();
        this.createColumnProperties();
        this.createFieldProperties();
        this.createVisibleDataStates();
        this.createReadOnlyDataStates();
        this.createPassword();
        this.createPermission();
        PickerUtils.createPickList(this);
    }

    private void createColumnProperties() {
        org.dangcat.web.annotation.ColumnProperties columnPropertiesAnnotation = this.getAnnotation(org.dangcat.web.annotation.ColumnProperties.class);
        if (columnPropertiesAnnotation != null) {
            for (org.dangcat.web.annotation.ColumnProperty columnPropertyAnnotation : columnPropertiesAnnotation.value())
                this.createColumnProperty(columnPropertyAnnotation);
        } else
            this.createColumnProperty(this.getAnnotation(org.dangcat.web.annotation.ColumnProperty.class));
    }

    private void createColumnProperty(org.dangcat.web.annotation.ColumnProperty columnPropertyAnnotation) {
        if (columnPropertyAnnotation != null) {
            Object value = ValueUtils.parseValue(columnPropertyAnnotation.classType(), columnPropertyAnnotation.value());
            if (value != null)
                this.getColumnProperties().put(columnPropertyAnnotation.name(), value);
        }
    }

    private void createFieldProperties() {
        org.dangcat.web.annotation.FieldProperties fieldPropertiesAnnotation = this.getAnnotation(org.dangcat.web.annotation.FieldProperties.class);
        if (fieldPropertiesAnnotation != null) {
            for (org.dangcat.web.annotation.FieldProperty fieldPropertyAnnotation : fieldPropertiesAnnotation.value())
                this.createFieldProperty(fieldPropertyAnnotation);
        } else
            this.createFieldProperty(this.getAnnotation(org.dangcat.web.annotation.FieldProperty.class));
    }

    private void createFieldProperty(org.dangcat.web.annotation.FieldProperty fieldPropertyAnnotation) {
        if (fieldPropertyAnnotation != null) {
            Object value = ValueUtils.parseValue(fieldPropertyAnnotation.classType(), fieldPropertyAnnotation.value());
            if (value != null)
                this.getFieldProperties().put(fieldPropertyAnnotation.name(), value);
        }
    }

    private void createPassword() {
        org.dangcat.web.annotation.Password passwordAnnotation = this.getAnnotation(org.dangcat.web.annotation.Password.class);
        if (passwordAnnotation != null && !ValueUtils.isEmpty(passwordAnnotation.value())) {
            this.getColumnProperties().put(PROPERTY_PASSWORD, ReflectUtils.toPropertyName(passwordAnnotation.value()));
            if (!ValueUtils.isEmpty(passwordAnnotation.algorithm()))
                this.getColumnProperties().put(PROPERTY_ENCRYPT_ALGORITHM, passwordAnnotation.algorithm());
        }
    }

    private void createPermission() {
        org.dangcat.web.annotation.Permission permissionAnnotation = this.getAnnotation(org.dangcat.web.annotation.Permission.class);
        if (permissionAnnotation != null && !ValueUtils.isEmpty(permissionAnnotation.value())) {
            this.getColumnProperties().put(PROPERTY_PERMISSION, permissionAnnotation.value());
            if (permissionAnnotation.visible())
                this.getColumnProperties().put(PROPERTY_PERMISSION_READONLY, true);
        }
    }

    private void createReadOnlyDataStates() {
        List<String> readOnlyList = new ArrayList<String>();
        org.dangcat.web.annotation.ReadOnlyDataStates readOnlyDataStatesAnnotation = this.getAnnotation(org.dangcat.web.annotation.ReadOnlyDataStates.class);
        if (readOnlyDataStatesAnnotation != null) {
            for (DataState dataState : readOnlyDataStatesAnnotation.value())
                readOnlyList.add(dataState.name());
        } else {
            org.dangcat.web.annotation.EditDataStates editDataStatesAnnotation = this.getAnnotation(org.dangcat.web.annotation.EditDataStates.class);
            if (editDataStatesAnnotation != null) {
                for (DataState dataState : DataState.values())
                    readOnlyList.add(dataState.name());
                for (DataState dataState : editDataStatesAnnotation.value())
                    readOnlyList.remove(dataState.name());
            }
        }
        if (readOnlyList.size() > 0)
            this.getColumnProperties().put(DATA_STATE_READ_ONLY, readOnlyList.toArray(new String[0]));
    }

    private void createValueMap() {
        org.dangcat.web.annotation.ValueMap valueMapAnnotation = this.getAnnotation(org.dangcat.web.annotation.ValueMap.class);
        if (valueMapAnnotation != null) {
            if (!ValueUtils.isEmpty(valueMapAnnotation.jndiName())) {
                String key = (valueMapAnnotation.scopeAtRow() ? "row" : "field") + "ValueMapJndiName";
                this.getColumnProperties().put(key, valueMapAnnotation.jndiName());
            }
            if (!ValueUtils.isEmpty(valueMapAnnotation.resourceKey())) {
                EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(this.classType);
                Map<Integer, String> valueMap = entityMetaData.getResourceReader().getValueMap(valueMapAnnotation.resourceKey());
                if (valueMap != null)
                    this.getColumnProperties().put(VALUE_MAP, valueMap);
            }
        }
    }

    private void createVisibleDataStates() {
        List<String> visibleList = new ArrayList<String>();

        DataState[] dataStates = DataState.values();
        org.dangcat.web.annotation.VisibleDataStates visibleDataStatesStatesAnnotation = this.getAnnotation(org.dangcat.web.annotation.VisibleDataStates.class);
        if (visibleDataStatesStatesAnnotation != null)
            dataStates = visibleDataStatesStatesAnnotation.value();

        org.dangcat.web.annotation.HiddenDataStates hiddenDataStatesAnnotation = this.getAnnotation(org.dangcat.web.annotation.HiddenDataStates.class);
        if (hiddenDataStatesAnnotation != null || visibleDataStatesStatesAnnotation != null) {
            if (dataStates == null)
                dataStates = DataState.values();
            for (DataState dataState : dataStates)
                visibleList.add(dataState.name());
        }

        if (hiddenDataStatesAnnotation != null) {
            for (DataState dataState : hiddenDataStatesAnnotation.value())
                visibleList.remove(dataState.name());
        }
        if (visibleList.size() > 0)
            this.getColumnProperties().put(DATA_STATE_VISIBLE, visibleList.toArray(new String[0]));
    }

    protected <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return BeanUtils.getAnnotation(this.classType, this.name, annotationType);
    }

    protected Map<String, Object> getColumnProperties() {
        return columnProperties;
    }

    protected Map<String, Object> getFieldProperties() {
        return fieldProperties;
    }
}

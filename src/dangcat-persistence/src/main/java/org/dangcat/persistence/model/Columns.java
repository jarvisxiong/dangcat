package org.dangcat.persistence.model;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.validator.DataValidator;
import org.dangcat.persistence.validator.impl.MaxLengthValidator;
import org.dangcat.persistence.validator.impl.NotNullValidator;

import java.util.*;

/**
 * 栏位对象集合。
 *
 * @author dangcat
 */
public class Columns extends ArrayList<Column> implements java.io.Serializable {
    private static final String ROW_NUM = "RowNum";
    private static final long serialVersionUID = 1L;
    private Collection<DataValidator> dataValidators = null;
    private Map<String, String> fieldNameMap = null;
    private Map<String, Column> nameMap = null;
    private Table parent;
    private Column[] primaryKeys = null;
    private Column rowNumColumn = null;
    private Map<String, Column> titleMap = null;

    public Columns() {
    }

    public Columns(Table parent) {
        this.parent = parent;
    }

    public Column add(int index, String name, Class<?> fieldClass) {
        return this.add(index, name, fieldClass, 0, false);
    }

    public Column add(int index, String name, Class<?> fieldClass, boolean isPrimaryKey) {
        return this.add(index, name, fieldClass, 0, isPrimaryKey);
    }

    public Column add(int index, String name, Class<?> fieldClass, int displaySize) {
        return this.add(index, name, fieldClass, displaySize, false);
    }

    public Column add(int index, String name, Class<?> fieldClass, int displaySize, boolean isPrimaryKey) {
        Column column = new Column();
        column.initialize();
        column.setName(name);
        column.setFieldClass(fieldClass);
        column.setDisplaySize(displaySize);
        column.setPrimaryKey(isPrimaryKey);
        if (index == -1)
            this.add(column);
        else
            this.add(index, column);
        this.primaryKeys = null;
        return column;
    }

    public Column add(String name, Class<?> fieldClass) {
        return this.add(name, fieldClass, 0, false);
    }

    public Column add(String name, Class<?> fieldClass, boolean isPrimaryKey) {
        return this.add(name, fieldClass, 0, isPrimaryKey);
    }

    public Column add(String name, Class<?> fieldClass, int displaySize) {
        return this.add(name, fieldClass, displaySize, false);
    }

    public Column add(String name, Class<?> fieldClass, int displaySize, boolean isPrimaryKey) {
        return this.add(-1, name, fieldClass, displaySize, isPrimaryKey);
    }

    public void addDataValidator(DataValidator dataValidator) {
        if (this.dataValidators == null)
            this.createDataValidators(null);
        if (dataValidator != null) {
            Column findColumn = this.find(dataValidator.getColumn().getName());
            if (findColumn != null) {
                if (!this.dataValidators.contains(dataValidator))
                    this.dataValidators.add(dataValidator);
                findColumn.addDataValidator(dataValidator);
            }
        }
    }

    public void createDataValidators(Class<?> classType) {
        Collection<DataValidator> dataValidators = new LinkedList<DataValidator>();
        for (Column column : this) {
            // 不可为空校验
            if (!column.isNullable() && (!column.isAutoIncrement() || !column.getGenerationType().equals(GenerationType.IDENTITY))) {
                NotNullValidator notNullValidator = new NotNullValidator(classType, column);
                dataValidators.add(notNullValidator);
                column.addDataValidator(notNullValidator);
            }

            if (column.isCalculate())
                continue;

            // 最大长度校验
            if (ValueUtils.isText(column.getFieldClass()) && column.getDisplaySize() > 0) {
                MaxLengthValidator maxLengthValidator = new MaxLengthValidator(classType, column);
                dataValidators.add(maxLengthValidator);
                column.addDataValidator(maxLengthValidator);
            }
        }
        this.dataValidators = dataValidators;
    }

    private void createNameMap() {
        if (this.nameMap == null || this.nameMap.size() != this.size()) {
            Map<String, Column> titleMap = new HashMap<String, Column>();
            Map<String, String> fieldNameMap = new HashMap<String, String>();
            Map<String, Column> nameMap = new HashMap<String, Column>();
            for (Column column : this) {
                nameMap.put(column.getName(), column);
                String fieldName = column.getFieldName();
                if (column.getTableName() != null)
                    fieldName = column.getName();
                fieldNameMap.put(fieldName.toLowerCase(), column.getName());
                if (!ValueUtils.isEmpty(column.getTitle()))
                    titleMap.put(column.getTitle(), column);
            }
            this.nameMap = nameMap;
            this.titleMap = titleMap;
            this.fieldNameMap = fieldNameMap;
        }
    }

    public void createRowNumColumn() {
        this.rowNumColumn = this.add(0, ROW_NUM, Integer.class, 4, false);
        this.rowNumColumn.setRowNum(true);
    }

    public Column find(String name) {
        Column column = this.getNameMap().get(name);
        if (column == null)
            column = this.getTitleMap().get(name);
        return column;
    }

    public Column findByFieldName(String fieldName) {
        fieldName = fieldName.toLowerCase();
        String name = this.getFieldNameMap().get(fieldName);
        if (name != null)
            return this.find(name);
        return this.find(fieldName);
    }

    public DataValidator[] getDataValidators() {
        return this.dataValidators == null ? null : this.dataValidators.toArray(new DataValidator[0]);
    }

    private Map<String, String> getFieldNameMap() {
        this.createNameMap();
        return this.fieldNameMap;
    }

    private Map<String, Column> getNameMap() {
        this.createNameMap();
        return this.nameMap;
    }

    public Table getParent() {
        return this.parent;
    }

    public Column[] getPrimaryKeys() {
        if (this.primaryKeys == null) {
            List<Column> keyColumns = new ArrayList<Column>();
            for (Column column : this) {
                if (column.isPrimaryKey())
                    keyColumns.add(column);
            }
            this.primaryKeys = keyColumns.toArray(new Column[0]);
        }
        return this.primaryKeys;
    }

    public Column getRowNumColumn() {
        if (this.rowNumColumn == null) {
            for (Column column : this) {
                if (column.isRowNum()) {
                    this.rowNumColumn = column;
                    break;
                }
            }
        }
        return this.rowNumColumn;
    }

    private Map<String, Column> getTitleMap() {
        this.createNameMap();
        return this.titleMap;
    }

    public int indexOf(String fieldName) {
        Column column = this.find(fieldName);
        if (column == null)
            return -1;
        return this.indexOf(column);
    }

    public void sort() {
        Collections.sort(this, new Comparator<Column>() {
            public int compare(Column srcColumn, Column dstColumn) {
                if (srcColumn.getIndex() == -1 || dstColumn.getIndex() == -1)
                    return dstColumn.getIndex() - srcColumn.getIndex();
                return srcColumn.getIndex() - dstColumn.getIndex();
            }
        });
    }

    @Override
    public String toString() {
        StringBuffer info = new StringBuffer();
        info.append("Columns Size: " + this.size() + "\n");
        for (Column column : this)
            info.append(column + "\n");
        return info.toString();
    }
}

package org.dangcat.persistence.entity;

import org.apache.log4j.Logger;
import org.dangcat.commons.serialize.annotation.Serialize;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.exception.ServiceInformation;
import org.dangcat.framework.service.ServiceFeedback;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.model.DataState;
import org.dangcat.persistence.model.DataStatus;
import org.dangcat.persistence.model.TableUtils;
import org.dangcat.persistence.tablename.DynamicTable;
import org.dangcat.persistence.tablename.TableName;

/**
 * 业务实体基础。
 *
 * @author dangcat
 */
public abstract class EntityBase extends ServiceFeedback implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    protected transient Logger logger = Logger.getLogger(this.getClass());
    /**
     * 实体数据状态。
     */
    private DataState dataState = null;
    @Column(index = 0, isCalculate = true, displaySize = 4)
    private transient Integer rowNum = null;
    private transient TableName tableName = null;

    public EntityBase() {
        if (this instanceof DataStatus)
            this.dataState = DataState.Insert;
    }

    private boolean containsNull(Object[] values) {
        if (values != null) {
            for (Object value : values) {
                if (value == null)
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            if (obj == this)
                return true;

            if (!this.getClass().equals(obj.getClass()))
                return false;

            if (!this.getTableName().equals(((EntityBase) obj).getTableName()))
                return false;

            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(this.getClass());
            if (entityMetaData != null) {
                Object[] srcPrimaryValues = entityMetaData.getPrimaryKeyValues(this);
                if (!this.containsNull(srcPrimaryValues)) {
                    Object[] dstPrimaryValues = entityMetaData.getPrimaryKeyValues(obj);
                    if (!this.containsNull(dstPrimaryValues)) {
                        if (srcPrimaryValues != null && dstPrimaryValues != null && dstPrimaryValues.length == srcPrimaryValues.length) {
                            for (int i = 0; i < srcPrimaryValues.length; i++) {
                                if (ValueUtils.compare(srcPrimaryValues[i], dstPrimaryValues[i]) != 0)
                                    return false;
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return super.equals(obj);
    }

    @Override
    public ServiceException findServiceException(Integer messageId) {
        return EntityFeedback.findServiceException(this, messageId);
    }

    @Override
    public ServiceInformation findServiceInformation(Integer messageId) {
        return EntityFeedback.findServiceInformation(this, messageId);
    }

    @Serialize(ignoreValue = "Browse")
    public DataState getDataState() {
        if (this.dataState == null && !(this instanceof DataStatus))
            return EntityUtils.checkDataState(this);
        return this.dataState;
    }

    public void setDataState(DataState dataState) {
        this.dataState = dataState;
    }

    public Integer getRowNum() {
        return this.rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    @Serialize(ignore = true)
    public String getTableName() {
        if (this.tableName == null)
            this.tableName = TableUtils.getTableName(this);
        if (this.tableName instanceof DynamicTable)
            return ((DynamicTable) this.tableName).getName(this);
        return this.tableName.getName();
    }

    @Override
    public boolean hasError() {
        return EntityFeedback.hasError(this);
    }

    @Override
    public int hashCode() {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(this.getClass());
        if (entityMetaData != null) {
            Object[] srcPrimaryValues = entityMetaData.getPrimaryKeyValues(this);
            if (srcPrimaryValues != null && !this.containsNull(srcPrimaryValues)) {
                final int prime = 31;
                int result = this.getClass().hashCode();
                result = prime * result + (this.getTableName() == null ? 0 : this.getTableName().hashCode());
                for (int i = 0; i < srcPrimaryValues.length; i++)
                    result = prime * result + (srcPrimaryValues[i] == null ? 0 : srcPrimaryValues[i].hashCode());
                return result;
            }
        }
        return super.hashCode();
    }

    @Override
    public boolean hasInformation() {
        return EntityFeedback.hasInformation(this);
    }

    @Override
    public String toString() {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(this.getClass());
        if (entityMetaData == null)
            return super.toString();

        StringBuilder info = new StringBuilder();
        info.append(this.getClass().getSimpleName() + ": ");
        boolean isFirst = true;
        for (EntityField entityField : entityMetaData.getEntityFieldCollection()) {
            org.dangcat.persistence.model.Column column = entityField.getColumn();
            if (!isFirst)
                info.append(", ");
            info.append(column.getName());
            info.append(" = ");
            info.append(column.toString(entityField.getValue(this)));
            isFirst = false;
        }
        return info.toString();
    }
}

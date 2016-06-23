package org.dangcat.business.config;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.conf.ConfigBase;
import org.dangcat.framework.conf.ConfigValue;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.entity.EntityField;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;

import java.util.Collection;

/**
 * “µŒÒ≈‰÷√°£
 *
 * @author dangcat
 */
public abstract class BusinessConfig extends ConfigBase {
    public BusinessConfig(String name) {
        super(name);
        this.initlialize();
    }

    public abstract EntityBase createDefaultEntity();

    @Override
    protected ConfigValue getConfigValue(String configName) {
        BusinessConfigManager.getInstance();
        return super.getConfigValue(configName);
    }

    public EntityBase getCurrentEntity() {
        EntityBase entity = this.createDefaultEntity();
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity);
        if (entityMetaData != null) {
            for (EntityField entityField : entityMetaData.getEntityFieldCollection()) {
                Object value = this.getCurrentValue(entityField.getFieldName());
                entityField.setValue(entity, value);
            }
        }
        return entity;
    }

    private void initlialize() {
        Object entity = this.createDefaultEntity();
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity);
        if (entityMetaData != null) {
            for (EntityField entityField : entityMetaData.getEntityFieldCollection()) {
                String name = entityField.getFieldName();
                Object value = entityField.getValue(entity);
                this.addConfigValue(name, entityField.getClassType(), value);
            }
        }
    }

    public void load(Collection<BusinessSetup> systemConfigCollection) {
        for (BusinessSetup businessSetup : systemConfigCollection) {
            if (!this.getName().equals(businessSetup.getName()))
                continue;

            ConfigValue configValue = this.getConfigValue(businessSetup.getConfigName());
            if (configValue != null)
                configValue.setConfigValue(businessSetup.getValue());
        }
    }

    public void save(Object entity) throws ServiceException {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity);
        if (entityMetaData != null) {
            for (EntityField entityField : entityMetaData.getEntityFieldCollection()) {
                String name = entityField.getFieldName();
                Object value = entityField.getValue(entity);
                ConfigValue configValue = this.getConfigValue(name);
                if (configValue != null)
                    configValue.setConfigValue(ValueUtils.toString(value));
            }
        }
        BusinessConfigManager.getInstance().save(this);
    }

    public abstract void validate(EntityBase entity) throws ServiceException;
}

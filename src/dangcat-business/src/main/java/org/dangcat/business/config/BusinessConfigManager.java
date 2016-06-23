package org.dangcat.business.config;

import org.dangcat.framework.conf.ConfigValue;
import org.dangcat.framework.service.impl.ServiceFactory;
import org.dangcat.framework.service.impl.ServiceInfo;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityManager;
import org.dangcat.persistence.entity.EntityManagerFactory;
import org.dangcat.persistence.model.Table;

import java.util.*;

/**
 * 业务配置服务。
 *
 * @author dangcat
 */
public class BusinessConfigManager {
    private static BusinessConfigManager instance = null;
    private Map<String, BusinessConfig> businessConfigMap = new HashMap<String, BusinessConfig>();

    private BusinessConfigManager() {
    }

    public static BusinessConfigManager getInstance() {
        if (instance == null) {
            synchronized (BusinessConfigManager.class) {
                if (instance == null) {
                    instance = new BusinessConfigManager();
                    instance.initialize();
                }
            }
        }
        return instance;
    }

    private void initialize() {
        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        Collection<String> jndiNames = serviceFactory.getJndiNames(false);
        if (jndiNames != null) {
            for (String jndiName : jndiNames) {
                ServiceInfo serviceInfo = serviceFactory.getServiceInfo(jndiName);
                if (serviceInfo != null && serviceInfo.getConfigProvider() instanceof BusinessConfig) {
                    BusinessConfig businessConfig = (BusinessConfig) serviceInfo.getConfigProvider();
                    this.businessConfigMap.put(businessConfig.getName(), businessConfig);
                }
            }
        }
        this.load();
    }

    /**
     * 载入配置。
     */
    public void load() {
        Table systemConfigTable = EntityHelper.getEntityMetaData(BusinessSetup.class).getTable();
        if (systemConfigTable.exists()) {
            EntityManager entityManager = EntityManagerFactory.getInstance().open();
            List<BusinessSetup> businessSetupList = entityManager.load(BusinessSetup.class);
            if (businessSetupList != null && businessSetupList.size() > 0) {
                for (BusinessConfig businessConfig : this.businessConfigMap.values())
                    businessConfig.load(businessSetupList);
            }
        }
    }

    private void read(Collection<BusinessSetup> businessSetupCollection, BusinessConfig businessConfig) {
        for (ConfigValue configValue : businessConfig.getConfigValueMap().values()) {
            if (!configValue.isDefaultValue()) {
                BusinessSetup businessSetup = new BusinessSetup();
                businessSetup.setName(businessConfig.getName());
                businessSetup.setConfigName(configValue.getName());
                businessSetup.setValue(configValue.getConfigValue());
                businessSetupCollection.add(businessSetup);
            }
        }
    }

    /**
     * 保存业务配置。
     *
     * @param businessConfig
     */
    public void save(BusinessConfig businessConfig) {
        Collection<BusinessSetup> businessSetupCollection = new HashSet<BusinessSetup>();
        this.read(businessSetupCollection, businessConfig);
        Table businessSetupTable = EntityHelper.getEntityMetaData(BusinessSetup.class).getTable();
        if (!businessSetupTable.exists())
            businessSetupTable.create();

        EntityManager entityManager = EntityManagerFactory.getInstance().open();
        entityManager.delete(BusinessSetup.class, new String[]{BusinessSetup.Name}, businessConfig.getName());
        if (businessSetupCollection.size() > 0)
            entityManager.save(businessSetupCollection.toArray());
    }
}
